package com.coolweather.android.util;

import com.coolweather.android.WeatherService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class HttpUtility {

    private static WeatherService weatherService;

    public static void sendRetrofitRequest(String address) {

        OkHttpClient.Builder httpBuilder=new OkHttpClient.Builder();
        OkHttpClient client=httpBuilder.readTimeout(2000, TimeUnit.MILLISECONDS)
                .connectTimeout(2000, TimeUnit.MILLISECONDS).writeTimeout(2000, TimeUnit.MILLISECONDS) //设置超时
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(address)
                .client(client)
//                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherService = retrofit.create(WeatherService.class);
    }

    public static WeatherService getDataByRequest() {
        return weatherService;
    }
}
