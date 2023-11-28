package com.labs.lab2weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

//https://api.openweathermap.org/data/2.5/weather?lat=52.9685433&lon=36.0692477&units=metric&lang=ru&appid=703d2de218953f8c05f7ee21a117fbec
//Geo api https://api.openweathermap.org/geo/1.0/direct?q=Орёл&limit=1&appid=703d2de218953f8c05f7ee21a117fbec

public class MainActivity extends AppCompatActivity {

    LocationManager LocationManager;
    private EditText cityEdit;
    private TextView cityView, tempView, tempFeelsLikeView, pressView, sunriseView, sunsetView, humidityView, weatherDescView, visibilityView, windSpeedView;
    private Button getWeatherBtn, getWeatherGpsBtn;
    private ProgressBar loadingPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        getWeatherBtn.setOnClickListener(textSearch);
        getWeatherGpsBtn.setOnClickListener(geoSearch);
    }

    public View.OnClickListener textSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( cityEdit.getText().toString().isEmpty() ) {
                Toast.makeText(getApplicationContext(), R.string.city_edit, Toast.LENGTH_SHORT).show();
                return;
            }

            loadingPB.setVisibility(ProgressBar.VISIBLE);

            GeoApi geoApi = GeoRetrofitClient.getRetrofitInstance(GeoApi.GEO_BASE_URL).create(GeoApi.class);
            Call<List<GeoDataModel>> geoCall = geoApi.getGeoData(cityEdit.getText().toString());
            geoCall.enqueue(new Callback<List<GeoDataModel>>() {
                @Override
                public void onResponse(Call<List<GeoDataModel>> call, Response<List<GeoDataModel>> response) {
                    try {
                        List<GeoDataModel> geoData = response.body();
                        float lat = geoData.get(0).getLat();
                        float lon = geoData.get(0).getLon();

                        WeatherApi weatherApi = WeatherRetrofitClient.getRetrofitInstance(WeatherApi.WEATHER_BASE_URL).create(WeatherApi.class);
                        Call<WeatherDataModel> weatherCall = weatherApi.getWeatherData(Locale.getDefault().getLanguage(), lat, lon);
                        weatherCall.enqueue(new Callback<WeatherDataModel>() {
                            @Override
                            public void onResponse(Call<WeatherDataModel> call, Response<WeatherDataModel> response) {
                                try {
                                    String name = response.body().getCityName();
                                    String weatherDesc = response.body().getWeather().get(0).getDescription();
                                    float temp = response.body().getMain().getTemp();
                                    float tempFeelsLike = response.body().getMain().getTempFeelsLike();
                                    float pressure = response.body().getMain().getPressure();
                                    float windSpeed = response.body().getWind().getSpeed();
                                    int sunrise = response.body().getSys().getSunriseTime();
                                    int sunset = response.body().getSys().getSunsetTime();
                                    int humidity = response.body().getMain().getHumidity();
                                    int visibility = response.body().getVisibility();

                                    Date sunriseDate = new Date((long) sunrise * 1000);
                                    Date sunsetDate = new Date((long) sunset * 1000);
                                    SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss a");
                                    format.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));

                                    cityView.setText(getString(R.string.city_text) + " " + name);
                                    weatherDescView.setText(getString(R.string.weather_main_text) + " " + weatherDesc);
                                    tempView.setText(getString(R.string.temp_text) + " " + String.valueOf(temp) + " °C");
                                    tempFeelsLikeView.setText(getString(R.string.temp_FeelsLike_text) + " " + String.valueOf(tempFeelsLike)  + " °C");
                                    humidityView.setText(getString(R.string.humidity_text) + " " + humidity + "%");
                                    windSpeedView.setText(getString(R.string.wind_speed_text) + " " + windSpeed + " " + getString(R.string.metrePerSecond_text));
                                    visibilityView.setText(getString(R.string.visibility_text) + " " + visibility + " " + getString(R.string.metre_text));
                                    pressView.setText(getString(R.string.pressure_text) + " " + String.valueOf(pressure * 0.750064f) + " " + getString(R.string.pressure_unit_text));
                                    sunriseView.setText(getString(R.string.sunrise_text) + " " + format.format(sunriseDate));
                                    sunsetView.setText(getString(R.string.sunset_text) + " " + format.format(sunsetDate));

                                    loadingPB.setVisibility(ProgressBar.GONE);
                                } catch (Exception e) {
                                    cityView.setText(e.toString());
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

            if (gps == null &&
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                loadingPB.setVisibility(ProgressBar.GONE);
                Toast.makeText(getApplicationContext(), getString(R.string.gps_permission_text), Toast.LENGTH_SHORT).show();
                return;
            } else if(gps == null) {
                loadingPB.setVisibility(ProgressBar.GONE);
                Toast.makeText(getApplicationContext(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                return;
            }

            WeatherApi weatherApi = WeatherRetrofitClient.getRetrofitInstance(WeatherApi.WEATHER_BASE_URL).create(WeatherApi.class);
            Call<WeatherDataModel> weatherCall = weatherApi.getWeatherData(Locale.getDefault().getLanguage(), (float)gps[0], (float)gps[1]);
            weatherCall.enqueue(new Callback<WeatherDataModel>() {
                @Override
                public void onResponse(Call<WeatherDataModel> call, Response<WeatherDataModel> response) {
                    try {
                        String name = response.body().getCityName();
                        String weatherDesc = response.body().getWeather().get(0).getDescription();
                        float temp = response.body().getMain().getTemp();
                        float tempFeelsLike = response.body().getMain().getTempFeelsLike();
                        float pressure = response.body().getMain().getPressure();
                        float windSpeed = response.body().getWind().getSpeed();
                        int sunrise = response.body().getSys().getSunriseTime();
                        int sunset = response.body().getSys().getSunsetTime();
                        int humidity = response.body().getMain().getHumidity();
                        int visibility = response.body().getVisibility();

                        Date sunriseDate = new Date((long) sunrise * 1000);
                        Date sunsetDate = new Date((long) sunset * 1000);
                        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss a");
                        format.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));

                        cityView.setText(getString(R.string.city_text) + " " + name);
                        weatherDescView.setText(getString(R.string.weather_main_text) + " " + weatherDesc);
                        tempView.setText(getString(R.string.temp_text) + " " + String.valueOf(temp) + " °C");
                        tempFeelsLikeView.setText(getString(R.string.temp_FeelsLike_text) + " " + String.valueOf(tempFeelsLike)  + " °C");
                        humidityView.setText(getString(R.string.humidity_text) + " " + humidity + "%");
                        windSpeedView.setText(getString(R.string.wind_speed_text) + " " + windSpeed + " " + getString(R.string.metrePerSecond_text));
                        visibilityView.setText(getString(R.string.visibility_text) + " " + visibility + " " + getString(R.string.metre_text));
                        pressView.setText(getString(R.string.pressure_text) + " " + String.valueOf(pressure * 0.750064f) + " " + getString(R.string.pressure_unit_text));
                        sunriseView.setText(getString(R.string.sunrise_text) + " " + format.format(sunriseDate));
                        sunsetView.setText(getString(R.string.sunset_text) + " " + format.format(sunsetDate));

                        loadingPB.setVisibility(ProgressBar.GONE);
                    } catch (Exception e) {
                        cityView.setText(e.toString());
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
        }
    };

    private void init() {
        //geoLocWeatherData();

        cityEdit = findViewById( R.id.city_text_edit );

        cityView = findViewById( R.id.city_text_view );
        weatherDescView = findViewById( R.id.weather_desc_text_view );
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
    }

    private double[] geoLocData() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return null;
        } else {
            LocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
            List<String> providers = LocationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = LocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {

                    bestLocation = l;
                }
            }

            double[] gps = new double[2];

            gps[0] = bestLocation.getLatitude();
            gps[1] = bestLocation.getLongitude();

            return gps;
        }
    }
}