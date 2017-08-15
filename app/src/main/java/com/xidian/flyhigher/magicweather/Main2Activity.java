package com.xidian.flyhigher.magicweather;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.xidian.flyhigher.magicweather.db.AllPCC;
import com.xidian.flyhigher.magicweather.db.SelectCity;
import com.xidian.flyhigher.magicweather.gson.Weather;
import com.xidian.flyhigher.magicweather.service.AutoUpdateService;
import com.xidian.flyhigher.magicweather.service.LocationService;
import com.xidian.flyhigher.magicweather.util.HttpUtil;
import com.xidian.flyhigher.magicweather.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.xidian.flyhigher.magicweather.EditsActivity.EDITS_STRING;
import static com.xidian.flyhigher.magicweather.EditsActivity.SUGGESTION_ONE_STRING;
import static com.xidian.flyhigher.magicweather.EditsActivity.SUGGESTION_TWO_STRING;
import static com.xidian.flyhigher.magicweather.EditsActivity.UPDATE_FREQUENCE_STRING;
import static com.xidian.flyhigher.magicweather.R.id.weather;

public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "Main2Activity";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private List<String> citysList = new ArrayList<>();

    private List<SelectCity> selectCityList;

    public static Toolbar toolbar;

    private final int SDK_PERMISSION_REQUEST = 127;

    private String permissionInfo;

    private LocationService locService;

    public ImageView[] pointViews;

    private LinearLayout circle_linearLayout;

   // public static  Weather mweather;

    public static String locationCity;

    public static boolean bIsSendRes = true;

//    public void SetSendRes(boolean bIsSend){
//        bIsSendRes = bIsSend;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        circle_linearLayout = (LinearLayout) findViewById(R.id.circle);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

