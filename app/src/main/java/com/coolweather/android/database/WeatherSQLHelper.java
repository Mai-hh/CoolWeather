package com.coolweather.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class WeatherSQLHelper extends SQLiteOpenHelper {

    private Context mContext;

    //Province:id,provinceName,provinceCode
    public static final String CREATE_PROVINCE = "CREATE TABLE Province ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "provinceName TEXT, "
            + "provinceCode TEXT)";

    //City:id,cityName,cityCode,provinceId
    public static final String CREATE_CITY = "CREATE TABLE City ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "cityName TEXT, "
            + "cityCode INTEGER, "
            + "provinceId INTEGER)";

    //County:id,countyName,weatherId,cityId
    public static final String CREATE_COUNTY = "CREATE TABLE County ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "countyName TEXT, "
            + "weatherId TEXT, "
            + "cityId TEXT)";


    public WeatherSQLHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PROVINCE);
        sqLiteDatabase.execSQL(CREATE_CITY);
        sqLiteDatabase.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Province");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS City");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS County");
        onCreate(sqLiteDatabase);
    }
}
