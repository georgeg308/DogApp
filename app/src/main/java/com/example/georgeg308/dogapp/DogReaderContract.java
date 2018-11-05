package com.example.georgeg308.dogapp;

import android.provider.BaseColumns;

public final class DogReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DogReaderContract() {}

    /* Inner class that defines the table contents */
    public static class DogEntry implements BaseColumns {
        public static final String TABLE_NAME = "DogImage";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_BREED= "breed";
    }
}
