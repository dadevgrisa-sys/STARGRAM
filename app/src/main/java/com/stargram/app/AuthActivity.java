package com.stargram.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.text.InputFilter;

public class AuthActivity extends Activity {

    LinearLayout root;
    TextView country;
    TextView prefix;
    EditText numberInput;
    String selectedCode = "+7";

    int blue = Color.rgb(51, 144, 236);
    int black = Color.rgb(34, 34, 34);
    int gray = Color.rgb(112, 117, 121);

    GradientDrawable shape(int color, int radius) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(radius);
        return bg;
    }

    TextView makeText(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        return t;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        showPhoneScreen();
    }

    void showPhoneScreen() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        root.setPadding(28, 65, 28, 20);

        TextView title = makeText("Номер телефона", 24, black);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        root.addView(title);

        TextView subtitle = makeText(
            "Проверьте код страны и введите номер телефона",
            14, gray
        );
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subParams =
            new LinearLayout.LayoutParams(-1, -2);
        subParams.setMargins(0, 12, 0, 38);
        root.addView(subtitle, subParams);

        country = makeText("Казахстан                         +7     ▾", 16, blue);
        country.setGravity(Gravity.CENTER_VERTICAL);
        country.setPadding(4, 0, 4, 0);

        View countryLine = new View(this);
        countryLine.setBackgroundColor(Color.rgb(218, 221, 225));

        root.addView(country, new LinearLayout.LayoutParams(-1, 48));
        root.addView(countryLine, new LinearLayout.LayoutParams(-1, 1));

        country.setOnClickListener(v -> {
            String[] choices = {
                "Казахстан (+7)",
                "STARGRAM Custom (+888)"
            };

            new AlertDialog.Builder(this)
                .setTitle("Выберите номер")
                .setItems(choices, (dialog, which) -> {
                    if (which == 0) {
                        selectedCode = "+7";
                        country.setText("Казахстан                         +7     ▾");
                    } else {
                        selectedCode = "+888";
                        country.setText("Пользовательский (+888)       ▾");
                    }

                    prefix.setText(selectedCode);
                    numberInput.setText("");
                    numberInput.setFilters(new InputFilter[] {
                        new InputFilter.LengthFilter(
                            selectedCode.equals("+7") ? 10 : 8
                        )
                    });
                })
                .show();
        });

        LinearLayout phoneRow = new LinearLayout(this);
        phoneRow.setOrientation(LinearLayout.HORIZONTAL);
        phoneRow.setGravity(Gravity.CENTER_VERTICAL);

        prefix = makeText("+7", 18, black);
        phoneRow.addView(prefix);

        numberInput = new EditText(this);
        numberInput.setSingleLine(true);
        numberInput.setTextSize(18);
        numberInput.setTextColor(black);
        numberInput.setHint("Номер телефона");
        numberInput.setHintTextColor(Color.rgb(150, 150, 150));
        numberInput.setInputType(2);
        numberInput.setBackgroundColor(Color.TRANSPARENT);
        numberInput.setPadding(12, 0, 0, 0);
        numberInput.setFilters(new InputFilter[] {
            new InputFilter.LengthFilter(10)
        });

        phoneRow.addView(numberInput,
            new LinearLayout.LayoutParams(0, 58, 1));

        TextView next = makeText("➜", 23, Color.WHITE);
        next.setGravity(Gravity.CENTER);
        next.setBackground(shape(blue, 100));

        LinearLayout.LayoutParams arrowParams =
            new LinearLayout.LayoutParams(48, 48);
        arrowParams.setMargins(8, 0, 0, 0);
        phoneRow.addView(next, arrowParams);

        root.addView(phoneRow,
            new LinearLayout.LayoutParams(-1, 58));

        View phoneLine = new View(this);
        phoneLine.setBackgroundColor(Color.rgb(218, 221, 225));
        root.addView(phoneLine,
            new LinearLayout.LayoutParams(-1, 1));

        next.setOnClickListener(v -> {
            String number = numberInput.getText().toString();

            int required = selectedCode.equals("+7") ? 10 : 8;

            if (number.length() != required) {
                Toast.makeText(this,
                    "Проверьте номер телефона",
                    Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this)
                .setTitle("Продолжить")
                .setMessage("Подключение подтверждения номера будет следующим этапом.")
                .setPositiveButton("ОК", null)
                .show();
        });

        setContentView(root);
    }
}
