package com.stargram.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.content.res.ColorStateList;

public class AuthActivity extends Activity {

    LinearLayout root;
    EditText numberInput;
    TextView prefix;
    TextView country;
    String selectedCode = "+7";

    int blue = Color.rgb(51, 144, 236);

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

    TextView makeText(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        return t;
    }

    void showPhoneScreen() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(25, 65, 25, 20);
        root.setBackgroundColor(Color.WHITE);

        TextView title = makeText("Номер телефона", 25, Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        root.addView(title);

        TextView subtitle = makeText(
            "Проверьте код страны и введите номер телефона",
            14, Color.GRAY
        );
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subParams =
            new LinearLayout.LayoutParams(-1, -2);
        subParams.setMargins(0, 14, 0, 35);
        root.addView(subtitle, subParams);

        country = makeText("Россия                         +7     ▾", 16, blue);
        country.setGravity(Gravity.CENTER_VERTICAL);
        country.setPadding(15, 0, 15, 0);
        country.setBackgroundColor(Color.rgb(247, 249, 252));

        root.addView(country, new LinearLayout.LayoutParams(-1, 55));

        country.setOnClickListener(v -> {
            String[] choices = {
                "Россия (+7)",
                "STARGRAM Custom (+888)"
            };

            new AlertDialog.Builder(this)
                .setTitle("Выберите тип номера")
                .setItems(choices, (dialog, which) -> {
                    if (which == 0) {
                        selectedCode = "+7"; prefix.setText(selectedCode);
                        country.setText("Россия                         +7     ▾");
                    } else {
                        selectedCode = "+888"; prefix.setText(selectedCode);
                        country.setText("STARGRAM Custom       +888     ▾");
                    }
                    numberInput.setText("");
                })
                .show();
        });

        LinearLayout phoneBox = new LinearLayout(this);
        phoneBox.setGravity(Gravity.CENTER_VERTICAL);
        phoneBox.setPadding(15, 0, 10, 0);
        phoneBox.setBackgroundColor(Color.rgb(247, 249, 252));

        prefix = makeText("+7", 17, Color.BLACK);
        phoneBox.addView(prefix);

        numberInput = new EditText(this);
        numberInput.setSingleLine(true);
        numberInput.setTextSize(17);
        numberInput.setTextColor(Color.BLACK);
        numberInput.setHint("Номер телефона");
        numberInput.setHintTextColor(Color.GRAY);
        numberInput.setInputType(2);
        numberInput.setBackgroundColor(Color.TRANSPARENT);
        numberInput.setPadding(12, 0, 0, 0);

        phoneBox.addView(numberInput,
            new LinearLayout.LayoutParams(0, 55, 1));

        root.addView(phoneBox,
            new LinearLayout.LayoutParams(-1, 55));

        numberInput.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                prefix.setText(selectedCode);
            }
            public void afterTextChanged(android.text.Editable s) {}
        });

        Button next = new Button(this);
        next.setText("Продолжить  →");
        next.setTextColor(Color.WHITE);
        next.setTextSize(16);
        next.setBackgroundTintList(ColorStateList.valueOf(blue));

        LinearLayout.LayoutParams buttonParams =
            new LinearLayout.LayoutParams(-1, 52);
        buttonParams.setMargins(0, 28, 0, 0);
        root.addView(next, buttonParams);

        next.setOnClickListener(v -> {
            String number = numberInput.getText().toString()
                .replaceAll("[^0-9]", "");

            int required = selectedCode.equals("+7") ? 10 : 8;

            if (number.length() != required) {
                Toast.makeText(this,
                    "Проверьте количество цифр",
                    Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this)
                .setTitle("Подтверждение")
                .setMessage("Экран кода подтверждения подключим следующим этапом.")
                .setPositiveButton("ОК", null)
                .show();
        });

        setContentView(root);
    }
}
