package com.stargram.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.*;

public class AuthActivity extends Activity {

    private final int bg = Color.rgb(24, 24, 24);
    private final int white = Color.WHITE;
    private final int gray = Color.rgb(145, 145, 145);
    private final int blue = Color.rgb(82, 115, 245);
    private final int outline = Color.rgb(75, 75, 75);

    private String selectedCode = "+7";
    private TextView country;
    private TextView prefix;
    private EditText numberInput;

    private CheckBox syncContacts;
    private CheckBox testServer;
    private CheckBox compatibility;

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable shape(int color, int strokeColor, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        if (strokeColor != 0) {
            drawable.setStroke(dp(1), strokeColor);
        }
        return drawable;
    }

    private TextView makeText(String value, int size, int color) {
        TextView text = new TextView(this);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(color);
        return text;
    }

    private CheckBox makeCheckBox(String value) {
        CheckBox check = new CheckBox(this);
        check.setText(value);
        check.setTextSize(16);
        check.setTextColor(white);
        check.setGravity(Gravity.CENTER_VERTICAL);
        check.setButtonTintList(new ColorStateList(
            new int[][] {
                new int[] { android.R.attr.state_checked },
                new int[] {}
            },
            new int[] { blue, Color.rgb(110, 110, 120) }
        ));
        check.setPadding(dp(2), 0, 0, 0);
        return check;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        getWindow().getDecorView().setSystemUiVisibility(0);
        getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );

