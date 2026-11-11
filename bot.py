import os
import sqlite3
import secrets
from telegram import InlineKeyboardButton, InlineKeyboardMarkup, Update
from telegram.ext import (
    Application, CommandHandler, CallbackQueryHandler,
    ContextTypes
)

CHANNEL = "@StargramNews"
DB = "stargram_auth.db"

def read_token():
    with open(".env", "r") as f:
        for line in f:
            if line.startswith("BOT_TOKEN="):
                return line.split("=", 1)[1].strip().strip('"').strip("'")
    return ""

TOKEN = read_token()

if not TOKEN:
    raise RuntimeError("BOT_TOKEN не найден в .env")

def init_db():
    with sqlite3.connect(DB) as con:
        con.execute("""
            CREATE TABLE IF NOT EXISTS users (
                user_id INTEGER PRIMARY KEY,
                virtual_number TEXT UNIQUE NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """)
        con.execute("""
            CREATE TABLE IF NOT EXISTS virtual_numbers (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                virtual_number TEXT UNIQUE NOT NULL,
                number_type TEXT NOT NULL
                    CHECK(number_type IN ('free', 'premium')),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                premium_level INTEGER,
                FOREIGN KEY(user_id) REFERENCES users(user_id)
            )
        """)

def get_or_create_number(user_id):
    with sqlite3.connect(DB) as con:
        row = con.execute(
            "SELECT virtual_number FROM virtual_numbers "
            "WHERE user_id = ? AND number_type = 'free' "
            "AND virtual_number LIKE '+7 900 %' ORDER BY id LIMIT 1",
            (user_id,)
        ).fetchone()

        if row:
            return row[0]

        row = con.execute(
            "SELECT virtual_number FROM users WHERE user_id = ?",
            (user_id,)
        ).fetchone()

        if row and row[0].startswith("+7 900 "):
            number = row[0]
            con.execute(
                "INSERT OR IGNORE INTO virtual_numbers "
                "(user_id, virtual_number, number_type) "
                "VALUES (?, ?, 'free')",
                (user_id, number)
            )
            return number

        while True:
            digits = f"{secrets.randbelow(10**7):07d}"
            number = f"+7 900 {digits[:3]} {digits[3:]}"

            exists = con.execute(
                "SELECT 1 FROM users WHERE virtual_number = ? "
                "UNION SELECT 1 FROM virtual_numbers WHERE virtual_number = ?",
                (number, number)
            ).fetchone()

            if exists:
                continue

            if row:
                con.execute(
                    "UPDATE users SET virtual_number = ? WHERE user_id = ?",
                    (number, user_id)
                )
            else:
                con.execute(
                    "INSERT INTO users (user_id, virtual_number) VALUES (?, ?)",
                    (user_id, number)
                )

            con.execute(
                "INSERT INTO virtual_numbers "
                "(user_id, virtual_number, number_type) "
                "VALUES (?, ?, 'free')",
                (user_id, number)
            )
            return number

def keyboard():
    return InlineKeyboardMarkup([
        [InlineKeyboardButton(
            "📢 Перейти в канал",
            url="https://t.me/StargramNews"
        )],
        [InlineKeyboardButton(
            "🔄 Проверить подписку",
            callback_data="check_subscription"
        )]
    ])

async def start(update: Update, context: ContextTypes.DEFAULT_TYPE):
    await update.message.reply_text(
        "🚀 Добро пожаловать в STARGRAM!\n\n"
        "Чтобы получить свой виртуальный номер STARGRAM, "
        "вступите в наш канал и нажмите «Проверить подписку».\n\n"
        "📢 Канал: @StargramNews",
        reply_markup=keyboard()
    )

async def check_subscription(
    update: Update,
    context: ContextTypes.DEFAULT_TYPE
):
    query = update.callback_query
    await query.answer()

    try:
        member = await context.bot.get_chat_member(
            chat_id=CHANNEL,
            user_id=query.from_user.id
        )

        subscribed = (
            member.status in ("member", "administrator", "creator")
            or (
                member.status == "restricted"
                and getattr(member, "is_member", False)
            )
        )

        if not subscribed:
            await query.edit_message_text(
                "❌ Подписка пока не найдена.\n\n"
                "Вступите в канал STARGRAM и проверьте ещё раз.",
                reply_markup=keyboard()
            )
            return

        number = get_or_create_number(query.from_user.id)

        await query.edit_message_text(
            "✅ Подписка подтверждена!\n\n"
            "🎉 Ваш постоянный виртуальный номер STARGRAM:\n\n"
            f"{number}\n\n"
            "Сохраните его. Он закреплён за вашим аккаунтом навсегда.\n\n"
            "Это внутренний идентификатор STARGRAM, "
            "он не принимает обычные SMS и звонки."
        )

    except Exception:
        await query.message.reply_text(
            "Не удалось проверить подписку. "
            "Попробуйте позже или проверьте права бота в канале."
        )

def main():
    init_db()
    app = Application.builder().token(TOKEN).build()
    app.add_handler(CommandHandler("start", start))
    app.add_handler(
        CallbackQueryHandler(
            check_subscription,
            pattern="^check_subscription$"
        )
    )
    print("STARGRAM AUTH запущен.")
    app.run_polling()

if __name__ == "__main__":
    main()
