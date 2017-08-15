package com.xidian.flyhigher.magicweather.gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    @SerializedName("drsg")
    public CarWash drsg;

    @SerializedName("flu")
    public CarWash flu;

    @SerializedName("uv")
    public CarWash uv;

    public Sport sport;

    public class Uv {

        @SerializedName("txt")
        public String info;

    }

    public class Flu {

        @SerializedName("txt")
        public String info;

    }

    public class Drsg {

        @SerializedName("txt")
        public String info;

    }

    public class Comfort {

        @SerializedName("txt")
        public String info;

    }

    public class CarWash {

        @SerializedName("txt")
        public String info;

    }

    public class Sport {

        @SerializedName("txt")
        public String info;

    }

}
