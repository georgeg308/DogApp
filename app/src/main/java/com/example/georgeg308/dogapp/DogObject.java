package com.example.georgeg308.dogapp;

import android.graphics.Bitmap;

public class DogObject {
    public String url;
    public byte[] image;
    public String breed;
    public int id;
    public String name;

    public DogObject(String url, String breed, String name) {
        this.breed = breed;
        this.name = name;
        this.url = url;
    }

    public DogObject(String url, byte[] image, String breed) {
        this.breed = breed;
        this.image = image;
        this.url = url;
    }



    public DogObject() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
