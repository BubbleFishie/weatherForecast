package com.example.weatherforecast.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AqiDaily {
    public String code;
    @SerializedName("daily")
    public List<AQI> aqiList;
}
