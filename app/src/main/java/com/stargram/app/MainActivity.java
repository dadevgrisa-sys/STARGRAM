package com.stargram.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setBackgroundColor(Color.rgb(13, 13, 18));
        main.setPadding(20, 30, 20, 20);

        TextView logo = new TextView(this);
        logo.setText("STARGRAM");
        logo.setTextSize(30);
        logo.setTextColor(Color.rgb(170, 90, 255));
        logo.setGravity(Gravity.CENTER_VERTICAL);
        logo.setPadding(0, 10, 0, 25);

        main.addView(logo);

        TextView title = new TextView(this);
        title.setText("Сообщения");
        title.setTextSize(23);
        title.setTextColor(Color.WHITE);
        title.setPadding(0, 10, 0, 20);

        main.addView(title);

        TextView empty = new TextView(this);
        empty.setText("Пока нет чатов\n\nНайди друзей и начни общение!");
        empty.setTextSize(16);
        empty.setTextColor(Color.LTGRAY);
        empty.setGravity(Gravity.CENTER);
        empty.setPadding(10, 100, 10, 100);

        main.addView(empty);

        setContentView(main);
    }
}
