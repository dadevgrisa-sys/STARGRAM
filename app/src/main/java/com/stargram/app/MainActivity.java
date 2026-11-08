package com.stargram.app;

import android.app.*;
import android.os.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import android.text.*;
import android.content.*;
import java.util.*;

public class MainActivity extends Activity {

    int blue = Color.rgb(51,144,236);
    int bg, card, fg, muted, line;
    LinearLayout root, content;
    String page = "Чаты";
    boolean dark;
    String selectedChat = "STARGRAM";

    String[] names = {"Александр","Маша","Новости","Игровой чат","STARGRAM"};
    String[] messages = {"Привет! Как дела?","Отправила фотографию","Последние новости","Кто сегодня играет?","Добро пожаловать!"};
    String[] avatars = {"А","М","Н","🎮","✈"};
    String[] times = {"19:04","18:52","18:30","17:45","Вчера"};

    int dp(int n) {
        return (int)(n * getResources().getDisplayMetrics().density + .5f);
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        dark = getPreferences(0).getBoolean("dark", false);
        showPage("Чаты");
    }

    void colors() {
        bg = dark ? Color.rgb(24,30,38) : Color.rgb(255,255,255);
        card = dark ? Color.rgb(34,42,52) : Color.rgb(255,255,255);
        fg = dark ? Color.WHITE : Color.rgb(34,34,34);
        muted = dark ? Color.rgb(160,170,180) : Color.rgb(130,130,130);
        line = dark ? Color.rgb(52,62,74) : Color.rgb(235,238,241);
    }

