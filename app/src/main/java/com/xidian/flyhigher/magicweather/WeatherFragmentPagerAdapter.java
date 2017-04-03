package com.xidian.flyhigher.magicweather;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by fhl on 2017/3/25.
 */

public class WeatherFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    public WeatherFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new WeatherFragment();
        } else if (position == 1){
            return new WeatherSecondFragment();
        } else {
            return new WeatherFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
