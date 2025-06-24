package com.example.aifoodapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Включение EdgeToEdge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Инициализация RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 колонки

        // Создайте список из 4 элементов
        List<CardItem> cardItems = new ArrayList<>();
        cardItems.add(new CardItem(R.drawable.card, "Товар 1"));
        cardItems.add(new CardItem(R.drawable.card_2, "Товар 2"));
        cardItems.add(new CardItem(R.drawable.card_3, "Товар 3"));
        cardItems.add(new CardItem(R.drawable.card_4, "Товар 4"));

        // Создаем адаптер, передавая контекст
        CardAdapter adapter = new CardAdapter(this, cardItems);
        recyclerView.setAdapter(adapter);
    }

    // Обработчик для перехода в Profile
    public void Account(View view) {
        Intent intent = new Intent(MainActivity.this, Profile.class);
        startActivity(intent);
    }
}