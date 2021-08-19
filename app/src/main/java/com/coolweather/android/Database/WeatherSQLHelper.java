package com.coolweather.android.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class WeatherSQLHelper extends SQLiteOpenHelper {

    //id,provinceName,provinceCode

    public static final String CREATE_TABLE = "CREATE TABLE weather ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "provinceName TEXT, "
            + "provinceCode TEXT)";

    public WeatherSQLHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
