package com.labs.lab2weather;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeoCoordApi {
    //http://api.openweathermap.org/geo/1.0/reverse?limit=1&appid=703d2de218953f8c05f7ee21a117fbec&lat=52.968017149999994&lon=36.09949941816104
    String GEO_CORD_BASE_URL = "https://api.openweathermap.org/geo/";
    @GET("1.0/reverse?limit=1&appid=703d2de218953f8c05f7ee21a117fbec")
    Call<List<GeoDataModel>> getCoordData(@Query("lat") float lat, @Query("lon") float lon );
}
