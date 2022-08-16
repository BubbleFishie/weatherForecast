package com.example.weatherforecast;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.weatherforecast.gson.AqiNow;
import com.example.weatherforecast.gson.Forecast;
import com.example.weatherforecast.gson.SuggesstionDaily;
import com.example.weatherforecast.gson.Weather;
import com.example.weatherforecast.gson.WeatherDaily;
import com.example.weatherforecast.gson.WeatherNow;
import com.example.weatherforecast.util.HttpUtil;
import com.example.weatherforecast.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private int show_weather=4;
    private int aqinow=0;
    private int forecast=0;
    private int suggesstion=0;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView temperatureText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView carWashText;
    private TextView sportText;
    private ProgressDialog progressDialog;
    private ImageView bingPicImg;
    private SharedPreferences prefs;
    public SwipeRefreshLayout swipeRefresh;
    private String mweatherId;
    public DrawerLayout  drawerLayout;
    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 使背景图和状态栏融合
        if(Build.VERSION.SDK_INT>=21) {
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }
        setContentView(R.layout.activity_weather);
        weatherLayout=(ScrollView) findViewById(R.id.weather_layout);
        titleCity=(TextView) findViewById(R.id.title_city);
        titleUpdateTime=(TextView) findViewById(R.id.title_update_time);
        temperatureText=(TextView) findViewById(R.id.temperature_text);
        forecastLayout=(LinearLayout) findViewById(R.id.forecast_layout);
        weatherInfoText=(TextView) findViewById(R.id.weather_info_text);
        aqiText=(TextView) findViewById(R.id.aqi_text);
        pm25Text=(TextView) findViewById(R.id.pm25_text);
        carWashText=(TextView) findViewById(R.id.car_wash_text);
        sportText=(TextView) findViewById(R.id.sprt_text);
        bingPicImg=(ImageView) findViewById(R.id.bing_pic_img);
        prefs= PreferenceManager.getDefaultSharedPreferences(this);
        swipeRefresh=(SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        navButton=(Button) findViewById(R.id.choose_area_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        displayWeather();
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("weather_id",mweatherId);
                requestAll(mweatherId);
            }
        });


    }

    public void displayWeather() {
        String bingPic= prefs.getString("bing_pic",null);
        if(bingPic!=null) {
            Glide.with(this).load(bingPic).into(bingPicImg);

        }else {
            loadBingPic();
        }
        mweatherId=prefs.getString("weatherId",null);
        String weatherString=prefs.getString("weatherNow",null);
        if(weatherString!=null) {
            Log.d("weatherString",weatherString);
            Weather weather=getWeather();
            showWeatherInfo(weather);
        }else {
            Log.d("weatherid",mweatherId);
            weatherLayout.setVisibility(View.INVISIBLE);
            requestAll(mweatherId);
        }

    }
    /**
     * 集合到weather类中
     */
    public Weather getWeather() {
        Weather weather=new Weather();
       // SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weatherNow",null);
        String forecastString=prefs.getString("weatherDaily",null);
        String aqiString=prefs.getString("aqiNow",null);
        String suggesstionString=prefs.getString("suggesstionDaily",null);
        weather.weatherNow=Utility.handleWeatherNowResponse(weatherString);
        weather.weatherDaily=Utility.handleWeatherDailyResponse(forecastString);
        weather.suggesstionDaily=Utility.handleSuggesstionDailyResponse(suggesstionString);
        weather.aqiNow=Utility.handleAqiNowResponse(aqiString);
        weather.cityName=prefs.getString("cityName",null);
        return weather;
    }
    /**
     * 显示天气信息
     */
    public void showWeatherInfo(Weather weather) {
        loadBingPic();
        String cityName=weather.cityName;
        String updateTime=weather.weatherNow.more.updateTime.substring(11,16);
        String temp=weather.weatherNow.more.temprature+"℃";
        String weatherInfo=weather.weatherNow.more.text;
        titleCity.setText(cityName);
        temperatureText.setText(temp);
        titleUpdateTime.setText(updateTime);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.weatherDaily.forecastList){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText=(TextView) view.findViewById(R.id.date_text);
            TextView infoText=(TextView) view.findViewById(R.id.info_text);
            TextView maxText=(TextView) view.findViewById(R.id.max_text);
            TextView minText=(TextView) view.findViewById(R.id.min_text);
            String date=forecast.fxDate;
            dateText.setText(date);
            infoText.setText(forecast.textDay);
            maxText.setText(forecast.tempMax);
            minText.setText(forecast.tempMin);
            forecastLayout.addView(view);
        }
        if(weather.aqiNow!=null) {
            aqiText.setText(weather.aqiNow.more.aqi);
            pm25Text.setText(weather.aqiNow.more.pm2p5);
        }
        String carWash=weather.suggesstionDaily.suggestionList.get(0).type_name+"："+weather.suggesstionDaily.suggestionList.get(0).text;
        String sport=weather.suggesstionDaily.suggestionList.get(1).type_name+"："+weather.suggesstionDaily.suggestionList.get(1).text;
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }
    /**
     * 查询城市天气
     */
    public static final String myKey="2817956463cf41d5b3b998a6995aa616";

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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"实时天气信息请求失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseText=response.body().string();
                Log.d("responseText",responseText);
                final WeatherNow weatherNow=Utility.handleWeatherNowResponse(responseText);
                Log.d("weathenow_code",weatherNow.code);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!weatherNow.code.equals("200")) ErrorInfo(weatherNow.code);
                        if(weatherNow!=null ){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weatherNow",responseText);
                            editor.apply();
                            show_weather--;
                            if(show_weather==0) {
                                Weather weather=getWeather();
                                showWeatherInfo(weather);
                            }


                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气实时信息失败",Toast.LENGTH_SHORT).show();

                        }
                        swipeRefresh.setRefreshing(false);

                    }
                });
            }
        });
    }
    private void requestAqiNow(final String weatherId) {
        String aqiUrl="https://devapi.qweather.com/v7/air/now?location="+weatherId+"&key="+myKey;
        HttpUtil.sendOkHttpRequest(aqiUrl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"实时天气信息请求失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseText=response.body().string();
                Log.d("responseText",responseText);
                final AqiNow aqiNow=Utility.handleAqiNowResponse(responseText);
                Log.d("weathenow_code",aqiNow.code);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!aqiNow.code.equals("200")) ErrorInfo(aqiNow.code);
                        if(aqiNow!=null ){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("aqiNow",responseText);
                            editor.apply();
                            show_weather--;
                            if(show_weather==0) {
                                Weather weather=getWeather();
                                showWeatherInfo(weather);
                            }


                        }else {
                            Toast.makeText(WeatherActivity.this,"获取空气实时信息失败",Toast.LENGTH_SHORT).show();

                        }
                        swipeRefresh.setRefreshing(false);

                    }
                });
            }
        });
    }
    private void requestForecast(final String weatherId) {
        String forecastUrl="https://devapi.qweather.com/v7/weather/3d?location="+weatherId+"&key="+myKey;
        HttpUtil.sendOkHttpRequest(forecastUrl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"实时天气信息请求失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseText=response.body().string();
               // Log.d("weather_id",mweatherId);
                final WeatherDaily weatherDaily=Utility.handleWeatherDailyResponse(responseText);
                Log.d("weathenow_code",weatherDaily.code);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!weatherDaily.code.equals("200")) ErrorInfo(weatherDaily.code);
                        if(weatherDaily!=null ){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weatherDaily",responseText);
                            editor.apply();

                            show_weather--;
                            if(show_weather==0) {
                                Weather weather=getWeather();
                                showWeatherInfo(weather);
                            }


                        }else {
                            Toast.makeText(WeatherActivity.this,"获取空气实时信息失败",Toast.LENGTH_SHORT).show();

                        }
                        swipeRefresh.setRefreshing(false);

                    }
                });
            }
        });

    }
    private void requestSuggesstionDaily(final String weatherId) {
        String suggestionUrl="https://devapi.qweather.com/v7/indices/1d?type=1,2&location="+weatherId+"&key="+myKey;
        HttpUtil.sendOkHttpRequest(suggestionUrl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"实时天气信息请求失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseText=response.body().string();
                Log.d("responseText",responseText);
                final SuggesstionDaily suggesstionDaily=Utility.handleSuggesstionDailyResponse(responseText);
                Log.d("weathenow_code",suggesstionDaily.code);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!suggesstionDaily.code.equals("200")) ErrorInfo(suggesstionDaily.code);
                        if(suggesstionDaily!=null ){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("suggesstionDaily",responseText);
                            editor.apply();
                            show_weather--;
                            if(show_weather==0) {
                                Weather weather=getWeather();
                                showWeatherInfo(weather);
                            }



                        }else {
                            Toast.makeText(WeatherActivity.this,"获取空气实时信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);

                    }
                });
            }
        });
    }

    public void ErrorInfo(String code) {

    }

    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
}