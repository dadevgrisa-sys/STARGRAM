import os
import json
import time
import hmac
import hashlib
import secrets
import sqlite3
import urllib.request

from flask import Flask, request, jsonify

DB = "stargram_auth.db"
app = Flask(__name__)
app.config["MAX_CONTENT_LENGTH"] = 2048


def read_env():
    values = {}
    if os.path.exists(".env"):
        with open(".env", encoding="utf-8") as f:
            for line in f:
                if "=" in line and not line.strip().startswith("#"):
                    key, value = line.strip().split("=", 1)
                    values[key] = value.strip().strip('"').strip("'")
    return values


env = read_env()
BOT_TOKEN = env.get("BOT_TOKEN", "")
AUTH_SECRET = env.get("AUTH_SECRET", "")

if not BOT_TOKEN:
    raise RuntimeError("BOT_TOKEN не найден в .env")

if not AUTH_SECRET:
    AUTH_SECRET = secrets.token_hex(32)
    with open(".env", "a", encoding="utf-8") as f:
        f.write("\nAUTH_SECRET=" + AUTH_SECRET + "\n")
    try:
        os.chmod(".env", 0o600)
    except OSError:
        pass



def normalize_number(value):
    value = str(value or "").strip()
    compact = "".join(ch for ch in value if ch.isdigit() or ch == "+")

    if compact.startswith("+7900") and len(compact) == 12:
        digits = compact[2:]
        return "+7 900 " + digits[3:6] + " " + digits[6:10]

    if compact.startswith("+888"):
        digits = compact[4:]
        if len(digits) == 10:
            return "+888 " + digits[:3] + " " + digits[3:6] + " " + digits[6:]
        if len(digits) == 6:
            return "+888 " + digits[:3] + " " + digits[3:]
        if len(digits) == 3:
            return "+888 " + digits

    return None


def get_db():
    con = sqlite3.connect(DB)
    con.row_factory = sqlite3.Row
    return con


def code_digest(code):
    return hmac.new(
        AUTH_SECRET.encode(),
        code.encode(),
        hashlib.sha256
    ).hexdigest()


def send_telegram(chat_id, code):
    url = f"https://api.telegram.org/bot{BOT_TOKEN}/sendMessage"
    body = json.dumps({
        "chat_id": chat_id,
        "text": (
            "🔐 Код входа в STARGRAM:\n\n"
            f"{code}\n\n"
            "Код действует 5 минут. "
            "Никому его не сообщайте."
        )
    }).encode()

    req = urllib.request.Request(
        url,
        data=body,
        headers={"Content-Type": "application/json"}
    )

    with urllib.request.urlopen(req, timeout=15) as response:
        result = json.loads(response.read().decode())

    if not result.get("ok"):
        raise RuntimeError("Telegram не принял сообщение")


with get_db() as con:
    con.execute("""
        CREATE TABLE IF NOT EXISTS auth_sessions (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            account_id TEXT NOT NULL,
            token_hash TEXT UNIQUE NOT NULL,
            expires_at INTEGER NOT NULL,
            created_at INTEGER NOT NULL,
            FOREIGN KEY(account_id)
                REFERENCES stargram_accounts(account_id)
        )
    """)


@app.post("/auth/request")
def request_code():
    data = request.get_json(silent=True) or {}
    number = normalize_number(data.get("number", ""))

    if not number:
        return jsonify(error="Некорректный запрос"), 400

    now = int(time.time())

    with get_db() as con:
        owner = con.execute("""
            SELECT account_id, telegram_user_id
            FROM account_numbers
            WHERE virtual_number = ?
        """, (number,)).fetchone()

        if not owner:
            return jsonify(
                message="Если номер зарегистрирован, код будет отправлен."
            ), 200

        count_number = con.execute("""
            SELECT COUNT(*) FROM login_codes
            WHERE virtual_number = ? AND created_at > ?
        """, (number, now - 900)).fetchone()[0]

        count_user = con.execute("""
            SELECT COUNT(*)
            FROM login_codes lc
            JOIN account_numbers an
              ON an.virtual_number = lc.virtual_number
            WHERE an.telegram_user_id = ?
              AND lc.created_at > ?
        """, (owner["telegram_user_id"], now - 900)).fetchone()[0]

        if count_number >= 3 or count_user >= 5:
            return jsonify(error="Слишком много запросов. Попробуйте позже."), 429

        code = f"{secrets.randbelow(1_000_000):06d}"

        con.execute("""
            INSERT INTO login_codes
            (virtual_number, code_hash, expires_at, attempts, created_at)
            VALUES (?, ?, ?, 0, ?)
        """, (number, code_digest(code), now + 300, now))

        try:
            send_telegram(owner["telegram_user_id"], code)
        except Exception:
            con.execute("""
                UPDATE login_codes SET used_at = ?
                WHERE virtual_number = ?
                  AND created_at = ?
            """, (now, number, now))
            return jsonify(error="Не удалось доставить код. Попробуйте позже."), 503

    return jsonify(
        message="Если номер зарегистрирован, код отправлен владельцу."
    ), 200


@app.post("/auth/verify")
def verify_code():
    data = request.get_json(silent=True) or {}
    number = normalize_number(data.get("number", ""))
    code = str(data.get("code", "")).strip()

    if not number or len(code) != 6 or not code.isdigit():
        return jsonify(error="Неверный номер или код"), 400

    now = int(time.time())

    with get_db() as con:
        row = con.execute("""
            SELECT id, code_hash, expires_at, attempts
            FROM login_codes
            WHERE virtual_number = ?
              AND used_at IS NULL
            ORDER BY id DESC LIMIT 1
        """, (number,)).fetchone()

        if not row or row["expires_at"] < now or row["attempts"] >= 5:
            return jsonify(error="Код недействителен или истёк"), 401

        if not hmac.compare_digest(
            row["code_hash"], code_digest(code)
        ):
            con.execute("""
                UPDATE login_codes SET attempts = attempts + 1
                WHERE id = ?
            """, (row["id"],))
            return jsonify(error="Неверный код"), 401

        owner = con.execute("""
            SELECT account_id FROM account_numbers
            WHERE virtual_number = ?
        """, (number,)).fetchone()

        if not owner:
            return jsonify(error="Аккаунт не найден"), 404

        con.execute("""
            UPDATE login_codes SET used_at = ?
            WHERE id = ? AND used_at IS NULL
        """, (now, row["id"]))

        session_token = secrets.token_urlsafe(32)
        token_hash = hashlib.sha256(
            session_token.encode()
        ).hexdigest()

        con.execute("""
            INSERT INTO auth_sessions
            (account_id, token_hash, expires_at, created_at)
            VALUES (?, ?, ?, ?)
        """, (owner["account_id"], token_hash, now + 30 * 86400, now))

    return jsonify(
        account_id=owner["account_id"],
        session_token=session_token,
        expires_in=30 * 86400
    ), 200


@app.get("/health")
def health():
    return jsonify(status="ok", service="STARGRAM Auth API")


if __name__ == "__main__":
    print("STARGRAM Auth API готов.")
    app.run(host="127.0.0.1", port=5000, debug=False)
