package com.fabiocosta.googleplacespoc.ui.home;

import android.graphics.Bitmap;

public class Place {
    public String name;
    public Bitmap image;
    public float rating;

    public Place(String name, Bitmap img, float rating) {
        this.name = name;
        this.image = img;
        this.rating = rating;
    }
}

