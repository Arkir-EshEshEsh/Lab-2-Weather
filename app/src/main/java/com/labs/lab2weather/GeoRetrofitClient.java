package com.labs.lab2weather;


        import retrofit2.Retrofit;
        import retrofit2.converter.gson.GsonConverterFactory;

public class GeoRetrofitClient {

    private static Retrofit retrofit;
    public static Retrofit getRetrofitInstance( String base_url ) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(base_url).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}