//        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-M-dd HH");
//        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
//        String str = formatter.format(curDate);
//        Log.i(TAG,"calender is :" + str);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        locService = new LocationService(getApplicationContext());
        LocationClientOption mOption = locService.getDefaultLocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        mOption.setCoorType("bd09ll");
        locService.setLocationOption(mOption);
        locService.registerListener(listener);
        if(ContextCompat.checkSelfPermission(Main2Activity.this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locService.start();
        }else{
            getPersimmions();
        }

        initCity();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),citysList);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        Log.i("Main2Activity","citysList.size()"+citysList.size());
        initPoint(citysList.size());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);
        if(position > 0){

            setPointViews(position);
            Log.i("Main3Activity","intent.getIntExtra position: "+ position);
            mViewPager.setCurrentItem(position);
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrollStateChanged(int state){
                Log.i("MainActivity","TEST:\n" +
                        "state: " + state);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){
                Log.i("MainActivity","TEST:\n"
                        + "posotion: " + position + "\n"
                        + "positionOffset: " + positionOffset + "\n"
                        + "positionOffsetPixels: " + positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position){
                //pointViews[position].setImageResource(R.drawable.circle_dot);
                setPointViews(position);
            }
        });


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        List<AllPCC> allPCCs = DataSupport.select("province").find(AllPCC.class);
        if (!(allPCCs.size() > 0)) {
            queryFromServer();
        }

    }

    private void queryFromServer() {
        String weatherUrl = "https://cdn.heweather.com/china-city-list.json";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.i(TAG, "queryFromServer()" + responseText);
                try {
                    JSONArray jsonArray = new JSONArray(responseText);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String provinceZh = jsonObject.getString("provinceZh");
                        String leaderZh = jsonObject.getString("leaderZh");
                        String cityZh = jsonObject.getString("cityZh");
                        AllPCC allPCC = new AllPCC();
                        allPCC.setProvince(provinceZh);
                        allPCC.setCity(leaderZh);
                        allPCC.setCounty(cityZh);
                        allPCC.save();
                        Log.i(TAG, "" + i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Main2Activity.this, "城市数据加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void setPointViews(int position){
//        if(position > 0){
            for (int x = 0; x < pointViews.length; x++) {
                pointViews[x].setImageResource(R.drawable.circle_gray);
                if (x == position) {
                    pointViews[x].setImageResource(R.drawable.circle_dot);;
                }
            }
//        }

    }

    private void initPoint(int length) {
        if(length > 1){
            Log.i("Main2Activity","initPoint()" + length);
            pointViews = new ImageView[length];        // 设置对应的小圆点
            for (int x = 0; x < length; x++) {
                ImageView iv = new ImageView(this);
                pointViews[x] = iv;
                pointViews[x].setImageResource(R.drawable.circle_gray);
                pointViews[x].setPadding(10, 0, 10, 0);
                pointViews[x].setEnabled(false);
                circle_linearLayout.addView(pointViews[x]);
            }
            pointViews[0].setImageResource(R.drawable.circle_dot);
        }
    }

    private void initCity(){
        selectCityList = DataSupport.findAll(SelectCity.class);
        for (SelectCity selectCity : selectCityList) {
            citysList.add(selectCity.getCityName());
            Log.i("Main2Activity",selectCity.getCityName() + " , " + selectCity.getId());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//
        switch(item.getItemId()) {
            case R.id.citys_list:
                startActivity(new Intent(this, ItemListActivity.class));
                return true;
            case R.id.choose_settings:
                startActivity(new Intent(this, EditsActivity.class));
                return true;
            case R.id.share:
                View view = getWindow().getDecorView();
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache();
                Bitmap bitmap = view.getDrawingCache();

                if (bitmap != null) {
                    Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,null,null));
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
                    shareIntent.setType("image/*");
                    startActivity(shareIntent);
                }
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_city";


        private TextView textView_weather;
        private ImageView suggestion1Image;
        private ImageView suggestion2Image;
        private TextView textView_title_update_time;
        private RecyclerView recyclerView;
        //private GifView gifView;
        private ImageView bkgndImage;
        private String weathercity;
        private SwipeRefreshLayout swipeRefresh;
        private String weatherPause = "";
        private TextView suggestion1Text;
        private String showsuggestion1;
        private String showsuggestion2;
        private String suggestiontitle1;
        private String suggestiontitle2;


        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(String sectionCity) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION_NUMBER, sectionCity);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.weather_fragment, container, false);
            // 初始化各控件
            textView_weather = (TextView) rootView.findViewById(weather);

            suggestion1Image = (ImageView) rootView.findViewById(R.id.suggestion1Image);
            suggestion2Image = (ImageView)rootView.findViewById(R.id.suggestion2Image);

            textView_title_update_time = (TextView) rootView.findViewById(R.id.title_update_time);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_weather);
            //gifView = (GifView) rootView.findViewById(R.id.homeBackgroundGif);
            bkgndImage = (ImageView) rootView.findViewById(R.id.bkgnd_image);
            swipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
            //textView.setText(getString(R.string.section_format) + getArguments().getString(ARG_SECTION_NUMBER));
            Log.i("Main2Activity","getArguments(): " + getArguments().getString(ARG_SECTION_NUMBER));
            Log.i("Main2Activity","R.string.section_format: " + getString(R.string.section_format));
            weathercity = getArguments().getString(ARG_SECTION_NUMBER);
            //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences preferences = getActivity().getSharedPreferences("weather_data",MODE_PRIVATE);
            String weatherString = preferences.getString(weathercity,null);
            if(weatherString != null){
                Weather weather = Utility.handleWeatherResponse(weatherString);

                showWeatherInfo(weather);
            }else{
                requestWeather(weathercity);
            }

            //设置建议图片显示
           // suggestion1Image.setImageResource(R.drawable.air_index);
            //suggestion2Image.setImageResource(R.drawable.car_index);

            //自定义界面显示

            rootView.findViewById(R.id.suggestion1Image).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){

                    showDialog_Layout(showsuggestion1,suggestiontitle1);
                }
            });

            rootView.findViewById(R.id.suggestion2Image).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){

                    showDialog_Layout(showsuggestion2,suggestiontitle2);
                }
            });



            //下拉刷新
            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    requestWeather(weathercity);
                    Log.i(TAG, "refresh");
                }
            });
            return rootView;
        }

        //显示基于layout的alertDialog
        private void showDialog_Layout(String suggestion,String title){

            LayoutInflater inflater = LayoutInflater.from(getContext());
            final View airTextView = inflater.inflate(R.layout.suggestion_one,null);
            suggestion1Text = (TextView)airTextView.findViewById(R.id.suggestion1);
            suggestion1Text.setText("    " + suggestion);

            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            DisplayMetrics dm;
            dm = getResources().getDisplayMetrics();
            int displayWidth = dm.widthPixels;
            //airTextView.setMinimumHeight((int) (displayHeight * 0.2));
            airTextView.setMinimumWidth((int)(displayWidth * 0.6));
            builder.setTitle(title);
            builder.setView(airTextView);
            builder.setCancelable(true);
            builder.show();
        }
        /**
         * 根据天气id请求城市天气信息。
         */
        public void requestWeather(String weatherId) {
            String weatherUrl = "https://api.heweather.com/x3/weather?city=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";


            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseText = response.body().string();
                    Log.i("Main2Activity", "TEST: " + responseText);
                    final Weather weather = Utility.handleWeatherResponse(responseText);
                    //mweather = weather;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (weather != null && "ok".equals(weather.status)) {
                                SharedPreferences.Editor editor = getActivity().getSharedPreferences("weather_data",MODE_PRIVATE).edit();
                                //SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                                editor.putString(weathercity, responseText);
                                editor.apply();
                                showWeatherInfo(weather);
                            } else {
                                Toast.makeText(getActivity(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                            }
                            swipeRefresh.setRefreshing(false);

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
                            swipeRefresh.setRefreshing(false);
                        }
                    });
                }
            });

