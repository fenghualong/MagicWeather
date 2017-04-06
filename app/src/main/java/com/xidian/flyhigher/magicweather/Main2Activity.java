package com.xidian.flyhigher.magicweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.ant.liao.GifView;
import com.xidian.flyhigher.magicweather.db.SelectCity;
import com.xidian.flyhigher.magicweather.gson.Weather;
import com.xidian.flyhigher.magicweather.util.HttpUtil;
import com.xidian.flyhigher.magicweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.xidian.flyhigher.magicweather.R.id.weather;

public class Main2Activity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        initCity();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),citysList);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,ItemListActivity.class);
            startActivity(intent);
            return true;
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
        private TextView textView_title_update_time;
        private RecyclerView recyclerView;
        private GifView gifView;

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
            textView_title_update_time = (TextView) rootView.findViewById(R.id.title_update_time);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_weather);
            gifView = (GifView) rootView.findViewById(R.id.homeBackgroundGif);

            //textView.setText(getString(R.string.section_format) + getArguments().getString(ARG_SECTION_NUMBER));
            Log.i("Main2Activity","getArguments(): " + getArguments().getString(ARG_SECTION_NUMBER));
            Log.i("Main2Activity","R.string.section_format: " + getString(R.string.section_format));

            String weathercity = getArguments().getString(ARG_SECTION_NUMBER);
            requestWeather(weathercity);
            return rootView;
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
            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            String degree = weather.now.temperature + "℃";
            String weatherInfo = weather.now.more.info;


            textView_title_update_time.setText(updateTime);

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
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
