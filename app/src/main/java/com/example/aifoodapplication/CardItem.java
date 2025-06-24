package com.example.aifoodapplication;
public class CardItem {
    public int imageResId; // ресурс картинки
    public String description; // описание

    public CardItem(int imageResId, String description) {
        this.imageResId = imageResId;
        this.description = description;
    }
}