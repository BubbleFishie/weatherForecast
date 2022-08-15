package com.example.weatherforecast.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherDaily {
    public String code;
    @SerializedName("daily")
    public List<Forecast> forecastList;
}
