package com.example.weatherforecast.gson;

import com.google.gson.annotations.SerializedName;

public class WeatherNow {
    /**
     * "obsTime"
     * code:
     *  temp:
     *     feelsLike:
     *     icon:
     *     text:
     *     cloud:
     */
    public String code;
    @SerializedName("now")
    public More more;
    public class More{
        @SerializedName("obsTime")
        public String updateTime;
        // public String code;
        @SerializedName("temp")
        public String temprature;
        public String feelsLike;
        public String icon;
        public String text;
        public String cloud;
    }
}
