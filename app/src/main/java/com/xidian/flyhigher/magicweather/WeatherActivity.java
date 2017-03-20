package com.xidian.flyhigher.magicweather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xidian.flyhigher.magicweather.gson.Forecast;
import com.xidian.flyhigher.magicweather.gson.Weather;
import com.xidian.flyhigher.magicweather.util.HttpUtil;
import com.xidian.flyhigher.magicweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {


    private TextView textView_weather;
    public DrawerLayout drawerLayout;
    private Button navButton;
    private TextView titleCity;
    private RecyclerView recyclerView;
    private TextView textView_title_update_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // 初始化各控件
        textView_weather = (TextView)findViewById(R.id.weather);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        titleCity = (TextView)findViewById(R.id.title_city) ;
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_weather);
        textView_title_update_time = (TextView)findViewById(R.id.title_update_time);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            // 无缓存时去服务器查询天气
            String weatherId = getIntent().getStringExtra("weather_id");
            requestWeather(weatherId);
        }

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        textView_title_update_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curPosition = getIntent().getStringExtra("position");
                requestWeather(curPosition);
            }
        });

    }

    /**
     * 根据天气id请求城市天气信息。
     */
    public void requestWeather(final String weatherId) {
            String weatherUrl = "https://api.heweather.com/x3/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        if(weatherId.length() != 11){
            weatherUrl = "https://api.heweather.com/x3/weather?city=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        }

        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.i("WeatherActivity","TEST: "+responseText);
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;


        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运行建议：" + weather.suggestion.sport.info;

        titleCity.setText(cityName);
        textView_title_update_time.setText(updateTime);
        String curPosition = getIntent().getStringExtra("position");

        String weather_info = cityName +"\n"
                +updateTime +"\n"
                +degree +"\n"
                +weatherInfo +"\n"
                +comfort +"\n"
                +carWash +"\n"
                +sport +"\n"
                +curPosition + "\n";
        for (Forecast forecast : weather.forecastList) {
            weather_info = weather_info
                    + forecast.date +" "
                    + forecast.more.info +" "
                    + forecast.more.info_code +" "
                    + forecast.temperature.max +" "
                    + forecast.temperature.min + "\n";

        }

        textView_weather.setText(weather_info);
        Log.e("TEST",weather_info);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        ForecastAdapter adapter = new ForecastAdapter(weather.forecastList,this);
        //获取屏幕宽度
        DisplayMetrics dm ;
        dm = getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int mWidth = displayWidth / 4;
        adapter.SetItemWidth(mWidth);
        recyclerView.setAdapter(adapter);
    }

}
