package com.xidian.flyhigher.magicweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xidian.flyhigher.magicweather.db.SelectCity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//    public LocationClient mLocationClient;

//    String curPosition;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for m
     * ore information.
     */

    private List<String> citysList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SelectCity selectCity = new SelectCity();
        selectCity.setCityName("西安");
        selectCity.updateAll("id == ?","1");


//        SelectCity selectCity = DataSupport.find(SelectCity.class, 1);
//        selectCity.setCityName("西安");
//        selectCity.save();
        Intent intent = new Intent(this,Main2Activity.class);
        startActivity(intent);
    }
}
        /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (prefs.getString("weather", null) != null) {
              if(curPosition != null){
                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                intent.putExtra("position", curPosition);
                startActivity(intent);
                finish();
              }
        }*/

        //location
//        try {
//            mLocationClient = new LocationClient(getApplicationContext());
//            mLocationClient.registerLocationListener(new MyLocationListener());
//        } catch (Exception e) {
//               Log.e("MainActivity","LocationClient",e);
//        }
//        List<String> permissionList = new ArrayList<>();
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
//                PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
//        }
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) !=
//                PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.READ_PHONE_STATE);
//        }
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
//                PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
//        if (!permissionList.isEmpty()) {
//            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
//            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
//        } else {
//            requestLocation();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case 1:
//                if (grantResults.length > 0) {
//                    for (int result : grantResults) {
//                        if (result != PackageManager.PERMISSION_GRANTED) {
//                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
//                            finish();
//                            return;
//                        }
//                    }
//                    requestLocation();
//                } else {
//                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//                break;
//            default:
//        }
//    }
//
//    /**
//     * ATTENTION: This was auto-generated to implement the App Indexing API.
//     * See https://g.co/AppIndexing/AndroidStudio for more information.
//     */
//
//    public class MyLocationListener implements BDLocationListener {
//
//        @Override
//        public void onConnectHotSpotMessage(String s, int i) {
//
//            Log.i("MainActivity", "TEST: onConnectHotSpotMessage:" + s + "i: " + i);
//            //return;
//        }
//
//
//        @Override
//        public void onReceiveLocation(BDLocation location) {
//            StringBuilder currentPosition = new StringBuilder();
//            currentPosition.append("纬度：").append(location.getLatitude()).append("\n");
//            currentPosition.append("经线：").append(location.getLongitude()).append("\n");
//            currentPosition.append("国家：").append(location.getCountry()).append("\n");
//            currentPosition.append("省：").append(location.getProvince()).append("\n");
//            currentPosition.append("市：").append(location.getCity()).append("\n");
//            currentPosition.append("区：").append(location.getDistrict()).append("\n");
//            currentPosition.append("街道：").append(location.getStreet()).append("\n");
//            currentPosition.append("城市代码：").append(location.getCityCode()).append("\n");
//            currentPosition.append("定位方式：");
//            if (location.getLocType() == BDLocation.TypeGpsLocation) {
//                currentPosition.append("GPS");
//            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
//                currentPosition.append("网络");
//            }
//            //positionText.setText(currentPosition);
//            curPosition = currentPosition.toString();
//            Log.e("MainAcivity", "curPosition1: " + curPosition);
//            curPosition = "city="+location.getCity();
//            Log.e("MainAcivity", "curPosition: " + curPosition);
//            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
//            if (prefs.getString("weather", null) != null) {
////                if(curPosition != null){
//                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
//                intent.putExtra("position", curPosition);
//                startActivity(intent);
//                finish();
//                //}
//            }
//        }
//    }
//
//    private void requestLocation() {
//        initLocation();
//        mLocationClient.start();
//    }
//
//    private void initLocation() {
//        LocationClientOption option = new LocationClientOption();
//        option.setScanSpan(5000);
//        //option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
//        option.setIsNeedAddress(true);
//        mLocationClient.setLocOption(option);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mLocationClient.stop();
//    }
//}
