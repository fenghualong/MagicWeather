package com.xidian.flyhigher.magicweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.util.Log;

import com.xidian.flyhigher.magicweather.gson.Weather;
import com.xidian.flyhigher.magicweather.util.HttpUtil;
import com.xidian.flyhigher.magicweather.util.Utility;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.xidian.flyhigher.magicweather.ContractListActivity.PHONE_NUMBER_STRING;
import static com.xidian.flyhigher.magicweather.EditsActivity.EDITS_STRING;
import static com.xidian.flyhigher.magicweather.EditsActivity.NOTIFY;
import static com.xidian.flyhigher.magicweather.EditsActivity.NOT_NOTIFY;
import static com.xidian.flyhigher.magicweather.EditsActivity.SMS_TIME_STRING;

/**
 * Created by fhl on 2017/5/20.
 */

public class AlarmService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String LOG_TAG = AlarmService.class.getSimpleName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG, "onStartCommand executed");
//        Calendar calender = Calendar.getInstance();

        SharedPreferences prefTime = getSharedPreferences("SmsTime", MODE_PRIVATE);
        String timing_sending = prefTime.getString("timing_sending", null);

        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);

        Log.i(LOG_TAG,"calender is :" + str.equals(timing_sending) + "\n" + str + "\n" + timing_sending);

//        int ihour = calender.get(Calendar.HOUR_OF_DAY);
        SharedPreferences pref = getSharedPreferences(EDITS_STRING, MODE_PRIVATE);
        int notify = pref.getInt(SMS_TIME_STRING, NOT_NOTIFY);
        if (str.equals(timing_sending)) {
            if (notify == NOTIFY) {
                sms_time();
            }
        }

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //int anHour = 8 * 60 * 60 * 1000; // 这是8小时的毫秒数
        int anHour = 60 * 1000; // 这是1小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather(final String city) {
        //citysList.add(selectCity.getCityName());

        String weatherUrl = "https://api.heweather.com/x3/weather?city=" + city + "&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                //Log.i(LOG_TAG,"responseText: " + responseText);
                Weather weather = Utility.handleWeatherResponse(responseText);
                if (weather != null && "ok".equals(weather.status)) {
                    SharedPreferences.Editor editor = getSharedPreferences("weather_data", MODE_PRIVATE).edit();
                    editor.putString(city, responseText);
                    editor.apply();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void sendMassage(String phone, String message) {

        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, AlarmService.class), 0);

        SmsManager sms = SmsManager.getDefault();

        sms.sendTextMessage(phone, null, message, pi, null);

    }

    public void sms_time() {

        SharedPreferences prefTime = getSharedPreferences("SmsTime", MODE_PRIVATE);
        String number = prefTime.getString(PHONE_NUMBER_STRING, null);
        String city = prefTime.getString("city_sms", null);
        String massage = prefTime.getString("massage", null);
        String weather_sms = prefTime.getString("weather_sms", null);
        Log.e(LOG_TAG, number + city + massage + weather_sms);

        SharedPreferences preferences = getSharedPreferences("weather_data", MODE_PRIVATE);
        String weatherString = preferences.getString(city, null);
        String weatherInfo = "";

        Weather weather = Utility.handleWeatherResponse(weatherString);
        //massage = massage + ", " + weather.now.more.info + "、" + weather.now.temperature +  "℃。";
        //发送
        if (weather != null) {
            weatherInfo = weather.now.more.info;
            if (weather_sms.equals(weatherInfo)) {
                sendMassage(number, massage);
            }

            stopSelf();
        } else {
            updateWeather(city);
            sms_time();
        }

    }
}
