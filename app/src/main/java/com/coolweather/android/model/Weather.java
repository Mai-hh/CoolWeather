package com.coolweather.android.model;

import com.google.gson.annotations.SerializedName;

public class Weather {

    private HeWeather heWeather;

    public HeWeather getHeWeather() {
        return heWeather;
    }



    public static class HeWeather {

        private Basic basic;
        private Update update;
        private String status;
        private Now now;
        private DailyForecast dailyForecast;
        private Aqi aqi;
        private Suggestion suggestion;

        public static class Basic {

            @SerializedName("cid")
            private String cid;

            @SerializedName("location")
            private String location;

            @SerializedName("parent_city")
            private String parenCity;

            @SerializedName("admin_area")
            private String adminArea;

            @SerializedName("cnty")
            private String country;

            @SerializedName("lat")
            private double lat;

            @SerializedName("lon")
            private double lon;

            @SerializedName("tz")
            private String timeZone;

            @SerializedName("city")
            private String city;

            @SerializedName("update")
            private Update update;

        }

        private static class Update {

            @SerializedName("loc")
            private String loc;

            @SerializedName("utc")
            private String utc;
        }

        private static class Now {

            @SerializedName("cloud")
            private int cloud;

            @SerializedName("cond_code")
            private int condCode;

            @SerializedName("cond_txt")
            private String condText;

            @SerializedName("fl")
            private int fl;

            @SerializedName("hum")
            private int hum;

            @SerializedName("pcpn")
            private double pcpn;

            @SerializedName("pres")
            private int pres;

            @SerializedName("tmp")
            private int tmp;

            @SerializedName("vis")
            private int vis;

            @SerializedName("wind_deg")
            private int windDeg;

            @SerializedName("wind_dir")
            private String windDir;

            @SerializedName("wind_sc")
            private int windSC;

            @SerializedName("wind_spd")
            private int windSpeed;

            @SerializedName("cond")
            private Cond cond;

            private static class Cond {

                @SerializedName("code")
                private int code;

                @SerializedName("txt")
                private String text;

            }
        }

        private static class DailyForecast {
        }

        private static class Aqi {
        }

        private static class Suggestion {
        }
    }
}
