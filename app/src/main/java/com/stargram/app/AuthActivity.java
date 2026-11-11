package com.stargram.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthActivity extends Activity {

    private final int bg = Color.rgb(24, 24, 24);
    private final int white = Color.WHITE;
    private final int gray = Color.rgb(155, 155, 155);
    private final int blue = Color.rgb(82, 115, 245);

    private final String API = "http://127.0.0.1:5000";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private String selectedCode = "+7";
    private String currentNumber = "";

    private EditText numberInput;
    private EditText codeInput;
    private TextView country;
    private TextView status;
    private TextView timerText;
    private Button continueButton;

    private CountDownTimer timer;
    private boolean verifyInProgress = false;
    private boolean requestInProgress = false;

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable shape(int color, int stroke, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        if (stroke != 0) d.setStroke(dp(1), stroke);
        return d;
    }

    private TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        return t;
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)
            getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private JSONObject post(String path, JSONObject data) throws Exception {
        HttpURLConnection connection = (HttpURLConnection)
            new URL(API + path).openConnection();

        connection.setRequestMethod("POST");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(15000);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        byte[] bytes = data.toString().getBytes(StandardCharsets.UTF_8);

        try (OutputStream out = connection.getOutputStream()) {
            out.write(bytes);
        }

        int responseCode = connection.getResponseCode();
        InputStream stream = responseCode >= 200 && responseCode < 300
            ? connection.getInputStream() : connection.getErrorStream();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        if (stream != null) {
            try (InputStream in = stream) {
                byte[] chunk = new byte[1024];
                int n;
                while ((n = in.read(chunk)) != -1) {
                    buffer.write(chunk, 0, n);
                }
            }
        }

        connection.disconnect();

        String body = buffer.toString("UTF-8");
        JSONObject result = body.isEmpty() ? new JSONObject() : new JSONObject(body);

        if (responseCode < 200 || responseCode >= 300) {
            throw new Exception(result.optString("error", "Ошибка сервера"));
        }

        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );

        showPhoneScreen();
    }

    private LinearLayout createScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(bg);
        root.setPadding(dp(24), dp(24), dp(24), dp(20));
        return root;
    }

    private void showPhoneScreen() {
        LinearLayout root = createScreen();

        TextView logo = text("✦ STARGRAM", 25, white);
        logo.setTypeface(null, Typeface.BOLD);
        logo.setGravity(Gravity.CENTER);
        root.addView(logo, new LinearLayout.LayoutParams(-1, dp(75)));

        TextView title = text("Номер телефона", 23, white);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView subtitle = text(
            "Выберите код страны и введите номер,\nпривязанный к вашему аккаунту STARGRAM.",
            15, gray
        );
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setLineSpacing(dp(4), 1f);

        LinearLayout.LayoutParams subParams =
            new LinearLayout.LayoutParams(-1, -2);
        subParams.setMargins(0, dp(12), 0, dp(30));
        root.addView(subtitle, subParams);

        country = text("Россия (+7)                         ⌄", 16, white);
        country.setGravity(Gravity.CENTER_VERTICAL);
        country.setPadding(dp(16), 0, dp(12), 0);
        country.setBackground(shape(Color.rgb(35, 35, 35), 0, 10));

        root.addView(country,
            new LinearLayout.LayoutParams(-1, dp(54)));

        country.setOnClickListener(v -> {
            String[] options = {
                "Россия (+7)",
                "Пользовательский номер (+888)"
            };

            new AlertDialog.Builder(this)
                .setTitle("Выберите код")
                .setItems(options, (dialog, which) -> {
                    selectedCode = which == 0 ? "+7" : "+888";
                    country.setText(
                        which == 0
                            ? "Россия (+7)                         ⌄"
                            : "Пользовательский номер (+888)       ⌄"
                    );
                    numberInput.setText("");
                    numberInput.setHint(
                        which == 0 ? "900 000 0000" : "Введите 3, 6 или 10 цифр"
                    );
                })
                .show();
        });

        numberInput = new EditText(this);
        numberInput.setSingleLine(true);
        numberInput.setTextSize(18);
        numberInput.setTextColor(white);
        numberInput.setHintTextColor(gray);
        numberInput.setHint("900 000 0000");
        numberInput.setInputType(2);
        numberInput.setPadding(dp(16), 0, dp(16), 0);
        numberInput.setBackground(shape(Color.rgb(35, 35, 35), blue, 10));
        numberInput.setFilters(new InputFilter[] {
            new InputFilter.LengthFilter(10)
        });

        LinearLayout.LayoutParams inputParams =
            new LinearLayout.LayoutParams(-1, dp(58));
        inputParams.setMargins(0, dp(18), 0, dp(12));
        root.addView(numberInput, inputParams);

        TextView hint = text(
            "+7: 10 цифр, начиная с 900.\n+888: допускается 3, 6 или 10 цифр.",
            13, gray
        );
        root.addView(hint);

        status = text("", 14, Color.rgb(255, 110, 110));
        status.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams statusParams =
            new LinearLayout.LayoutParams(-1, -2);
        statusParams.setMargins(0, dp(12), 0, dp(8));
        root.addView(status, statusParams);

        continueButton = new Button(this);
        continueButton.setText("Продолжить");
        continueButton.setTextColor(white);
        continueButton.setAllCaps(false);
        continueButton.setTextSize(16);
        continueButton.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(blue)
        );

        LinearLayout.LayoutParams buttonParams =
            new LinearLayout.LayoutParams(-1, dp(52));
        buttonParams.setMargins(0, dp(12), 0, 0);
        root.addView(continueButton, buttonParams);

        continueButton.setOnClickListener(v -> requestCode());

        setContentView(root);
    }

    private void requestCode() {
        if (requestInProgress) return;

        String digits = numberInput.getText().toString().trim();

        boolean valid;
        if (selectedCode.equals("+7")) {
            valid = digits.length() == 10 && digits.startsWith("900");
        } else {
            valid = digits.length() == 10 ||
                    digits.length() == 6 ||
                    digits.length() == 3;
        }

        if (!valid) {
            status.setText("Проверьте номер телефона.");
            return;
        }

        currentNumber = selectedCode + digits;
        requestInProgress = true;
        continueButton.setEnabled(false);
        status.setText("Отправляем запрос...");

        hideKeyboard(numberInput);

        executor.execute(() -> {
            try {
                JSONObject data = new JSONObject();
                data.put("number", currentNumber);

                post("/auth/request", data);

                runOnUiThread(() -> {
                    requestInProgress = false;
                    showCodeScreen();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    requestInProgress = false;
                    continueButton.setEnabled(true);
                    status.setText(e.getMessage());
                });
            }
        });
    }

    private void showCodeScreen() {
        LinearLayout root = createScreen();

        TextView logo = text("✦ STARGRAM", 25, white);
        logo.setTypeface(null, Typeface.BOLD);
        logo.setGravity(Gravity.CENTER);
        root.addView(logo, new LinearLayout.LayoutParams(-1, dp(75)));

        TextView title = text("Код подтверждения", 23, white);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView subtitle = text(
            "Если номер зарегистрирован, код отправлен\nв Telegram, привязанный к аккаунту.",
            15, gray
        );
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setLineSpacing(dp(4), 1f);

        LinearLayout.LayoutParams subParams =
            new LinearLayout.LayoutParams(-1, -2);
        subParams.setMargins(0, dp(14), 0, dp(25));
        root.addView(subtitle, subParams);

        TextView number = text(currentNumber, 17, white);
        number.setGravity(Gravity.CENTER);
        root.addView(number);

        codeInput = new EditText(this);
        codeInput.setSingleLine(true);
        codeInput.setGravity(Gravity.CENTER);
        codeInput.setTextSize(25);
        codeInput.setTextColor(white);
        codeInput.setHintTextColor(gray);
        codeInput.setHint("·  ·  ·  ·  ·  ·");
        codeInput.setInputType(2);
        codeInput.setLetterSpacing(0.25f);
        codeInput.setFilters(new InputFilter[] {
            new InputFilter.LengthFilter(6)
        });
        codeInput.setBackground(shape(Color.rgb(35, 35, 35), blue, 10));

        LinearLayout.LayoutParams codeParams =
            new LinearLayout.LayoutParams(-1, dp(62));
        codeParams.setMargins(0, dp(28), 0, dp(12));
        root.addView(codeInput, codeParams);

        status = text("", 14, Color.rgb(255, 110, 110));
        status.setGravity(Gravity.CENTER);
        root.addView(status, new LinearLayout.LayoutParams(-1, dp(35)));

        timerText = text("Код действителен 05:00", 14, gray);
        timerText.setGravity(Gravity.CENTER);
        root.addView(timerText);

        TextView change = text("Изменить номер телефона", 15, blue);
        change.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams changeParams =
            new LinearLayout.LayoutParams(-1, dp(55));
        changeParams.setMargins(0, dp(20), 0, 0);
        root.addView(change, changeParams);

        change.setOnClickListener(v -> {
            if (timer != null) timer.cancel();
            verifyInProgress = false;
            showPhoneScreen();
        });

        setContentView(root);

        timer = new CountDownTimer(300000, 1000) {
            @Override
            public void onTick(long millis) {
                long seconds = millis / 1000;
                timerText.setText(String.format(
                    "Код действителен %02d:%02d",
                    seconds / 60, seconds % 60
                ));
            }

            @Override
            public void onFinish() {
                timerText.setText("Время действия кода истекло");
            }
        }.start();

        codeInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(
                CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(
                CharSequence s, int start, int before, int count) {
                if (s.length() == 6 && !verifyInProgress) {
                    verifyCode(s.toString());
                }
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        codeInput.requestFocus();
        codeInput.post(() -> {
            InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(codeInput, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    private void verifyCode(String code) {
        if (verifyInProgress) return;

        verifyInProgress = true;
        status.setText("Проверяем код...");
        hideKeyboard(codeInput);

        executor.execute(() -> {
            try {
                JSONObject data = new JSONObject();
                data.put("number", currentNumber);
                data.put("code", code);

                JSONObject result = post("/auth/verify", data);
                String token = result.getString("session_token");
                String accountId = result.optString("account_id", "");

                getSharedPreferences("stargram_session", MODE_PRIVATE)
                    .edit()
                    .putString("session_token", token)
                    .putString("account_id", accountId)
                    .apply();

                runOnUiThread(() -> {
                    if (timer != null) timer.cancel();

                    startActivity(new Intent(
                        AuthActivity.this, MainActivity.class
                    ));
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    verifyInProgress = false;
                    status.setText(e.getMessage());
                    codeInput.setText("");
                    codeInput.requestFocus();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (timer != null) timer.cancel();
        executor.shutdownNow();
        super.onDestroy();
    }
}
