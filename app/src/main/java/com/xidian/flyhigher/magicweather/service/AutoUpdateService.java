package com.xidian.flyhigher.magicweather.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.xidian.flyhigher.magicweather.R;
import com.xidian.flyhigher.magicweather.db.SelectCity;
import com.xidian.flyhigher.magicweather.gson.Weather;
import com.xidian.flyhigher.magicweather.util.HttpUtil;
import com.xidian.flyhigher.magicweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.xidian.flyhigher.magicweather.ContractListActivity.PHONE_NUMBER_STRING;
import static com.xidian.flyhigher.magicweather.EditsActivity.EDITS_STRING;
import static com.xidian.flyhigher.magicweather.EditsActivity.NOTIFY;
import static com.xidian.flyhigher.magicweather.EditsActivity.NOTIFY_BANK_STRING;
import static com.xidian.flyhigher.magicweather.EditsActivity.NOT_NOTIFY;
import static com.xidian.flyhigher.magicweather.EditsActivity.SMS_WEATHER_CHANGE_STRING;
import static com.xidian.flyhigher.magicweather.EditsActivity.UPDATE_FREQUENCE_STRING;

public class AutoUpdateService extends Service {


    private String LOG_TAG = AutoUpdateService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences pref = getSharedPreferences(EDITS_STRING, MODE_PRIVATE);
        int update = pref.getInt(UPDATE_FREQUENCE_STRING, 1);
        int notify_bank = pref.getInt(NOTIFY_BANK_STRING, NOTIFY);
        int sms = pref.getInt(SMS_WEATHER_CHANGE_STRING,NOT_NOTIFY);

        int dday = pref.getInt("day",0);
        Calendar calender = Calendar.getInstance() ;
        int cDay = calender.get(Calendar.DAY_OF_MONTH);
        int ihour = calender.get(Calendar.HOUR_OF_DAY);

        if (dday != cDay && ihour > 7) {
            SharedPreferences.Editor editor = getSharedPreferences(EDITS_STRING, MODE_PRIVATE).edit();
            editor.putInt("day",cDay);
            editor.apply();
            if(sms == NOTIFY){
                sms_time();
            }

        }

        updateWeather();
        if(notify_bank == NOTIFY){
            notify_bank();
        }
        //notify_bank();
        Log.i(LOG_TAG,"onStartCommand executed");
//        updateBingPic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = update * 60 * 60 * 1000; // 这是1小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息。
     */
    private void updateWeather() {
        List<SelectCity> selectCityList = DataSupport.findAll(SelectCity.class);
        for (SelectCity selectCity : selectCityList) {
            //citysList.add(selectCity.getCityName());
            final String weatherCity = selectCity.getCityName();
            String weatherUrl = "https://api.heweather.com/x3/weather?city=" + weatherCity + "&key=bc0418b57b2d4918819d3974ac1285d9";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = getSharedPreferences("weather_data", MODE_PRIVATE).edit();
                        editor.putString(weatherCity, responseText);
                        editor.apply();
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
            });


            Log.i("ItemListActivity", "id: " + selectCity.getId()
                    + "\ncityname: " + selectCity.getCityName());
        }

    }

    private void notify_bank() {
//        Intent intent = new Intent(this, Main2Activity.class);
//        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        List<SelectCity> citys = DataSupport.where("isLocationCity != ?", "0").find(SelectCity.class);
        String weathercity = citys.get(0).getCityName();

        SharedPreferences preferences = getSharedPreferences("weather_data", MODE_PRIVATE);
        String weatherString = preferences.getString(weathercity, null);
        Weather weather = Utility.handleWeatherResponse(weatherString);

        String comfort = weather.suggestion.comfort.info;
        Log.i("GU",comfort);

        if (comfort != null) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("舒适指数")
                    .setContentText(comfort)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.magic_weather_round)
                    //.setContentIntent(pi)
                    .setAutoCancel(true)
                    //.setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(comfort))
                    .build();
            manager.notify(1, notification);

        }
    }

    public void sms_time() {

        SharedPreferences prefTime = getSharedPreferences("SmsWeatherChange", MODE_PRIVATE);
        String number = prefTime.getString(PHONE_NUMBER_STRING, null);
        String city = prefTime.getString("city_sms", null);
        String massage = prefTime.getString("massage", null);

        SharedPreferences preferences = getSharedPreferences("weather_data", MODE_PRIVATE);
        String weatherString = preferences.getString(city, null);

        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            massage = massage + ". " + weather.now.more.info + "、" + weather.now.temperature + "℃。";
            //发送
            if (weather.now.more.info.indexOf("雨") != -1) {
                sendMassage(number, massage);
            }
        } else {
            updateWeather(city);
            sms_time();
        }

    }

    private void sendMassage(String phone, String message) {

        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, AutoUpdateService.class), 0);

        SmsManager sms = SmsManager.getDefault();

        sms.sendTextMessage(phone, null, message, pi, null);

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


}
