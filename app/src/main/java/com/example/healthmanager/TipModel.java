package com.example.healthmanager;

public class TipModel {

    private String text;
    private int image;

    public TipModel(String text, int image) {
        this.text = text;
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public int getImage() {
        return image;
    }
}