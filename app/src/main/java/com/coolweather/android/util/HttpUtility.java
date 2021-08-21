package com.coolweather.android.util;

import android.util.Log;

import com.coolweather.android.WeatherService;
import com.coolweather.android.model.Weather;

import retrofit2.Retrofit;

public class HttpUtility {

    private static WeatherService weatherService;

    public static void sendRetrofitRequest(String address) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(address)
//                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherService = retrofit.create(WeatherService.class);
    }

    public static WeatherService getDataByRequest() {
        return weatherService;
    }
}
