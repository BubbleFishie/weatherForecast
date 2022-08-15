package com.example.weatherforecast.service;

import android.os.AsyncTask;

public class GetWeatherTask extends AsyncTask<String,Integer,Integer> {
    public String weatherId;
    public GetWeatherTask(String weatherId) {
        this.weatherId=weatherId;
    }
    @Override
    protected Integer doInBackground(String... strings) {
        return null;
    }

}
