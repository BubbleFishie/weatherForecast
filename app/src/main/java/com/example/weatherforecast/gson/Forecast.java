package com.example.weatherforecast.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    /**
     * fxDate
     * tempMax:
     *     tempMin:
     *     "sunrise": "06:58",
     *       "sunset": "16:59",
     *     "iconDay": "101",
     *       "textDay": "多云",
     *       "iconNight": "150",
     *       "textNight": "晴",
     *     "pressure": "1020",
     *     "cloud": "4",
     */
    public String fxDate;
    public String tempMax;
    public String tempMin;
    public String iconDay;
    public String textDay;
    public String iconNight;
    public String textNight;
    public String cloud;
}
