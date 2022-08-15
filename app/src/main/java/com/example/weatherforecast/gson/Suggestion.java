package com.example.weatherforecast.gson;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Suggestion {
    /**
     * fxDate
     * type:
     *     name:
     *     lavel:
     *     category:
     *     text:
     */
    public String fxDate;
    public String type;
    @SerializedName("name")
    public String type_name;
    public String level;
    public String category;
    public String text;

}
