package com.labs.lab2weather;

import com.google.gson.annotations.SerializedName;

public class GeoDataModel {
    @SerializedName("lat")
    float lat;
    @SerializedName("lon")
    float lon;

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }


}
