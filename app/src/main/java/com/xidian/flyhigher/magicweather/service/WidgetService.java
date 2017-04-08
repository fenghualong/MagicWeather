package com.xidian.flyhigher.magicweather.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by lilo on 2017/3/30.
 */

/**
 *  这个服务就是过 WidgetService.UPDATE_TIME 发送一个广播 com.xian.leo.update
 *  就这么简单
 */
public class WidgetService  extends Service{
    private static final String TAG = "WidgetService";

    private final String ACTION_UPDATE_ALL = "com.xian.leo.update";
    public static final int UPDATE_TIME = 1000*20;
    private Context mContext;
    private UpdateThread muUpdateThread;

    @Override
    public void onCreate() {
        muUpdateThread = new UpdateThread();
        muUpdateThread.start();
        mContext = this.getApplicationContext();

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if(muUpdateThread!=null){
            muUpdateThread.interrupt();
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class UpdateThread extends  Thread{
        @Override
        public void run() {
            super.run();
            Intent updateIntent = new Intent(ACTION_UPDATE_ALL);
            while(true){
                mContext.sendBroadcast(updateIntent);
                Log.e(TAG, "---------------send a broadcast" );
                try {
                    Thread.sleep(UPDATE_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
