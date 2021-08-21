package com.coolweather.android.util;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.coolweather.android.database.WeatherSQLHelper;
import com.coolweather.android.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {

    public static boolean handleProvinceResponse(String response, WeatherSQLHelper sqlHelper) {
        if (!TextUtils.isEmpty(response)) {
            Log.d("Utility", " 处理省级信息");
            try {
                JSONArray allProvinces= new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    SQLiteDatabase db = sqlHelper.getWritableDatabase();
                    String provinceName = provinceObject.getString("name");
                    String provinceCode = provinceObject.getString("id");
                    Log.d("Utility", provinceName + "/" + provinceCode);
                    ContentValues values = new ContentValues();
                    values.put("provinceCode", provinceCode);
                    values.put("ProvinceName", provinceName);
                    db.insert("Province", null, values);
                    values.clear();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response, int provinceId, WeatherSQLHelper sqlHelper) {
        if (!TextUtils.isEmpty(response)) {
            Log.d("Utility", " 处理市级信息");
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    SQLiteDatabase db = sqlHelper.getWritableDatabase();
                    String cityName = cityObject.getString("name");
                    String cityCode = cityObject.getString("id");

                    ContentValues values = new ContentValues();
                    values.put("cityCode", cityCode);
                    values.put("cityName", cityName);
                    values.put("provinceId", provinceId);
                    db.insert("City", null, values);
                    values.clear();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCountyResponse(String response, int cityId, WeatherSQLHelper sqlHelper) {
        if (!TextUtils.isEmpty(response)) {
            try {

                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {

                    JSONObject countyObject = allCounties.getJSONObject(i);
                    SQLiteDatabase db = sqlHelper.getWritableDatabase();
                    String countyName = countyObject.getString("name");
                    String weatherId = countyObject.getString("weather_id");

                    ContentValues values = new ContentValues();
                    values.put("countyName", countyName);
                    values.put("weatherId", weatherId);
                    values.put("cityId", cityId);
                    db.insert("County", null, values);
                    values.clear();

                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
