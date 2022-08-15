package com.example.weatherforecast.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.weatherforecast.gson.Weather;

public class WeatherService extends Service {
    private GetWeatherTask getWeatherTask;
    private WeatherBinder mbinder=new WeatherBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mbinder;
    }
    class WeatherBinder extends Binder {
        public void requestAll(String weatherId) {
            getWeatherTask=new GetWeatherTask(weatherId);

        }

    }
}
