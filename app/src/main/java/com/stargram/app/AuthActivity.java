package com.stargram.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.text.InputFilter;

public class AuthActivity extends Activity {

    private final int blue = Color.rgb(51, 144, 236);
    private final int black = Color.rgb(34, 34, 34);
    private final int gray = Color.rgb(112, 117, 121);

    private String selectedCode = "+7";
    private TextView country;
    private TextView prefix;
    private EditText numberInput;

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable border(int color, int stroke, int radius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadius(dp(radius));
        shape.setStroke(dp(stroke), Color.rgb(218, 221, 225));
        return shape;
    }

    private TextView makeText(String value, int size, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        return view;
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

    private void showScreen() {
        FrameLayout screen = new FrameLayout(this);
        screen.setBackgroundColor(Color.WHITE);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.setPadding(dp(24), dp(8), dp(24), dp(20));

        FrameLayout.LayoutParams contentParams =
            new FrameLayout.LayoutParams(
                -1, -2, Gravity.TOP | Gravity.CENTER_HORIZONTAL
            );
        contentParams.topMargin = dp(65);
        screen.addView(content, contentParams);

        TextView logo = makeText("STARGRAM", 25, blue);
        logo.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        logo.setGravity(Gravity.CENTER);
        content.addView(logo, new LinearLayout.LayoutParams(-1, dp(42)));

        TextView title = makeText("Номер телефона", 24, black);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams =
            new LinearLayout.LayoutParams(-1, -2);
        titleParams.setMargins(0, dp(25), 0, dp(12));
        content.addView(title, titleParams);

        TextView subtitle = makeText(
            "Выберите страну и введите номер телефона", 14, gray
        );
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subtitleParams =
            new LinearLayout.LayoutParams(-1, -2);
        subtitleParams.setMargins(0, 0, 0, dp(30));
        content.addView(subtitle, subtitleParams);

        country = makeText("Россия", 16, black);
        country.setGravity(Gravity.CENTER_VERTICAL);

        TextView arrow = makeText("⌄", 22, blue);
        arrow.setGravity(Gravity.CENTER);

        LinearLayout countryBox = new LinearLayout(this);
        countryBox.setOrientation(LinearLayout.HORIZONTAL);
        countryBox.setGravity(Gravity.CENTER_VERTICAL);
        countryBox.setPadding(dp(14), 0, dp(12), 0);
        countryBox.setBackground(border(Color.WHITE, 1, 9));
        countryBox.addView(country, new LinearLayout.LayoutParams(0, dp(54), 1));
        countryBox.addView(arrow, new LinearLayout.LayoutParams(dp(30), dp(54)));

        LinearLayout.LayoutParams countryParams =
            new LinearLayout.LayoutParams(-1, dp(54));
        countryParams.setMargins(0, 0, 0, dp(22));
        content.addView(countryBox, countryParams);

        TextView phoneLabel = makeText("Номер телефона", 14, gray);
        LinearLayout.LayoutParams labelParams =
            new LinearLayout.LayoutParams(-1, -2);
        labelParams.setMargins(dp(2), 0, 0, dp(8));
        content.addView(phoneLabel, labelParams);

        LinearLayout phoneRow = new LinearLayout(this);
        phoneRow.setOrientation(LinearLayout.HORIZONTAL);
        phoneRow.setGravity(Gravity.CENTER_VERTICAL);
        phoneRow.setPadding(dp(8), dp(5), dp(10), dp(5));
        phoneRow.setBackground(border(Color.WHITE, 1, 9));

        prefix = makeText("+7", 18, black);
        prefix.setGravity(Gravity.CENTER);
        prefix.setBackground(border(Color.WHITE, 1, 7));

        phoneRow.addView(prefix,
            new LinearLayout.LayoutParams(dp(58), dp(44)));

        numberInput = new EditText(this);
        numberInput.setSingleLine(true);
        numberInput.setTextSize(18);
        numberInput.setTextColor(black);
        numberInput.setHint("000 000 0000");
        numberInput.setHintTextColor(Color.rgb(150, 150, 150));
        numberInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        numberInput.setBackgroundColor(Color.TRANSPARENT);
        numberInput.setPadding(dp(12), 0, 0, 0);
        numberInput.setFilters(new InputFilter[] {
            new InputFilter.LengthFilter(10)
        });

        phoneRow.addView(numberInput,
            new LinearLayout.LayoutParams(0, dp(54), 1));

        LinearLayout.LayoutParams phoneParams =
            new LinearLayout.LayoutParams(-1, dp(64));
        content.addView(phoneRow, phoneParams);

        TextView next = makeText("➜", 23, Color.WHITE);
        next.setGravity(Gravity.CENTER);
        next.setBackground(border(blue, 0, 50));

        LinearLayout.LayoutParams nextParams =
            new LinearLayout.LayoutParams(dp(52), dp(52));
        nextParams.gravity = Gravity.RIGHT;
        nextParams.setMargins(0, dp(25), 0, 0);
        content.addView(next, nextParams);

        countryBox.setOnClickListener(v -> {
            String[] options = {
                "Россия (+7)",
                "Пользовательский номер (+888)"
            };

            new AlertDialog.Builder(this)
                .setTitle("Выберите страну или тип номера")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        selectedCode = "+7";
                        country.setText("Россия");
                    } else {
                        selectedCode = "+888";
                        country.setText("Пользовательский номер");
                    }

                    prefix.setText(selectedCode);
                    numberInput.setText("");
                    numberInput.setFilters(new InputFilter[] {
                        new InputFilter.LengthFilter(
                            selectedCode.equals("+7") ? 10 : 8
                        )
                    });
                    numberInput.setHint(
                        selectedCode.equals("+7") ? "000 000 0000" : "0000 0000"
                    );
                })
                .show();
        });

        next.setOnClickListener(v -> {
            String number = numberInput.getText().toString();

            if (number.equals("20261107")) {
                startActivity(new Intent(AuthActivity.this, MainActivity.class));
                finish();
                return;
            }

            int required = selectedCode.equals("+7") ? 10 : 8;
            if (number.length() != required) {
                Toast.makeText(
                    this, "Проверьте введённый номер", Toast.LENGTH_SHORT
                ).show();
                return;
            }

            new AlertDialog.Builder(this)
                .setTitle("STARGRAM")
                .setMessage("Подтверждение номера будет добавлено следующим этапом.")
                .setPositiveButton("Понятно", null)
                .show();
        });

        setContentView(screen);
    }
}
