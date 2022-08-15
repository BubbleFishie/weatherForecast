package com.example.weatherforecast.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.weatherforecast.WeatherActivity;
import com.example.weatherforecast.db.City;
import com.example.weatherforecast.db.County;
import com.example.weatherforecast.db.Province;
import com.example.weatherforecast.gson.AqiNow;
import com.example.weatherforecast.gson.Forecast;
import com.example.weatherforecast.gson.SuggesstionDaily;
import com.example.weatherforecast.gson.WeatherDaily;
import com.example.weatherforecast.gson.WeatherNow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class Utility {
    public static boolean handleProvinceReasponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvince= new JSONArray(response);
                for(int i=0;i<allProvince.length();i++) {
                    JSONObject provinceObject =allProvince.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return  true;

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return  false;
    }

    public static boolean handleCityResponse(String response,int provinceId) {
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCities=new JSONArray(response);
                for(int i=0;i<allCities.length();i++) {
                    JSONObject cityObject=allCities.getJSONObject(i);
                    City city=new City();
                    city.setCityCode(cityObject.getInt("id"));
                    city.setCityName(cityObject.getString("name"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return  true;

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCountyResponse(String response,int cityId) {
        if(!TextUtils.isEmpty(response)) {
            try {
                JSONArray couties=new JSONArray(response);
                for(int i=0;i<couties.length();i++) {
                    JSONObject coutyObject=couties.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(coutyObject.getString("name"));
                    county.setWeatherId(coutyObject.getString("weather_id").substring(2));
                    county.setCityId(cityId);
                    county.save();
                }
                return  true;
            }catch (JSONException e){
                e.printStackTrace();
            }


        }
        return  false;
    }


    /**
     * 将返回的json数据解析成weathernow实体类
     */
    public static WeatherNow handleWeatherNowResponse(String response) {
        try{
            JSONObject jsonObject=new JSONObject(response);
            return new Gson().fromJson(jsonObject.toString(),WeatherNow.class);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将返回的json解析成aqinow实体类
     */
    public static AqiNow handleAqiNowResponse(String response) {
        try{
            JSONObject jsonObject=new JSONObject(response);
            return new Gson().fromJson(jsonObject.toString(),AqiNow.class);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static SuggesstionDaily handleSuggesstionDailyResponse(String response) {
        try{
            JSONObject jsonObject=new JSONObject(response);
            return new Gson().fromJson(jsonObject.toString(),SuggesstionDaily.class);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static WeatherDaily handleWeatherDailyResponse(String response) {
        try{
            JSONObject jsonObject=new JSONObject(response);
            Log.d("dailyweather",jsonObject.getString("code"));
            Log.d("dailyweather",jsonObject.getString("daily"));
            WeatherDaily weatherDaily=new WeatherDaily();
            weatherDaily.code=jsonObject.getString("code");
            Type listType = new TypeToken<List<Forecast>>() {}.getType();
            Forecast[] forecasts=new Gson().fromJson(jsonObject.getString("daily"), Forecast[].class);
            weatherDaily.forecastList= Arrays.asList(forecasts);
            return weatherDaily;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

}
