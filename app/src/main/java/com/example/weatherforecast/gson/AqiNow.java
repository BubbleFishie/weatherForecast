package com.example.weatherforecast.gson;

import com.google.gson.annotations.SerializedName;

public class AqiNow {
    /**
     *     aqi:
     *     level:
     *     pm2p5:
     *     category:
     *     pm10:
     *     pubTime:
     */
    public String code;
    @SerializedName("now")
    public More more;
    public class More{
        public String aqi;
        public String level;
        public String pm2p5;
        public String category;
        public String pm10;
        public String pubTime;
    }
}
