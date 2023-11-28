package com.labs.lab2weather;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeoApi {
    String GEO_BASE_URL = "https://api.openweathermap.org/geo/";
    @GET("1.0/direct?limit=1&appid=703d2de218953f8c05f7ee21a117fbec")
    Call<List<GeoDataModel>> getGeoData(@Query("q") String city );
}
