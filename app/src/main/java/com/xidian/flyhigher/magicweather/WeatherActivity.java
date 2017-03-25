package com.xidian.flyhigher.magicweather;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    private Button navButton;
    private Button shareButton;
    public TextView titleCity;
    private ViewPager viewPager;
    public ImageView[] pointViews;

    private LinearLayout circle_linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        shareButton = (Button) findViewById(R.id.share_button);
        titleCity = (TextView) findViewById(R.id.title_city);
        circle_linearLayout = (LinearLayout) findViewById(R.id.circle);
        // Find the view pager that will allow the user to swipe between fragments
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        // Create an adapter that knows which fragment should be shown on each page
        WeatherFragmentPagerAdapter adapter = new WeatherFragmentPagerAdapter(getSupportFragmentManager(),this);
        initPoint();
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
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
                for (int x = 0; x < pointViews.length; x++) {
                    pointViews[x].setImageResource(R.drawable.circle_gray);
                    if (x == position) {
                        pointViews[x].setImageResource(R.drawable.circle_dot);;
                    }
                }
            }
        });

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Toast.makeText(WeatherActivity.this,"you clicked share image",Toast.LENGTH_SHORT).show();

                View view = v.getRootView();
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

        });


    }

    private void initPoint() {
        pointViews = new ImageView[3];        // 设置对应的小圆点
        for (int x = 0; x < 3; x++) {
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