    TextView text(String s, int size, int color) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        return t;
    }

    LinearLayout row() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(0);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }

    LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(1);
        return l;
    }

    GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }

    void showPage(String p) {
        page = p;
        colors();

        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        getWindow().getDecorView().setSystemUiVisibility(
            dark ? 0 : View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        root = column();
        root.setBackgroundColor(bg);

        if (p.equals("Чаты")) {
            chatHeader();
        } else {
            LinearLayout header = row();
            header.setPadding(dp(18),dp(15),dp(18),dp(15));
            TextView back = text("‹",32,blue);
            header.addView(back);
            back.setOnClickListener(v -> showPage("Чаты"));
            TextView title = text("  "+p,22,fg);
            title.setTypeface(null,Typeface.BOLD);
            header.addView(title);
            root.addView(header);
        }

        ScrollView scroll = new ScrollView(this);
        content = column();
        scroll.addView(content);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));

        if (p.equals("Чаты")) chats();
        else if (p.equals("Контакты")) contacts();
        else if (p.equals("Звонки")) calls();
        else if (p.equals("Настройки")) settings();
        else if (p.equals("Меню")) menuPage();
        else if (p.equals("Профиль")) profile();
        else if (p.equals("Чат")) chatPage();
        else if (p.equals("Stars")) stars();
        else if (p.equals("NFT и подарки")) nft();
        else simplePage(p);

        if (!p.equals("Чат") && !p.equals("Меню")) bottomBar();
        setContentView(root);
    }

    void chatHeader() {
        LinearLayout h = row();
        h.setPadding(dp(18),dp(13),dp(18),dp(13));

        TextView logo = text("✦",32,blue);
        logo.setTypeface(null,Typeface.BOLD);
        h.addView(logo);

        TextView title = text("  Stargram",25,fg);
        title.setTypeface(null,Typeface.BOLD);
        h.addView(title,new LinearLayout.LayoutParams(0,-2,1));

        TextView menu = text("⋮",30,fg);
        h.addView(menu);
        menu.setOnClickListener(v -> showPage("Меню"));

        root.addView(h);
    }

    void heading(String s) {
        TextView t = text(s,19,fg);
        t.setTypeface(null,Typeface.BOLD);
        t.setPadding(dp(18),dp(18),dp(12),dp(10));
        content.addView(t);
    }

    void chats() {
        EditText search = new EditText(this);
        search.setSingleLine(true);
        search.setTextSize(16);
        search.setHint("⌕   Поиск чатов");
        search.setPadding(dp(18),0,dp(18),0);
        search.setTextColor(fg);
        search.setHintTextColor(muted);
        search.setBackground(shape(card,30));

        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1,dp(50));
        sp.setMargins(dp(16),dp(10),dp(16),dp(12));
        content.addView(search,sp);

        LinearLayout banner = row();
        banner.setPadding(dp(15),dp(13),dp(12),dp(13));
        banner.setBackground(shape(card,25));

        LinearLayout bannerText = column();
        bannerText.addView(text("Добро пожаловать в Stargram! ⭐",16,fg));
        bannerText.addView(text("Общайтесь и находите новых друзей.",13,muted));
        banner.addView(bannerText,new LinearLayout.LayoutParams(0,-2,1));

        TextView close = text("×",25,muted);
        banner.addView(close);
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1,-2);
        bp.setMargins(dp(16),0,dp(16),dp(14));
        content.addView(banner,bp);
        close.setOnClickListener(v -> content.removeView(banner));

        LinearLayout archive = row();
        archive.setPadding(dp(16),dp(12),dp(16),dp(12));

        TextView archiveIcon = text("▣",28,Color.WHITE);
        archiveIcon.setGravity(Gravity.CENTER);
        archiveIcon.setBackground(shape(Color.rgb(56,83,105),50));
        archive.addView(archiveIcon,new LinearLayout.LayoutParams(dp(58),dp(58)));

        LinearLayout archiveInfo = column();
        archiveInfo.setPadding(dp(14),0,0,0);
        TextView archiveTitle = text("Архив чатов",17,fg);
        archiveTitle.setTypeface(null,Typeface.BOLD);
        archiveInfo.addView(archiveTitle);
        archiveInfo.addView(text("Сохранённые переписки",14,muted));
        archive.addView(archiveInfo);
        content.addView(archive);
        archive.setOnClickListener(v -> toast("Архив пока пуст"));

        heading("Все чаты");

        ArrayList<View> chatRows = new ArrayList<>();

        for (int i=0;i<names.length;i++) {
            final int index=i;
            LinearLayout item = row();
            item.setPadding(dp(16),dp(11),dp(16),dp(11));

            TextView avatar = text(avatars[i],25,Color.WHITE);
            avatar.setGravity(Gravity.CENTER);
            int[] avatarColors = {
                Color.rgb(67,145,225),
                Color.rgb(219,112,147),
                Color.rgb(80,170,130),
                Color.rgb(125,105,210),
                blue
            };
            avatar.setBackground(shape(avatarColors[i],50));
            item.addView(avatar,new LinearLayout.LayoutParams(dp(58),dp(58)));

            LinearLayout info = column();
            info.setPadding(dp(13),0,0,0);

            TextView name = text(names[i],17,fg);
            name.setTypeface(null,Typeface.BOLD);
            info.addView(name);

            TextView message = text(messages[i],14,muted);
            info.addView(message);

            item.addView(info,new LinearLayout.LayoutParams(0,-2,1));
            TextView time = text(times[i],12,muted);
            item.addView(time);

            content.addView(item);
            chatRows.add(item);

            item.setOnClickListener(v -> {
                selectedChat=names[index];
                showPage("Чат");
            });
        }

        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int st,int c,int a) {}
            public void onTextChanged(CharSequence s,int st,int before,int count) {
                String q=s.toString().toLowerCase();
                for(int i=0;i<chatRows.size();i++) {
                    boolean match = names[i].toLowerCase().contains(q)
                        || messages[i].toLowerCase().contains(q);
                    chatRows.get(i).setVisibility(match ? View.VISIBLE : View.GONE);
                }
            }
            public void afterTextChanged(Editable e) {}
        });
    }

    void bottomBar() {
        LinearLayout bar = row();
        bar.setBackground(shape(card,28));
        bar.setElevation(dp(8));
        bar.setPadding(dp(3),dp(7),dp(3),dp(7));

        String[] tabs={"Чаты","Контакты","Настройки","Профиль"};
        String[] icons={"●","♙","⚙","●"};

        for(int i=0;i<tabs.length;i++) {
            final String tab=tabs[i];
            LinearLayout item=column();
            item.setGravity(Gravity.CENTER);

            int color=page.equals(tab)?blue:muted;
            TextView ic=text(icons[i],23,color);
            ic.setGravity(Gravity.CENTER);
            item.addView(ic);

            TextView label=text(tab,11,color);
            label.setTypeface(null,Typeface.BOLD);
            item.addView(label);

            bar.addView(item,new LinearLayout.LayoutParams(0,dp(50),1));
            item.setOnClickListener(v -> showPage(tab));
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
        lp.setMargins(dp(12),dp(5),dp(12),dp(8));
        root.addView(bar,lp);
    }

    void contacts() {
        heading("Контакты");
        for(String n:names) button("👤   "+n,()->{
            selectedChat=n; showPage("Чат");
        });
        button("＋ Добавить контакт",()->toast("Добавление контактов подключим позже"));
    }

    void calls() {
        heading("Недавние звонки");
        button("☎   История звонков",()->toast("История пока пуста"));
        button("＋   Новый звонок",()->toast("Звонки подключим отдельно"));
    }

    void settings() {
        heading("Настройки");
        button("👤   Мой профиль",()->showPage("Профиль"));
        button("🌙   Тема оформления: "+(dark?"Тёмная":"Светлая"),()->{
            dark=!dark;
            getPreferences(0).edit().putBoolean("dark",dark).apply();
            showPage("Настройки");
        });
        button("🔔   Уведомления",()->simplePage("Уведомления"));
        button("🔒   Конфиденциальность",()->simplePage("Конфиденциальность"));
        button("🌐   Язык",()->simplePage("Язык"));
        button("⭐   Stars",()->showPage("Stars"));
        button("🎁   NFT и подарки",()->showPage("NFT и подарки"));
        button("ℹ️   О STARGRAM",()->simplePage("О STARGRAM"));
    }

    void menuPage() {
        heading("Меню STARGRAM");
        button("👤   Мой профиль",()->showPage("Профиль"));
        button("💬   Чаты",()->showPage("Чаты"));
        button("👥   Контакты",()->showPage("Контакты"));
        button("☎   Звонки",()->showPage("Звонки"));
        button("⭐   Stars",()->showPage("Stars"));
        button("🎁   NFT и подарки",()->showPage("NFT и подарки"));
        button("⚙   Настройки",()->showPage("Настройки"));
    }

    void profile() {
        heading("Мой профиль");
        TextView avatar=text("👤",55,blue);
        avatar.setGravity(Gravity.CENTER);
        content.addView(avatar,new LinearLayout.LayoutParams(-1,dp(100)));
        heading("Пользователь STARGRAM");
        button("Изменить имя",()->toast("Профиль подключим к аккаунту"));
        button("Изменить аватар",()->toast("Выбор фотографии добавим отдельно"));
    }

    void chatPage() {
        heading(selectedChat);
        TextView info=text("Здесь будет переписка с пользователем.",15,muted);
        info.setPadding(dp(18),dp(30),dp(18),dp(30));
        content.addView(info);
        EditText input=new EditText(this);
        input.setHint("Сообщение...");
        content.addView(input);
        button("➤   Отправить",()->{
            if(input.getText().length()>0)
                toast("Отправка сообщений появится после подключения сервера");
        });
        button("← Назад к чатам",()->showPage("Чаты"));
    }

    void stars() {
        heading("Мои Stars");
        TextView balance=text("⭐ 0 Stars",28,blue);
        balance.setGravity(Gravity.CENTER);
        balance.setPadding(0,dp(35),0,dp(35));
        content.addView(balance);
        button("Пополнить баланс",()->simplePage("Пополнение Stars"));
        button("История операций",()->simplePage("История Stars"));
    }

    void nft() {
        heading("NFT и подарки");
        button("🎁   Мои подарки",()->simplePage("Мои подарки"));
        button("💎   Коллекции",()->simplePage("Коллекции"));
        button("🛍   Магазин",()->simplePage("Магазин"));
    }

    void simplePage(String s) {
        heading(s);
        TextView t=text("Раздел STARGRAM\n\nФункции этого раздела подключим следующим этапом.",15,muted);
        t.setPadding(dp(18),dp(20),dp(18),dp(20));
        content.addView(t);
        button("← Назад",()->showPage("Настройки"));
    }

    void button(String s,Runnable action) {
        TextView b=text(s,16,fg);
        b.setPadding(dp(18),dp(17),dp(18),dp(17));
        b.setBackgroundColor(card);
        content.addView(b,new LinearLayout.LayoutParams(-1,-2));
        View d=new View(this);
        d.setBackgroundColor(line);
        content.addView(d,new LinearLayout.LayoutParams(-1,dp(1)));
        b.setOnClickListener(v->action.run());
    }

    void toast(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        showPage("Чаты");
    }
}
