package com.stargram.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.*;
import android.text.InputFilter;

public class AuthActivity extends Activity {

    int blue = Color.rgb(51, 144, 236);
    int black = Color.rgb(34, 34, 34);
    int gray = Color.rgb(112, 117, 121);

    String selectedCode = "+7";

    TextView country;
    TextView prefix;
    EditText numberInput;

    GradientDrawable background(int color, int radius) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(radius);
        return bg;
    }

    TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        return t;
    }

    View line() {
        View v = new View(this);
        v.setBackgroundColor(Color.rgb(218, 221, 225));
        return v;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        showScreen();
    }

    void showScreen() {

        FrameLayout screen = new FrameLayout(this);
        screen.setBackgroundColor(Color.WHITE);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.setPadding(dp(28), dp(20), dp(28), dp(20));

        FrameLayout.LayoutParams contentParams =
            new FrameLayout.LayoutParams(
                -1, -2, Gravity.CENTER
            );

        screen.addView(content, contentParams);

        // Заголовок

        TextView title = text(
            "Номер телефона", 25, black
        );

        title.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        title.setGravity(Gravity.CENTER);

        content.addView(title,
            new LinearLayout.LayoutParams(-1, -2));

        // Описание

        TextView subtitle = text(
            "Проверьте код страны и введите номер телефона",
            14, gray
        );

        subtitle.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams subtitleParams =
            new LinearLayout.LayoutParams(-1, -2);

        subtitleParams.setMargins(0, dp(12), 0, dp(38));

        content.addView(subtitle, subtitleParams);

        // Выбор номера

        LinearLayout countryRow = new LinearLayout(this);
        countryRow.setOrientation(LinearLayout.HORIZONTAL);
        countryRow.setGravity(Gravity.CENTER_VERTICAL);

        country = text("Казахстан", 16, blue);

        countryRow.addView(country,
            new LinearLayout.LayoutParams(0, dp(52), 1));

        TextView arrowDown = text("⌄", 23, blue);
        arrowDown.setGravity(Gravity.CENTER);

        countryRow.addView(arrowDown,
            new LinearLayout.LayoutParams(dp(35), dp(52)));

        content.addView(countryRow,
            new LinearLayout.LayoutParams(-1, dp(52)));

        content.addView(line(),
            new LinearLayout.LayoutParams(-1, dp(1)));

        countryRow.setOnClickListener(v -> {

            String[] options = {
                "Казахстан (+7)",
                "Пользовательский номер (+888)"
            };

            new AlertDialog.Builder(this)
                .setTitle("Выберите тип номера")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {
                        selectedCode = "+7";
                        country.setText("Казахстан");
                    } else {
                        selectedCode = "+888";
                        country.setText("Пользовательский номер");
                    }

                    prefix.setText(selectedCode);
                    numberInput.setText("");

                    int limit = selectedCode.equals("+7") ? 10 : 8;

                    numberInput.setFilters(new InputFilter[] {
                        new InputFilter.LengthFilter(limit)
                    });
                })
                .show();
        });

        // Поле номера

        LinearLayout phoneRow = new LinearLayout(this);
        phoneRow.setOrientation(LinearLayout.HORIZONTAL);
        phoneRow.setGravity(Gravity.CENTER_VERTICAL);

        prefix = text("+7", 18, black);

        phoneRow.addView(prefix,
            new LinearLayout.LayoutParams(-2, dp(55)));

        numberInput = new EditText(this);
        numberInput.setSingleLine(true);
        numberInput.setTextSize(18);
        numberInput.setTextColor(black);
        numberInput.setHint("Номер телефона");
        numberInput.setHintTextColor(Color.rgb(150, 150, 150));
        numberInput.setInputType(2);
        numberInput.setBackgroundColor(Color.TRANSPARENT);
        numberInput.setPadding(dp(12), 0, 0, 0);

        numberInput.setFilters(new InputFilter[] {
            new InputFilter.LengthFilter(10)
        });

        phoneRow.addView(numberInput,
            new LinearLayout.LayoutParams(0, dp(55), 1));

        // Круглая кнопка

        TextView next = text("➜", 23, Color.WHITE);
        next.setGravity(Gravity.CENTER);
        next.setBackground(background(blue, dp(50)));

        LinearLayout.LayoutParams nextParams =
            new LinearLayout.LayoutParams(dp(48), dp(48));

        nextParams.setMargins(dp(8), 0, 0, 0);

        phoneRow.addView(next, nextParams);

        LinearLayout.LayoutParams phoneParams =
            new LinearLayout.LayoutParams(-1, dp(58));

        phoneParams.setMargins(0, dp(4), 0, 0);

        content.addView(phoneRow, phoneParams);

        content.addView(line(),
            new LinearLayout.LayoutParams(-1, dp(1)));

        // Продолжение

        next.setOnClickListener(v -> {

            String number = numberInput.getText().toString();

            int required = selectedCode.equals("+7") ? 10 : 8;

            if (number.length() != required) {
                Toast.makeText(this,
                    "Проверьте введённый номер",
                    Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this)
                .setTitle("Подтверждение")
                .setMessage(
                    "Подключение системы подтверждения будет следующим этапом."
                )
                .setPositiveButton("Понятно", null)
                .show();
        });

        setContentView(screen);
    }

    int dp(int value) {
        return (int) (value * getResources()
            .getDisplayMetrics().density + 0.5f);
    }
}
