package com.labs.lab2weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    private String TAG = "MyActivity";
    private EditText cityEdit;
    private TextView cityView, tempView, tempFeelsLikeView, pressView, sunriseView,
            sunsetView, humidityView, weatherDescView, visibilityView, windSpeedView,
            rainfall1hView, rainfall3hView, snowfall1hView, snowfall3hView;
    private Button getWeatherBtn, getWeatherGpsBtn;
    private ProgressBar loadingPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public View.OnClickListener textSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( cityEdit.getText().toString().isEmpty() ) {
                Toast.makeText(getApplicationContext(), R.string.city_edit, Toast.LENGTH_SHORT).show();
                return;
            }

            getWeatherGpsBtn.setEnabled(false);
            getWeatherBtn.setEnabled(false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getWeatherGpsBtn.setEnabled(true);
                    getWeatherBtn.setEnabled(true);
                }
            }, 10000);

            loadingPB.setVisibility(ProgressBar.VISIBLE);

            GeoApi geoApi = RetrofitClient.getGeoTextRetrofitInstance(GeoApi.GEO_BASE_URL).create(GeoApi.class);
            Call<List<GeoDataModel>> geoCall = geoApi.getGeoData(cityEdit.getText().toString());
            geoCall.enqueue(new Callback<List<GeoDataModel>>() {
                @Override
                public void onResponse(Call<List<GeoDataModel>> call, Response<List<GeoDataModel>> response) {
                    try {
                        List<GeoDataModel> geoData = response.body();
                        float lat = geoData.get(0).getLat();
                        float lon = geoData.get(0).getLon();

                        WeatherApi weatherApi = RetrofitClient.getWeatherRetrofitInstance(WeatherApi.WEATHER_BASE_URL).create(WeatherApi.class);
                        Call<WeatherDataModel> weatherCall = weatherApi.getWeatherData(Locale.getDefault().getLanguage(), lat, lon);
                        weatherCall.enqueue(new Callback<WeatherDataModel>() {
                            @Override
                            public void onResponse(Call<WeatherDataModel> call, Response<WeatherDataModel> response) {
                                try {
                                    setText(response);

                                    loadingPB.setVisibility(ProgressBar.GONE);
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), R.string.internet_error, Toast.LENGTH_SHORT).show();
                                    loadingPB.setVisibility(ProgressBar.GONE);
                                }
                            }

                            @Override
                            public void onFailure(Call<WeatherDataModel> call, Throwable t) {

                                Toast.makeText(getApplicationContext(), R.string.internet_error, Toast.LENGTH_SHORT).show();
                                loadingPB.setVisibility(ProgressBar.GONE);
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.internet_error, Toast.LENGTH_SHORT).show();
                        loadingPB.setVisibility(ProgressBar.GONE);
                    }
                }

                @Override
                public void onFailure(Call<List<GeoDataModel>> call, Throwable t) {

                    Toast.makeText(getApplicationContext(), R.string.internet_error, Toast.LENGTH_SHORT).show();
                    loadingPB.setVisibility(ProgressBar.GONE);
                }
            });
        }
    };

    public View.OnClickListener geoSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            loadingPB.setVisibility(ProgressBar.VISIBLE);

            double[] gps = geoLocData();

            if(gps == null) {
                loadingPB.setVisibility(ProgressBar.GONE);
                return;
            }

            getWeatherGpsBtn.setEnabled(false);
            getWeatherBtn.setEnabled(false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getWeatherGpsBtn.setEnabled(true);
                    getWeatherBtn.setEnabled(true);
                }
            }, 10000);

            GeoCoordApi geoCoordApi = RetrofitClient.getGeoCoordRetrofitInstance(GeoCoordApi.GEO_CORD_BASE_URL).create(GeoCoordApi.class);
            Call<List<GeoDataModel>> geoCoordCall = geoCoordApi.getCoordData((float)gps[0], (float)gps[1]);
            geoCoordCall.enqueue(new Callback<List<GeoDataModel>>() {
                @Override
                public void onResponse(Call<List<GeoDataModel>> call, Response<List<GeoDataModel>> response) {
                    try {
                        GeoApi geoApi = RetrofitClient.getGeoTextRetrofitInstance(GeoApi.GEO_BASE_URL).create(GeoApi.class);
                        Call<List<GeoDataModel>> geoCall = geoApi.getGeoData(response.body().get(0).getLocal_names().getEn());
                        geoCall.enqueue(new Callback<List<GeoDataModel>>() {
                            @Override
                            public void onResponse(Call<List<GeoDataModel>> call, Response<List<GeoDataModel>> response) {
                                try {
                                    List<GeoDataModel> geoData = response.body();
                                    float lat = geoData.get(0).getLat();
                                    float lon = geoData.get(0).getLon();

                                    WeatherApi weatherApi = RetrofitClient.getWeatherRetrofitInstance(WeatherApi.WEATHER_BASE_URL).create(WeatherApi.class);
                                    Call<WeatherDataModel> weatherCall = weatherApi.getWeatherData(Locale.getDefault().getLanguage(), lat, lon);
                                    weatherCall.enqueue(new Callback<WeatherDataModel>() {
                                        @Override
                                        public void onResponse(Call<WeatherDataModel> call, Response<WeatherDataModel> response) {
                                            try {
                                                setText(response);

                                                loadingPB.setVisibility(ProgressBar.GONE);
                                            } catch (Exception e) {
                                                Toast.makeText(getApplicationContext(), R.string.internet_error, Toast.LENGTH_SHORT).show();
                                                loadingPB.setVisibility(ProgressBar.GONE);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<WeatherDataModel> call, Throwable t) {

                                            Toast.makeText(getApplicationContext(), R.string.internet_error, Toast.LENGTH_SHORT).show();
                                            loadingPB.setVisibility(ProgressBar.GONE);
                                        }
                                    });
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), R.string.internet_error, Toast.LENGTH_SHORT).show();
                                    loadingPB.setVisibility(ProgressBar.GONE);
                                }
                            }

                            @Override
                            public void onFailure(Call<List<GeoDataModel>> call, Throwable t) {

                                Toast.makeText(getApplicationContext(), R.string.internet_error, Toast.LENGTH_SHORT).show();
                                loadingPB.setVisibility(ProgressBar.GONE);
                            }
                        });
                    } catch (NullPointerException e) {
                        Toast.makeText(getApplicationContext(), R.string.internet_error, Toast.LENGTH_SHORT).show();
                        loadingPB.setVisibility(ProgressBar.GONE);
                    }
                }

                @Override
                public void onFailure(Call<List<GeoDataModel>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.internet_error, Toast.LENGTH_SHORT).show();
                    loadingPB.setVisibility(ProgressBar.GONE);
                }
            });
        }
    };

    private void init() {
        cityEdit = findViewById( R.id.city_text_edit );

        cityView = findViewById( R.id.city_text_view );
        weatherDescView = findViewById( R.id.weather_desc_text_view );
        rainfall1hView = findViewById( R.id.rainfall_1h_text_view );
        rainfall3hView = findViewById( R.id.rainfall_3h_text_view );
        snowfall1hView = findViewById( R.id.snowfall_1h_text_view );
        snowfall3hView = findViewById( R.id.snowfall_3h_text_view );
        tempView = findViewById( R.id.temp_text_view );
        tempFeelsLikeView = findViewById( R.id.temp_feelsLike_text_view );
        humidityView = findViewById( R.id.humidity_text_view );
        windSpeedView = findViewById( R.id.wind_speed_text_view );
        visibilityView = findViewById( R.id.visibility_text_view );
        pressView = findViewById( R.id.pressure_text_view );
        sunriseView = findViewById( R.id.sunrise_text_view );
        sunsetView = findViewById( R.id.sunset_text_view );

        getWeatherBtn = findViewById( R.id.weather_update_button );
        getWeatherGpsBtn = findViewById( R.id.gps_weather_update_button );

        loadingPB = findViewById( R.id.weather_progress_bar );

        getWeatherBtn.setOnClickListener(textSearch);
        getWeatherGpsBtn.setOnClickListener(geoSearch);
    }

    private double[] geoLocData() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            Toast.makeText(getApplicationContext(), getString(R.string.gps_permission_text), Toast.LENGTH_SHORT).show();
            return null;
        } else {
            locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);

            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(getApplicationContext(), R.string.gps_disabled_text, Toast.LENGTH_SHORT).show();
                return null;
            }

            List<String> providers = locationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {

                    bestLocation = l;
                }
            }

            double[] gps = new double[2];

            try {
                gps[0] = bestLocation.getLatitude();
                gps[1] = bestLocation.getLongitude();
            } catch(NullPointerException e) {
                Toast.makeText(getApplicationContext(), R.string.internet_error, Toast.LENGTH_SHORT).show();
                return null;
            }

            return gps;
        }
    }

    private void setText(Response<WeatherDataModel> response) {
        String name;
        try {
            name = response.body().getCityName();
        } catch( NullPointerException e) {
            name = "";
        }

        String weatherDesc;
        try {
            weatherDesc = response.body().getWeather().get(0).getDescription();
        } catch( NullPointerException e) {
            weatherDesc = "";
        }

        float temp;
        try {
            temp = response.body().getMain().getTemp();
        } catch( NullPointerException e) {
            temp = 0;
        }

        float tempFeelsLike;
        try {
            tempFeelsLike = response.body().getMain().getTempFeelsLike();
        } catch( NullPointerException e) {
            tempFeelsLike = 0;
        }

        float pressure;
        try {
            pressure = response.body().getMain().getPressure();
        } catch( NullPointerException e) {
            pressure = 0;
        }

        float windSpeed;
        try {
            windSpeed = response.body().getWind().getSpeed();
        } catch( NullPointerException e) {
            windSpeed = 0;
        }

        int sunrise;
        try {
            sunrise = response.body().getSys().getSunriseTime();
        } catch( NullPointerException e) {
            sunrise = 0;
        }

        int sunset;
        try {
            sunset = response.body().getSys().getSunsetTime();
        } catch( NullPointerException e) {
            sunset = 0;
        }

        int humidity;
        try {
            humidity = response.body().getMain().getHumidity();
        } catch( NullPointerException e) {
            humidity = 0;
        }

        int visibility;
        try {
            visibility = response.body().getVisibility();
        } catch( NullPointerException e) {
            visibility = 0;
        }

        float rainfall1h;
        try {
            rainfall1h = response.body().getRain().getRain1h();
        } catch( NullPointerException e) {
            rainfall1h = 0;
        }

        if(rainfall1h == 0) {
            rainfall1hView.setVisibility(View.GONE);
        } else {
            rainfall1hView.setVisibility(View.VISIBLE);
        }

        float rainfall3h;
        try {
            rainfall3h = response.body().getRain().getRain3h();
        } catch( NullPointerException e) {
            rainfall3h = 0;
        }

        if(rainfall3h == 0) {
            rainfall3hView.setVisibility(View.GONE);
        } else {
            rainfall3hView.setVisibility(View.VISIBLE);
        }

        float snowfall1h;
        try {
            snowfall1h = response.body().getSnow().getSnow1h();
        } catch( NullPointerException e) {
            snowfall1h = 0;
        }

        if(snowfall1h == 0) {
            snowfall1hView.setVisibility(View.GONE);
        } else {
            snowfall1hView.setVisibility(View.VISIBLE);
        }

        float snowfall3h;
        try {
            snowfall3h = response.body().getSnow().getSnow3h();
        } catch( NullPointerException e) {
            snowfall3h = 0;
        }

        if(snowfall3h == 0) {
            snowfall3hView.setVisibility(View.GONE);
        } else {
            snowfall3hView.setVisibility(View.VISIBLE);
        }

        Date sunriseDate = new Date((long) sunrise * 1000);
        Date sunsetDate = new Date((long) sunset * 1000);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));

        cityView.setText(getString(R.string.city_text) + " " + name);
        weatherDescView.setText(getString(R.string.weather_main_text) + " " + weatherDesc);
        rainfall1hView.setText(getString(R.string.rain_1h_text) + " " + rainfall1h + " " + getString(R.string.unit_mm_text));
        rainfall3hView.setText(getString(R.string.rain_3h_text) + " " + rainfall3h + " " + getString(R.string.unit_mm_text));
        snowfall1hView.setText(getString(R.string.snow_1h_text) + " " + snowfall1h + " " + getString(R.string.unit_mm_text));
        snowfall3hView.setText(getString(R.string.snow_3h_text) + " " + snowfall3h + " " + getString(R.string.unit_mm_text));
        tempView.setText(getString(R.string.temp_text) + " " + temp + " °C");
        tempFeelsLikeView.setText(getString(R.string.temp_FeelsLike_text) + " " + tempFeelsLike  + " °C");
        humidityView.setText(getString(R.string.humidity_text) + " " + humidity + "%");
        windSpeedView.setText(getString(R.string.wind_speed_text) + " " + windSpeed + " " + getString(R.string.unit_metrePerSecond_text));
        visibilityView.setText(getString(R.string.visibility_text) + " " + visibility + " " + getString(R.string.unit_metre_text));
        pressView.setText(getString(R.string.pressure_text) + " " + String.valueOf(pressure * 0.750064f) + " " + getString(R.string.unit_pressure_text));
        sunriseView.setText(getString(R.string.sunrise_text) + " " + format.format(sunriseDate));
        sunsetView.setText(getString(R.string.sunset_text) + " " + format.format(sunsetDate));
    }
}