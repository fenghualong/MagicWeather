package com.xidian.flyhigher.magicweather.db;
import org.litepal.crud.DataSupport;

/**
 * Created by fhl on 2017/4/15.
 */

public class AllPCC extends DataSupport {

    private int id;

    private String province;

    private String city;

    private String county;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }
}