//            SharedPreferences pref = getActivity().getSharedPreferences(EDITS_STRING, MODE_PRIVATE);
//            int update = pref.getInt(UPDATE_FREQUENCE_STRING, 1);
//            Log.i(TAG,"update: " + update);
//            Intent intent = new Intent(getActivity(), AutoUpdateService.class);
//            if(0 != update){
//                getActivity().startService(intent);
//            }else{
//                getActivity().stopService(intent);
//            }

        }

        private void showSuggestionImage(ImageView suggestionImage ,int flag) {
            switch (flag) {
                case 1:
                    suggestionImage.setImageResource(R.drawable.car_index);
                    break;
                case 2:
                    suggestionImage.setImageResource(R.drawable.clothes_index);
                    break;
                case 3:
                    suggestionImage.setImageResource(R.drawable.comfort_index);
                    break;
                case 4:
                    suggestionImage.setImageResource(R.drawable.ultraviolet_index);
                    break;
                case 5:
                    suggestionImage.setImageResource(R.drawable.cold_index);
                    break;
                case 6:
                    suggestionImage.setImageResource(R.drawable.sports_index);
                    break;
                default:
                    break;
            }
        }

        private String suggestion_title(int flag){
            switch (flag){
                case 1:
                    return getActivity().getString(R.string.cw);
                case 2:
                    return getActivity().getString(R.string.drsg);

                case 3:
                    return getActivity().getString(R.string.comf);
                case 4:
                    return getActivity().getString(R.string.uv);
                case 5:
                    return getActivity().getString(R.string.flu);
                case 6:
                    return getActivity().getString(R.string.sport);
                default:
                    return "";
            }
        }

        private String suggestion_method(Weather weather,int flag){
            switch (flag){
                case 1:
                    return weather.suggestion.carWash.info;

                case 2:
                    return weather.suggestion.drsg.info;

                case 3:
                    return weather.suggestion.comfort.info;

                case 4:
                    return weather.suggestion.uv.info;

                case 5:
                    return weather.suggestion.flu.info;

                case 6:
                    return weather.suggestion.sport.info;

                default:
                    return "";

            }
        }

        /**
         * 处理并展示Weather实体类中的数据。
         */
        private void showWeatherInfo(Weather weather) {
            String cityName = weather.basic.cityName;
            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            String degree = weather.now.temperature + "℃";
            String weatherInfo = weather.now.more.info;

            SharedPreferences pref = getActivity().getSharedPreferences(EDITS_STRING, MODE_PRIVATE);
            int update = pref.getInt(UPDATE_FREQUENCE_STRING, 1);
            Log.i(TAG,"update: " + update);
            if (bIsSendRes) {

                Intent intent = new Intent(getActivity(), AutoUpdateService.class);
                if (0 != update) {
                    getActivity().startService(intent);
                } else {
                    getActivity().stopService(intent);
                }
                bIsSendRes = false;
            }

            //SharedPreferences pref = getActivity().getSharedPreferences(EDITS_STRING, MODE_PRIVATE);
            int sug_one = pref.getInt(SUGGESTION_ONE_STRING, 1);
            int sug_two = pref.getInt(SUGGESTION_TWO_STRING, 2);

            showsuggestion1= suggestion_method(weather,sug_one);
            showsuggestion2 = suggestion_method(weather,sug_two);

            suggestiontitle1 = suggestion_title(sug_one);
            suggestiontitle2 = suggestion_title(sug_two);

            showSuggestionImage(suggestion1Image,sug_one);
            showSuggestionImage(suggestion2Image,sug_two);


            //showsuggestion1= weather.aqi.city.aqi;
            //showsuggestion2= weather.aqi.city.pm25;
            Log.v("Main2Activity","showsuggestion1" + showsuggestion1);

            String update_Time = "发布时间" + updateTime;
            textView_title_update_time.setText(update_Time);
            String weather_info = cityName + "\n"
                    + degree + "\n"
                    + weatherInfo + "\n";

            Log.e("Main2Activity","showWeatherInfo" + weather_info);
            textView_weather.setText(weather_info);
            /*if (weather.aqi != null) {
                aqiText.setText(weather.aqi.city.aqi);
                pm25Text.setText(weather.aqi.city.pm25);
            }
            String carWash = "洗车指数：" + weather.suggestion.carWash.info;*/
            //String comfort = "舒适度：" + weather.suggestion.comfort.info;
            //String sport = "运行建议：" + weather.suggestion.sport.info;
            //carWashText.setText(carWash);
            //comfortText.setText(comfort);
            //sportText.setText(sport);

            //获取屏幕宽度
            DisplayMetrics dm;
            dm = getResources().getDisplayMetrics();
            int displayWidth = dm.widthPixels;
            //int displayHeight = dm.heightPixels;

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

        if (!weatherPause.equals(weatherInfo)) {
            Log.i("Main2Activity", "weatherPause: " + weatherPause
                    + "\nweatherInfo: " + weatherInfo);
            if (weatherInfo.indexOf("阴") != -1) {
                //gifView.setGifImage(R.drawable.overcast);
                Glide.with(this).load(R.drawable.overcast).placeholder(R.drawable.overcast).into(bkgndImage);

            }
            if (weatherInfo.indexOf("多云") != -1) {
                //gifView.setGifImage(R.drawable.cloud);
                Glide.with(this).load(R.drawable.cloud).placeholder(R.drawable.cloud).into(bkgndImage);
            }
            if (weatherInfo.indexOf("雨") != -1) {
                //gifView.setGifImage(R.drawable.rain);
                Glide.with(this).load(R.drawable.rain).placeholder(R.drawable.rain).into(bkgndImage);
            }
            if (weatherInfo.indexOf("雷阵雨") != -1) {
                //gifView.setGifImage(R.drawable.thunder);
                Glide.with(this).load(R.drawable.thunder).placeholder(R.drawable.thunder).into(bkgndImage);
            }
            if (weatherInfo.indexOf("晴") != -1) {
              //  GifDrawable gifDrawable = new GifDrawable(getResources(),R.drawable.sun);
                //gifView.setGifImage(R.drawable.sun);
                Glide.with(this).load(R.drawable.sun).placeholder(R.drawable.sun).into(bkgndImage);
            }
            if (weatherInfo.indexOf("雪") != -1) {
                //gifView.setGifImage(R.drawable.snowy);
                Glide.with(this).load(R.drawable.snowy).placeholder(R.drawable.snowy).into(bkgndImage);
            }else {
                Log.i("Main2Activity", "else");
            }

            //gifView.setShowDimension(displayWidth, displayHeight);
            weatherPause = weatherInfo;
            //gifView.setGifImageType(GifView.GifImageType.COVER);
        }

        }

        @Override
        public void onStop() {
            weatherPause = "";
            super.onStop();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<String> citysList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm,List<String> citysList) {
            super(fm);
            this.citysList = citysList;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //String city = position + 1+"";
            String city = citysList.get(position);
            Log.i("Main2Activity.this","city: " + city);
            return PlaceholderFragment.newInstance(city);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return citysList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return citysList.get(position);
        }
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        locService.unregisterListener(listener); //注销掉监听
        locService.stop(); //停止定位服务
        bIsSendRes = true;
        super.onStop();
    }

    /***
     * 定位结果回调，在此方法中处理定位结果
     */
    BDLocationListener listener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer();

                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }

                String city = location.getCity();
                locationCity = city.substring(0,city.indexOf("市"));
                Log.i("Main2Activity","city: " + city);

                runOnUiThread(new Runnable() {
                    @Override
                   public void run() {

                        List<SelectCity> citys = DataSupport.where("isLocationCity != ?","0").find(SelectCity.class);
                        if(citys.isEmpty()){
                            //SelectCity selectCity = DataSupport.find(SelectCity.class, 1);
                            SelectCity selectCity = new SelectCity();
                            selectCity.setCityName(locationCity);
                            selectCity.setLocationCity(true);
                            selectCity.save();
                            citysList.add(locationCity);
                        }else{
                            SelectCity selectCity = new SelectCity();
                            selectCity.setCityName(locationCity);
                            selectCity.updateAll("isLocationCity != ?","0");
                        }
                        //mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),citysList);
                        mSectionsPagerAdapter.notifyDataSetChanged();
                        //mViewPager.setAdapter(mSectionsPagerAdapter);
                   }
                });

            }
        }

        public void onConnectHotSpotMessage(String s, int i){
        }
    };


    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
			/*
			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)){
                return true;
            }else{
                permissionsList.add(permission);
                return false;
            }

        }else{
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ContextCompat.checkSelfPermission(Main2Activity.this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locService.start();
        }else{
            getPersimmions();
        }
    }
}
