package com.xidian.flyhigher.magicweather;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ant.liao.GifView;
import com.xidian.flyhigher.magicweather.gson.Weather;
import com.xidian.flyhigher.magicweather.util.HttpUtil;
import com.xidian.flyhigher.magicweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.xidian.flyhigher.magicweather.R.id.weather;

/**
 * Created by fhl on 2017/3/24.
 */

public class WeatherFragment extends Fragment{

    private TextView textView_weather;
    private TextView textView_title_update_time;
    private RecyclerView recyclerView;
    private GifView gifView;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.weather_fragment, container, false);

        // 初始化各控件
        textView_weather = (TextView) rootView.findViewById(weather);
        textView_title_update_time = (TextView) rootView.findViewById(R.id.title_update_time);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_weather);
        gifView = (GifView) rootView.findViewById(R.id.homeBackgroundGif);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //String weatherString = prefs.getString("weather", null);
        //Log.i("WeatherFragment","weatherString: " + weatherString);

        //String weatherId = getActivity().getIntent().getStringExtra("weather_id");
        String weatherId = getActivity().getIntent().getStringExtra("position");
        //String weatherId = prefs.getString("weather_id", null);
        //String weatherId = "city=西安";
        requestWeather(weatherId);



        //Weather weather = Utility.handleWeatherResponse(weatherString);
        //showWeatherInfo(weather);



        return rootView;
    }












    /**
     * 根据天气id请求城市天气信息。
     */
    public void requestWeather(String weatherId) {
        String weatherUrl = "https://api.heweather.com/x3/weather?" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";

        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.i("WeatherActivity", "TEST: " + responseText);
                final Weather weather = Utility.handleWeatherResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(getActivity(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
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
        //String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;


        //textView_title_update_time.setText(updateTime);

        String weather_info = cityName + "\n"
                + degree + "\n"
                + weatherInfo + "\n";


        textView_weather.setText(weather_info);

       //获取屏幕宽度
       DisplayMetrics dm;
       dm = getResources().getDisplayMetrics();
       int displayWidth = dm.widthPixels;
       int displayHeight = dm.heightPixels;

       //设置循环视图方向及大小
       LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
       layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
       recyclerView.setLayoutManager(layoutManager);
       ForecastAdapter adapter = new ForecastAdapter(weather.forecastList, getActivity());

       int mWidth = displayWidth / 4;
       adapter.SetItemWidth(mWidth);
       recyclerView.setAdapter(adapter);

        /*
    *处理和显示背景Gif图
     */
       gifView.setGifImage(R.drawable.rian);
       gifView.setShowDimension(displayWidth, displayHeight);
    }




}
