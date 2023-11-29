package com.labs.lab2weather;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit weatherRetrofit;
    private static Retrofit geoTextRetrofit;
    private static Retrofit geoCoordRetrofit;
    public static Retrofit getWeatherRetrofitInstance( String base_url ) {
        if (weatherRetrofit == null) {
            weatherRetrofit = new Retrofit.Builder().baseUrl(base_url).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return weatherRetrofit;
    }

    public static Retrofit getGeoTextRetrofitInstance( String base_url ) {
        if (geoTextRetrofit == null) {
            geoTextRetrofit = new Retrofit.Builder().baseUrl(base_url).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return geoTextRetrofit;
    }

    public static Retrofit getGeoCoordRetrofitInstance( String base_url ) {
        if (geoCoordRetrofit == null) {
            geoCoordRetrofit = new Retrofit.Builder().baseUrl(base_url).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return geoCoordRetrofit;
    }
}
