package com.xidian.flyhigher.magicweather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import com.xidian.flyhigher.magicweather.db.SelectCity;
import com.xidian.flyhigher.magicweather.gson.Weather;
import com.xidian.flyhigher.magicweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by fhl on 2017/4/8.
 */
 public class APPWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "APPWidgetProvider";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Calendar calender = Calendar.getInstance() ;

        int IdImages[] = {
                R.drawable.widget_sun,
                R.drawable.widget_clouds,
                R.drawable.widget_rain,
                R.drawable.widget_snow,
                R.drawable.widget_lighting,
                R.drawable.widget_overcast
        };

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        List<SelectCity> citys = DataSupport.where("isLocationCity != ?","0").find(SelectCity.class);
        String weathercity = citys.get(0).getCityName();
        SharedPreferences pref = context.getSharedPreferences("weather_data",MODE_PRIVATE);
        String weatherString = pref.getString(weathercity,null);
        Log.i("NewAPPWidget","weatherString" +weatherString);
        Weather weather = Utility.handleWeatherResponse(weatherString);

        String temp , descr , date , week=" " ;
        int mIv ;

        int cMonth = calender.get(Calendar.MONTH)+1;
        int cDay = calender.get(Calendar.DAY_OF_MONTH);
        date = cMonth+"."+cDay+"  ";
        int iweek = calender.get(Calendar.DAY_OF_WEEK);
        switch (iweek-1){
            case 1:
                week = "星期一 ";
                break;
            case 2:
                week = "星期二 ";
                break;
            case 3:
                week = "星期三 ";
                break;
            case 4:
                week = "星期四 ";
                break;
            case 5:
                week = "星期五 ";
                break;
            case 6:
                week = "星期六 ";
                break;
            case 7:
                week = "星期日 ";
                break;
        }


        // Construct the RemoteViews object
        RemoteViews rvs = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        if(weather == null){ /*if == null 按理是进不去的  为了防止空指针加了一些没拿到数据的判断*/
            Log.e(TAG, "onReceive: big error--------------"+"no data " );
            temp = "9999";
            descr = "Error";
            mIv = IdImages[1];
        }else {  /*执行到这里说明我们拿到了数据，把数据付给变量*/
            temp = weather.now.temperature + "℃";
            descr =weather.now.more.info;
                /*判断用什么天气 陈氏判断*/
            mIv = IdImages[1];
            if(descr.indexOf("雪")!=-1){
                mIv = IdImages[3];
            }
            if(descr.indexOf("雨")!=-1){
                mIv = IdImages[2];
            }
            if (descr.indexOf("多云")!= -1) {
                mIv = IdImages[1];
            }
            if(descr.indexOf("阴")!=-1) {
                mIv = IdImages[5];
            }
            if(descr.indexOf("闪电")!=-1) {
                mIv = IdImages[4];
            }
            if(descr.indexOf("晴")!= -1){
                mIv = IdImages[0];
            }
        }
            /*开始改变界面*/
        Log.e(TAG, "updateAppWidget: "+date+week );
        rvs.setTextViewText(R.id.tv_date, date);
        rvs.setTextViewText(R.id.tv_week , week);
        rvs.setTextViewText(R.id.tv_weather,descr);
        rvs.setTextViewText(R.id.tv_temperature,temp);
        rvs.setImageViewResource(R.id.iv_weather ,mIv);

        // Create an Intent to launch ExampleActivity
        Intent intent = new Intent(context, Main2Activity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Get the layout for the App Widget and attach an on-click listener
        // to the button
        rvs.setOnClickPendingIntent(R.id.kuang, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, rvs);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        mContext = context;

        Log.i("NewAPPWidget","onUpdate");
        for (int appWidgetId : appWidgetIds) {
            Log.i("NewAPPWidget","appWidgetId : " + appWidgetId);
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.e(TAG, "onEnabled: " );
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        Log.e(TAG, "onDisabled: " );
        super.onDisabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.e(TAG, "onDeleted: " );
        super.onDeleted(context, appWidgetIds);
    }
}
