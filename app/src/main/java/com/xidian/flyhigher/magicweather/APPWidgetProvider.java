package com.xidian.flyhigher.magicweather;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;


import com.xidian.flyhigher.magicweather.gson.Weather;

import java.util.Calendar;
/**
 * Created by fhl on 2017/4/8.
 */
 public class APPWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "APPWidgetProvider";

    private Calendar calender = Calendar.getInstance() ;
    private Weather weather;
    private Context mContext;
    private  final Intent SERVICE_INTENT = new Intent("android.appwidget.action.APP_WIDGET_SERVICE");
    /*这个是我们发送的广播*/
    private final String ACTION_UPDATE_ALL = "com.xian.leo.update";
    /*定义了一些图片ID*/
    private int IdImages[] = {
            R.drawable.weather_sunny,
            R.drawable.weather_cloud,
            R.drawable.weather_rain,
            R.drawable.weather_snow,
            R.drawable.weather_lightning
    };


    /*主要是这个--------------------------------------------*/
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        this.mContext = context;
        Log.e(TAG, "onReceive: " );

        /*判断使我们发送的广播 ，我们发一次广播，就会进入if中更新一次*/
        /*主要就5个变量
        *   日期  星期
        *   温度   天气 （晴 多云等）  我们根据天气判断什么图片
        * */
        if(ACTION_UPDATE_ALL.equals(action)){
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
            RemoteViews rvs = new RemoteViews(context.getPackageName() , R.layout.widget_layout);
            //更改界面
            /*----------------这里的weather 我直接从天气主界面拿 我改了哪里的一点代码*/
//            List<SelectCity> citys = DataSupport.where("isLocationCity != ?","0").find(SelectCity.class);
//            String city = citys[0];
//            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Main2Activity.class);
//            String weatherString = preferences.getString(weathercity,null);
//            Weather weather = Utility.handleWeatherResponse(weatherString);
            weather = Main2Activity.mweather;
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
                if(descr.indexOf("晴")!= -1){
                    mIv = IdImages[0];
                }
            }
            /*开始改变界面*/
            Log.e(TAG, "onReceive: "+date+week );
            rvs.setTextViewText(R.id.tv_date, date);
            rvs.setTextViewText(R.id.tv_week , week);
            rvs.setTextViewText(R.id.tv_weather,descr);
            rvs.setTextViewText(R.id.tv_temperature,temp);
            rvs.setImageViewResource(R.id.iv_weather ,mIv);
            /*这2行代码不加就不会改变界面生效，加上就对了*/
            AppWidgetManager appWidgetManger = AppWidgetManager
                    .getInstance(context);
            int[] appIds = appWidgetManger.getAppWidgetIds(new ComponentName(context,APPWidgetProvider.class));
            appWidgetManger.updateAppWidget(appIds, rvs);
        }else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mContext = context;

        /*  跳转页面 */
        for(int i =0;i<appWidgetIds.length;i++){
            Intent intent = new Intent(context,Main2Activity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget_layout);
            remoteViews.setOnClickPendingIntent(R.id.iv_weather,pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        Log.e(TAG, "onEnabled: " );
        SERVICE_INTENT.setPackage("com.xidian.flyhigher.magicweather");
        context.startService(SERVICE_INTENT);
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        Log.e(TAG, "onDisabled: " );
        SERVICE_INTENT.setPackage("com.xidian.flyhigher.magicweather");
        context.stopService(SERVICE_INTENT);
        super.onDisabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.e(TAG, "onDeleted: " );
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }
}
