package com.xidian.flyhigher.magicweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by fhl on 2017/4/8.
 */

public class firstCheckActivity extends AppCompatActivity {
    private static final String TAG = "firstCheckActivity";
    private boolean flags = false ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        flags = sharedPreferences.getBoolean("flag",false);
        //setContentView(R.layout.activity_main2);
        if(flags == false && (info == null || !info.isAvailable())) {
            setContentView(R.layout.judege_first);
            findViewById(R.id.btn_first_check).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent  in = new Intent(firstCheckActivity.this,firstCheckActivity.class);
                    startActivity(in);
                }
            });
        }else if(flags==false && (info == null || info.isAvailable())){
            Log.e(TAG, "onCreate: "+"isAvailable"+flags );
            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
            editor.putBoolean("flag",true);
            editor.apply();
            Intent i = new Intent(firstCheckActivity.this,Main2Activity.class);
            startActivity(i);
            firstCheckActivity.this.finish();
        }else if(flags==true){
            Intent i = new Intent(firstCheckActivity.this,Main2Activity.class);
            startActivity(i);
            firstCheckActivity.this.finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        flags = sharedPreferences.getBoolean("flag",false);
        //setContentView(R.layout.activity_main2);
        if(flags == false && (info == null || !info.isAvailable())) {
            setContentView(R.layout.judege_first);
            findViewById(R.id.btn_first_check).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent  in = new Intent(firstCheckActivity.this,firstCheckActivity.class);
                    startActivity(in);
                }
            });
        }else if(flags==false && (info == null || info.isAvailable())){
            Log.e(TAG, "onNewIntent: "+"isAvailable"+flags );
            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
            editor.putBoolean("flag",true);
            editor.apply();
            Intent i = new Intent(firstCheckActivity.this,Main2Activity.class);
            startActivity(i);
//            firstCheckActivity.this.finish();
        }else if(flags==true){
            Intent i = new Intent(firstCheckActivity.this,Main2Activity.class);
            startActivity(i);
            firstCheckActivity.this.finish();
        }
    }
}
