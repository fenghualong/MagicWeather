package com.xidian.flyhigher.magicweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by fhl on 2017/4/1.
 */

public class SelectCity extends DataSupport {
    private int id;

    private String cityName;

    private boolean isLocationCity;

    public boolean isLocationCity() {
        return isLocationCity;
    }

    public void setLocationCity(boolean locationCity) {
        isLocationCity = locationCity;
    }

    public int getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