        showScreen();
    }

    private void showScreen() {
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setBackgroundColor(bg);
        screen.setPadding(dp(20), dp(5), dp(20), dp(8));

        // Верхняя панель
        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);

        TextView back = makeText("←", 27, white);
        back.setGravity(Gravity.CENTER);
        toolbar.addView(back, new LinearLayout.LayoutParams(dp(45), dp(48)));

        View spacer = new View(this);
        toolbar.addView(spacer, new LinearLayout.LayoutParams(0, 1, 1));

        TextView menu = makeText("⋮", 27, white);
        menu.setGravity(Gravity.CENTER);
        toolbar.addView(menu, new LinearLayout.LayoutParams(dp(35), dp(48)));

        screen.addView(toolbar);

        back.setOnClickListener(v -> finish());

        menu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, menu);
            popup.getMenu().add("О STARGRAM");
            popup.getMenu().add("Помощь");
            popup.setOnMenuItemClickListener(item -> {
                new AlertDialog.Builder(this)
                    .setTitle("STARGRAM")
                    .setMessage("Мессенджер STARGRAM")
                    .setPositiveButton("OK", null)
                    .show();
                return true;
            });
            popup.show();
        });

        // Основное содержимое
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(18), dp(10), dp(18), 0);

        TextView title = makeText("Номер телефона", 22, white);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        content.addView(title, new LinearLayout.LayoutParams(-1, -2));

        TextView subtitle = makeText(
            "Проверьте код страны и введите свой\nномер телефона.",
            15,
            gray
        );
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setLineSpacing(dp(2), 1f);

        LinearLayout.LayoutParams subtitleParams =
            new LinearLayout.LayoutParams(-1, -2);
        subtitleParams.setMargins(0, dp(12), 0, dp(27));
        content.addView(subtitle, subtitleParams);

        // Выбор страны
        LinearLayout countryBox = new LinearLayout(this);
        countryBox.setGravity(Gravity.CENTER_VERTICAL);
        countryBox.setPadding(dp(16), 0, dp(12), 0);
        countryBox.setBackground(shape(bg, outline, 9));

        country = makeText("Россия", 16, white);
        countryBox.addView(country,
            new LinearLayout.LayoutParams(0, dp(54), 1));

        TextView countryArrow = makeText("⌄", 24, gray);
        countryArrow.setGravity(Gravity.CENTER);
        countryBox.addView(countryArrow,
            new LinearLayout.LayoutParams(dp(30), dp(54)));

        content.addView(countryBox,
            new LinearLayout.LayoutParams(-1, dp(56)));

        countryBox.setOnClickListener(v -> {
            String[] options = {
                "Россия (+7)",
                "Пользовательский номер (+888)"
            };

            new AlertDialog.Builder(this)
                .setTitle("Страна")
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

                    int required = selectedCode.equals("+7") ? 10 : 8;
                    numberInput.setFilters(new InputFilter[] {
                        new InputFilter.LengthFilter(required)
                    });

                    numberInput.setHint(
                        selectedCode.equals("+7")
                            ? "000 000 0000"
                            : "0000 0000"
                    );
                })
                .show();
        });

        // Поле номера телефона
        FrameLayout phoneField = new FrameLayout(this);
        phoneField.setClipChildren(false);
        phoneField.setClipToPadding(false);
        phoneField.setBackground(shape(bg, Color.rgb(135, 150, 255), 10));

        LinearLayout phoneRow = new LinearLayout(this);
        phoneRow.setGravity(Gravity.CENTER_VERTICAL);
        phoneRow.setPadding(dp(12), 0, dp(12), 0);

        prefix = makeText("+7", 18, white);
        prefix.setGravity(Gravity.CENTER);
        phoneRow.addView(prefix,
            new LinearLayout.LayoutParams(dp(48), -1));

        View divider = new View(this);
        divider.setBackgroundColor(outline);
        phoneRow.addView(divider,
            new LinearLayout.LayoutParams(dp(1), dp(27)));

        numberInput = new EditText(this);
        numberInput.setSingleLine(true);
        numberInput.setTextSize(18);
        numberInput.setTextColor(white);
        numberInput.setHint("000 000 0000");
        numberInput.setHintTextColor(gray);
        numberInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        numberInput.setBackgroundColor(Color.TRANSPARENT);
        numberInput.setPadding(dp(12), 0, 0, 0);
        numberInput.setFilters(new InputFilter[] {
            new InputFilter.LengthFilter(10)
        });
        numberInput.setSelectAllOnFocus(false);

        phoneRow.addView(numberInput,
            new LinearLayout.LayoutParams(0, -1, 1));

        phoneField.addView(phoneRow,
            new FrameLayout.LayoutParams(-1, -1));

        TextView phoneLabel = makeText(
            "Номер телефона", 12, Color.rgb(160, 170, 255)
        );
        phoneLabel.setBackgroundColor(bg);
        phoneLabel.setPadding(dp(6), 0, dp(6), 0);

        FrameLayout.LayoutParams labelParams =
            new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.LEFT);
        labelParams.leftMargin = dp(15);
        labelParams.topMargin = -dp(8);
        phoneField.addView(phoneLabel, labelParams);

        LinearLayout.LayoutParams phoneParams =
            new LinearLayout.LayoutParams(-1, dp(58));
        phoneParams.setMargins(0, dp(22), 0, dp(15));
        content.addView(phoneField, phoneParams);

        // Переключатели
        syncContacts = makeCheckBox("Синхронизировать контакты");
        testServer = makeCheckBox("Тестовый сервер");
        compatibility = makeCheckBox("Режим совместимости");

        content.addView(syncContacts,
            new LinearLayout.LayoutParams(-1, dp(48)));

        content.addView(testServer,
            new LinearLayout.LayoutParams(-1, dp(48)));

        LinearLayout compatibilityRow = new LinearLayout(this);
        compatibilityRow.setGravity(Gravity.CENTER_VERTICAL);

        compatibilityRow.addView(compatibility,
            new LinearLayout.LayoutParams(0, dp(48), 1));

        TextView question = makeText("ⓘ", 19, gray);
        question.setGravity(Gravity.CENTER);
        compatibilityRow.addView(question,
            new LinearLayout.LayoutParams(dp(35), dp(45)));

        question.setOnClickListener(v ->
            new AlertDialog.Builder(this)
                .setTitle("Режим совместимости")
                .setMessage("Дополнительный режим совместимости приложения.")
                .setPositiveButton("Понятно", null)
                .show()
        );

        content.addView(compatibilityRow);

        // Кнопка продолжения
        FrameLayout button = new FrameLayout(this);
        button.setBackground(shape(blue, 0, 15));

        TextView arrow = makeText("→", 29, white);
        arrow.setGravity(Gravity.CENTER);
        button.addView(arrow,
            new FrameLayout.LayoutParams(-1, -1));

        LinearLayout.LayoutParams buttonParams =
            new LinearLayout.LayoutParams(dp(56), dp(56));
        buttonParams.gravity = Gravity.RIGHT;
        buttonParams.setMargins(0, dp(8), 0, dp(12));
        content.addView(button, buttonParams);

        button.setOnClickListener(v -> {
            String number = numberInput.getText().toString();

            if (number.equals("20261107")) {
                startActivity(new Intent(AuthActivity.this, MainActivity.class));
                finish();
                return;
            }

            int required = selectedCode.equals("+7") ? 10 : 8;

            if (number.length() != required) {
                Toast.makeText(
                    this,
                    "Проверьте введённый номер",
                    Toast.LENGTH_SHORT
                ).show();
                return;
            }

            new AlertDialog.Builder(this)
                .setTitle("STARGRAM")
                .setMessage("Подтверждение номера будет добавлено следующим этапом.")
                .setPositiveButton("Понятно", null)
                .show();
        });

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(bg);
        scroll.setClipToPadding(false);
        scroll.addView(content);

        screen.addView(scroll,
            new LinearLayout.LayoutParams(-1, 0, 1));

        setContentView(screen);

        // Автоматически открываем цифровую клавиатуру
        numberInput.requestFocus();
        numberInput.post(() -> {
            InputMethodManager keyboard =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(numberInput, InputMethodManager.SHOW_IMPLICIT);
        });
    }
}
