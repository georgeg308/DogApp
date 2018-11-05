package com.example.georgeg308.dogapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

public class DogDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "dog.db";
    static final String KEY_ID = "_id";
    private Context context;

    static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + DogReaderContract.DogEntry.TABLE_NAME  + " (" + KEY_ID + " integer primary key autoincrement, "+
            DogReaderContract.DogEntry.COLUMN_NAME_URL+ " TEXT," +
            DogReaderContract.DogEntry.COLUMN_NAME_IMAGE+ " BLOB," +
            DogReaderContract.DogEntry.COLUMN_NAME_BREED + " TEXT)";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DogReaderContract.DogEntry.TABLE_NAME + " (" +
                    DogReaderContract.DogEntry._ID + " INTEGER PRIMARY KEY," +
                    DogReaderContract.DogEntry.COLUMN_NAME_URL+ " TEXT," +
                    DogReaderContract.DogEntry.COLUMN_NAME_IMAGE+ " BLOB," +
                    DogReaderContract.DogEntry.COLUMN_NAME_BREED + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DogReaderContract.DogEntry.TABLE_NAME;

    public DogDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void dropTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
    }

    public void addDog(DogObject dog) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DogReaderContract.DogEntry.COLUMN_NAME_URL, dog.url);
        values.put(DogReaderContract.DogEntry.COLUMN_NAME_IMAGE, dog.image);
        values.put(DogReaderContract.DogEntry.COLUMN_NAME_BREED, dog.breed);
// Inserting Row
        db.insert(DogReaderContract.DogEntry.TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    public int getDogCount() {
        String countQuery = "SELECT * FROM " + DogReaderContract.DogEntry.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
// return count
        return cursor.getCount();
    }

    public List<DogObject> getAllDogs() {
        List<DogObject> dogList = new ArrayList<DogObject>();
// Select All Query
        String selectQuery = "SELECT * FROM DogImage";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DogObject dog = new DogObject();
                dog.id = Integer.parseInt(cursor.getString(0));
                dog.url = (cursor.getString(1));
                dog.image = (cursor.getBlob(2));
                Log.d("DBBBBB", "getAllDogs: "+ dog.id);
                Log.d("DBBBBB", "getAllDogs: "+ dog.url.toString());
                Log.d("DBBBBB", "getAllDogs: "+ dog.image.toString());
                Log.d("DBBBBB", "getAllDogs: "+ cursor.getColumnName(0));
                Log.d("DBBBBB", "getAllDogs: "+ cursor.getColumnName(1));
                Log.d("DBBBBB", "getAllDogs: "+ cursor.getColumnName(2));
                Log.d("DBBBBB", "getAllDogs: "+ cursor.getColumnName(3));

                dog.breed = (cursor.getString(3));
// Adding contact to list
                dogList.add(dog);
            } while (cursor.moveToNext());
        }
// close inserting data from database
        db.close();
// return contact list
        return dogList;
    }


    public void deleteDog(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DogReaderContract.DogEntry.TABLE_NAME, DogReaderContract.DogEntry.COLUMN_NAME_URL + " = ?",
                new String[] { url });
        db.close();
    }


}
