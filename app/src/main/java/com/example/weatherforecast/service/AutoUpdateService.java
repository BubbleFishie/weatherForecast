package com.example.weatherforecast.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.weatherforecast.WeatherActivity;
import com.example.weatherforecast.gson.AqiNow;
import com.example.weatherforecast.gson.SuggesstionDaily;
import com.example.weatherforecast.gson.Weather;
import com.example.weatherforecast.gson.WeatherDaily;
import com.example.weatherforecast.gson.WeatherNow;
import com.example.weatherforecast.util.HttpUtil;
import com.example.weatherforecast.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    private static final String myKey="2817956463cf41d5b3b998a6995aa616";
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager=(AlarmManager) getSystemService(ALARM_SERVICE);
        int anhour=8*60*60*1000;//八小时的毫秒数
        long triggerAtTime= SystemClock.elapsedRealtime()+anhour;
        Intent i=new Intent(this,AutoUpdateService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);

        return super.onStartCommand(intent, flags, startId);
    }

    private void updateBingPic() {
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",bingPic);

            }
        });
    }

    private void updateWeather() {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherId=prefs.getString("weatherId",null);
        requestAll(weatherId);


    }
    public void requestAll(final String weatherId) {

        requestWeatherNow(weatherId);
        requestAqiNow(weatherId);
        requestForecast(weatherId);
        requestSuggesstionDaily(weatherId);


    }
    public void requestWeatherNow(final String weatherId) {
        String weatherUrl="https://devapi.qweather.com/v7/weather/now?location="+weatherId+"&key="+myKey;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseText=response.body().string();
                Log.d("responseText",responseText);
                final WeatherNow weatherNow= Utility.handleWeatherNowResponse(responseText);
                Log.d("weathenow_code",weatherNow.code);
                if(!weatherNow.code.equals("200")) ErrorInfo(weatherNow.code);
                if(weatherNow!=null ){
                    SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                    editor.putString("weatherNow",responseText);
                    editor.apply();

                }
            }
        });
    }
    private void requestAqiNow(final String weatherId) {
        String aqiUrl="https://devapi.qweather.com/v7/air/now?location="+weatherId+"&key="+myKey;
        HttpUtil.sendOkHttpRequest(aqiUrl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseText=response.body().string();
                Log.d("responseText",responseText);
                final AqiNow aqiNow=Utility.handleAqiNowResponse(responseText);
                Log.d("weathenow_code",aqiNow.code);
                if(!aqiNow.code.equals("200")) ErrorInfo(aqiNow.code);
                if(aqiNow!=null ){
                    SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                    editor.putString("aqiNow",responseText);
                    editor.apply();

                }
            }
        });
    }
    private void requestForecast(final String weatherId) {
        String forecastUrl="https://devapi.qweather.com/v7/weather/3d?location="+weatherId+"&key="+myKey;
        HttpUtil.sendOkHttpRequest(forecastUrl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseText=response.body().string();
                // Log.d("weather_id",mweatherId);
                final WeatherDaily weatherDaily=Utility.handleWeatherDailyResponse(responseText);
                Log.d("weathenow_code",weatherDaily.code);
                if(!weatherDaily.code.equals("200")) ErrorInfo(weatherDaily.code);
                if(weatherDaily!=null ){
                    SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                    editor.putString("weatherDaily",responseText);
                    editor.apply();

                }
            }
        });

    }
    private void requestSuggesstionDaily(final String weatherId) {
        String suggestionUrl="https://devapi.qweather.com/v7/indices/1d?type=1,2&location="+weatherId+"&key="+myKey;
        HttpUtil.sendOkHttpRequest(suggestionUrl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseText=response.body().string();
                Log.d("responseText",responseText);
                final SuggesstionDaily suggesstionDaily=Utility.handleSuggesstionDailyResponse(responseText);
                Log.d("weathenow_code",suggesstionDaily.code);
                if(!suggesstionDaily.code.equals("200")) ErrorInfo(suggesstionDaily.code);
                if(suggesstionDaily!=null ){
                    SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                    editor.putString("suggesstionDaily",responseText);
                    editor.apply();

                }
            }
        });
    }
    public void ErrorInfo(String code) {

    }
}