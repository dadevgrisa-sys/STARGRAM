package com.stargram.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;

public class MainActivity extends Activity {

    int blue = Color.rgb(51, 144, 236);
    int dark = Color.rgb(34, 34, 34);
    int gray = Color.rgb(130, 130, 130);

    LinearLayout main;

    TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        return t;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setBackgroundColor(Color.WHITE);

        LinearLayout header = new LinearLayout(this);
        header.setPadding(18, 18, 18, 12);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView menu = text("☰", 27, blue);
        header.addView(menu);

        TextView title = text("STARGRAM", 23, blue);
        title.setPadding(20, 0, 0, 0);
        title.setTypeface(null, 1);
        header.addView(title);

        main.addView(header);

        EditText search = new EditText(this);
        search.setSingleLine(true);
        search.setTextSize(15);
        search.setHint("Поиск");
        search.setBackgroundColor(Color.rgb(242, 244, 247));
        search.setPadding(18, 5, 18, 5);

        LinearLayout.LayoutParams searchParams =
            new LinearLayout.LayoutParams(-1, 48);
        searchParams.setMargins(15, 5, 15, 12);
        main.addView(search, searchParams);

        TextView stories = text(
            "  Истории    🔵    🔵    🔵    🔵    🔵",
            17, blue
        );
        stories.setPadding(5, 15, 5, 18);
        main.addView(stories);

        addChat("👤", "Александр", "Привет! Как дела?", "15:42");
        addChat("🐱", "Маша", "Отправила фотографию", "15:20");
        addChat("🌍", "Новости", "Последние новости", "14:55");
        addChat("🎮", "Игровой чат", "Кто сегодня играет?", "13:40");
        addChat("⭐", "STARGRAM", "Добро пожаловать!", "12:10");

        android.widget.Space space = new android.widget.Space(this);
        main.addView(space, new LinearLayout.LayoutParams(1, 0, 1));

        LinearLayout bottom = new LinearLayout(this);
        bottom.setGravity(Gravity.CENTER);
        bottom.setPadding(0, 12, 0, 12);
        bottom.setBackgroundColor(Color.rgb(250, 250, 250));

        String[] tabs = {"Контакты", "Звонки", "Чаты", "Настройки"};

        for (String tab : tabs) {
            TextView item = text(tab, 12, blue);
            item.setGravity(Gravity.CENTER);
            bottom.addView(item, new LinearLayout.LayoutParams(0, 45, 1));
        }

        main.addView(bottom);
        setContentView(main);
    }

    void addChat(String avatar, String name, String message, String time) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(16, 12, 16, 12);

        TextView picture = text(avatar, 32, dark);
        row.addView(picture);

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setPadding(15, 0, 0, 0);

        TextView username = text(name, 17, dark);
        username.setTypeface(null, 1);

        TextView lastMessage = text(message, 14, gray);

        info.addView(username);
        info.addView(lastMessage);

        row.addView(info, new LinearLayout.LayoutParams(0, -2, 1));

        TextView clock = text(time, 12, gray);
        row.addView(clock);

        main.addView(row);
    }
}
