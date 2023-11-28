package com.labs.lab2weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    String WEATHER_BASE_URL = "https://api.openweathermap.org/data/";
    @GET("2.5/weather?units=metric&appid=703d2de218953f8c05f7ee21a117fbec")
    Call<WeatherDataModel> getWeatherData( @Query("lang") String lang, @Query("lat") float lat, @Query("lon") float lon );
}
