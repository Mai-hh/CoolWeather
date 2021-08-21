package com.coolweather.android;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherService {

    @GET("weather")
    Call<ResponseBody> getWeatherData(@Query("cityid")String cityID, @Query("key") String key);

    @GET("china")
    Call<ResponseBody> getProvinces();

    @GET("{id}")
    Call<ResponseBody> getCities(@Path("id") int id);

    @GET("{proid}/{cityid}")
    Call<ResponseBody> getCounties(@Path("proid") int provinceId, @Path("cityid") int cityId);

    @GET("bing_pic")
    Call<ResponseBody> getBingPic();
}
