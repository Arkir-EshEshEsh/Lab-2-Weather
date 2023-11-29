package com.labs.lab2weather;

import com.google.gson.annotations.SerializedName;

public class GeoDataModel {
    @SerializedName("lat")
    private float lat;
    @SerializedName("lon")
    private float lon;

    @SerializedName("local_names")
    private Local_names local_names;

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public Local_names getLocal_names() {
        return local_names;
    }

    public static class Local_names {
        @SerializedName("en")
        private String en;

        public String getEn() {
            return en;
        }
    }
}
