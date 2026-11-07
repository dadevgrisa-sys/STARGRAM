
package com.stargram.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {

    int blue = Color.rgb(51, 144, 236);
    int black = Color.rgb(34, 34, 34);
    int gray = Color.rgb(130, 130, 130);

    LinearLayout root;
    LinearLayout content;
    String currentPage = "Чаты";

    String[] names = {
        "Александр", "Маша", "Новости",
        "Игровой чат", "STARGRAM"
    };

    String[] messages = {
        "Привет! Как дела?",
        "Отправила фотографию",
        "Последние новости",
        "Кто сегодня играет?",
        "Добро пожаловать!"
    };

    String[] avatars = {"👤", "🐱", "🌍", "🎮", "⭐"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showPage("Чаты");
    }

    TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        return t;
    }

    LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    LinearLayout row() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }

    void showPage(String page) {
        currentPage = page;

        root = column();
        root.setBackgroundColor(Color.WHITE);

        LinearLayout header = row();
        header.setPadding(16, 12, 16, 12);

        TextView menu = text("☰", 27, blue);
        header.addView(menu);
        menu.setOnClickListener(v -> showPage("Меню"));

        TextView title = text("  STARGRAM", 23, blue);
        title.setTypeface(null, Typeface.BOLD);
        header.addView(title);

        root.addView(header);

        content = column();
        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);

        root.addView(scroll,
            new LinearLayout.LayoutParams(-1, 0, 1));

        LinearLayout bottom = row();
        bottom.setGravity(Gravity.CENTER);
        bottom.setPadding(0, 8, 0, 8);

        String[] tabs = {"Контакты", "Звонки", "Чаты", "Настройки"};

        for (String tab : tabs) {
            TextView item = text(tab, 12,
                currentPage.equals(tab) ? blue : gray);
            item.setGravity(Gravity.CENTER);
            bottom.addView(item,
                new LinearLayout.LayoutParams(0, 48, 1));

            item.setOnClickListener(v -> showPage(tab));
        }

        root.addView(bottom);
        setContentView(root);

        if (page.equals("Чаты")) chats();
        else if (page.equals("Контакты")) contacts();
        else if (page.equals("Звонки")) calls();
        else if (page.equals("Настройки")) settings();
        else if (page.equals("Профиль")) profile();
        else if (page.equals("NFT")) nft();
        else if (page.equals("Stars")) stars();
        else if (page.equals("Меню")) menuPage();
        else if (page.equals("Уведомления")) simplePage("Уведомления");
        else if (page.equals("Конфиденциальность")) simplePage("Конфиденциальность");
        else if (page.equals("Оформление")) simplePage("Оформление");
        else if (page.equals("Язык")) simplePage("Язык");
        else if (page.equals("Чат")) chatPage();
    }

    void heading(String value) {
        TextView t = text(value, 22, black);
        t.setTypeface(null, Typeface.BOLD);
        t.setPadding(18, 20, 10, 15);
        content.addView(t);
    }

    void button(String value, Runnable action) {
        TextView b = text(value, 17, black);
        b.setPadding(18, 17, 18, 17);
        b.setBackgroundColor(Color.rgb(248, 249, 250));
        content.addView(b,
            new LinearLayout.LayoutParams(-1, -2));

        View line = new View(this);
        line.setBackgroundColor(Color.rgb(235, 235, 235));
        content.addView(line,
            new LinearLayout.LayoutParams(-1, 1));

        b.setOnClickListener(v -> action.run());
    }

    void chats() {
        heading("Сообщения");

        EditText search = new EditText(this);
        search.setSingleLine(true);
        search.setHint("Поиск");
        search.setTextSize(15);
        content.addView(search);

        heading("Истории");

        TextView stories = text(
            "  ＋       🔵       🔵       🔵       🔵",
            25, blue);
        stories.setPadding(10, 10, 10, 15);
        content.addView(stories);

        for (int i = 0; i < names.length; i++) {
            final int index = i;

            LinearLayout item = row();
            item.setPadding(15, 15, 15, 15);

            TextView avatar = text(avatars[i], 32, black);
            item.addView(avatar);

            LinearLayout info = column();
            info.setPadding(15, 0, 0, 0);

            TextView name = text(names[i], 17, black);
            name.setTypeface(null, Typeface.BOLD);
            info.addView(name);
            info.addView(text(messages[i], 14, gray));

            item.addView(info);
            content.addView(item);

            item.setOnClickListener(v -> {
                selectedChat = names[index];
                showPage("Чат");
            });
        }
    }

    String selectedChat = "STARGRAM";

    void chatPage() {
        heading(selectedChat);

        TextView welcome = text(
            "Здесь будет переписка с пользователем.",
            16, gray);
        welcome.setPadding(18, 30, 18, 30);
        content.addView(welcome);

        EditText input = new EditText(this);
        input.setHint("Сообщение...");
        content.addView(input);

        button("➤ Отправить", () -> {
            String value = input.getText().toString().trim();
            if (!value.isEmpty()) {
                Toast.makeText(this,
                    "Демо: сообщение «" + value + "»",
                    Toast.LENGTH_SHORT).show();
                input.setText("");
            }
        });

        button("← Назад к чатам", () -> showPage("Чаты"));
    }

    void contacts() {
        heading("Контакты");

        for (String name : names) {
            button("👤  " + name, () -> {
                selectedChat = name;
                showPage("Чат");
            });
        }

        button("＋ Добавить контакт", () ->
            Toast.makeText(this, "Добавление контактов появится позже",
                Toast.LENGTH_SHORT).show());
    }

    void calls() {
        heading("Звонки");
        button("📞  История звонков", () ->
            simplePage("История звонков"));
        button("＋  Новый звонок", () ->
            Toast.makeText(this, "Звонки появятся позже",
                Toast.LENGTH_SHORT).show());
    }

    void settings() {
        heading("Настройки");

        button("👤  Мой профиль", () -> showPage("Профиль"));
        button("🔔  Уведомления", () -> showPage("Уведомления"));
        button("🔒  Конфиденциальность", () ->
            showPage("Конфиденциальность"));
        button("🎨  Оформление", () -> showPage("Оформление"));
        button("🌐  Язык", () -> showPage("Язык"));
        button("⭐  Stars", () -> showPage("Stars"));
        button("🎁  NFT и подарки", () -> showPage("NFT"));
        button("ℹ️  О приложении", () -> simplePage("О приложении"));
    }

    void menuPage() {
        heading("Меню");

        button("👤  Мой профиль", () -> showPage("Профиль"));
        button("💬  Чаты", () -> showPage("Чаты"));
        button("👥  Контакты", () -> showPage("Контакты"));
        button("📞  Звонки", () -> showPage("Звонки"));
        button("⭐  Stars", () -> showPage("Stars"));
        button("🎁  NFT и подарки", () -> showPage("NFT"));
        button("⚙️  Настройки", () -> showPage("Настройки"));
    }

    void profile() {
        heading("Мой профиль");

        TextView avatar = text("👤", 65, blue);
        avatar.setGravity(Gravity.CENTER);
        content.addView(avatar);

        heading("Пользователь STARGRAM");

        button("Изменить имя", () ->
            editValue("Имя"));
        button("Изменить описание", () ->
            editValue("Описание"));
        button("Изменить аватар", () ->
            Toast.makeText(this, "Добавление аватара появится позже",
                Toast.LENGTH_SHORT).show());
    }

    void editValue(String label) {
        EditText input = new EditText(this);
        input.setHint(label);
        content.addView(input);

        button("Сохранить", () ->
            Toast.makeText(this, label + ": " + input.getText(),
                Toast.LENGTH_SHORT).show());
    }

    void nft() {
        heading("NFT и подарки");

        button("🎁  Мои подарки", () -> simplePage("Мои подарки"));
        button("💎  Коллекции", () -> simplePage("Коллекции"));
        button("🛍  Магазин", () -> simplePage("Магазин"));
        button("🎨  Редкие предметы", () ->
            simplePage("Редкие предметы"));
    }

    void stars() {
        heading("Мои Stars");

        TextView balance = text("⭐ 0 Stars", 28, blue);
        balance.setGravity(Gravity.CENTER);
        balance.setPadding(15, 35, 15, 35);
        content.addView(balance);

        button("Пополнить баланс", () ->
            simplePage("Пополнение Stars"));
        button("История операций", () ->
            simplePage("История операций"));
    }

    void simplePage(String name) {
        heading(name);

        TextView info = text(
            "Раздел STARGRAM: " + name +
            "\n\nЗдесь будут настройки и функции этого раздела.",
            16, gray);

        info.setPadding(18, 25, 18, 25);
        content.addView(info);

        button("← Назад", () -> showPage("Настройки"));
    }

    @Override
    public void onBackPressed() {
        showPage("Чаты");
    }
}

