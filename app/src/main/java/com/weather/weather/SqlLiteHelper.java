package com.weather.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Local database controller.
 */
public class SqlLiteHelper extends SQLiteOpenHelper {
    //------------------------VARIABLES------------------------
    protected static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "WeatherDb";
    protected static final String TABLE_NAME = "ChosenCities";
    protected static final String KEY_ID = "id";
    protected static final String CITY_NAME = "CityName";

    //---------------------MAIN-METHODS-------------------------

    public SqlLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + "if not exists " + TABLE_NAME
                + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CITY_NAME + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void addCity(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + CITY_NAME + "="
                + "'" + name + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                return;
            }
        } finally {
            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put(CITY_NAME, name);
        db.insert(TABLE_NAME, null, values);
    }


    public ArrayList<String> getAllCities() {
        ArrayList<String> allItems = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    String item = cursor.getString(1);
                    allItems.add(item);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return allItems;
    }
}