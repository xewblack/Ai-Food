package com.example.aifoodapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DetailActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Включение EdgeToEdge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        // Настройка отступов для системных панелей
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Получение данных, если нужно
        String itemName = getIntent().getStringExtra("item_name");
        // Предположим, у вас есть TextView с id "detail_text" в разметке
        // и вы хотите отобразить переданное имя
        // TextView textView = findViewById(R.id.detail_text);
        // textView.setText(itemName);
    }

    public void Back(View view) {
        Intent intent = new Intent(DetailActivity.this, MainActivity.class);
        startActivity(intent);
    }
}