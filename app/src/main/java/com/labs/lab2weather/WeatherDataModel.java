package com.labs.lab2weather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherDataModel {
    @SerializedName("name")
    private String cityName;

    @SerializedName("main")
    private Main main;

    @SerializedName("sys")
    private Sys sys;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("weather")
    private List<Weather> weather;

    @SerializedName("visibility")
    private int visibility;

    @SerializedName("rain")
    private Rain rain;

    @SerializedName("snow")
    private Snow snow;

    public String getCityName() {
        return cityName;
    }

    public int getVisibility() {
        return visibility;
    }

    public Main getMain() {
        return main;
    }

    public Sys getSys() {
        return sys;
    }

    public Wind getWind() {
        return wind;
    }

    public Rain getRain() {
        return rain;
    }

    public Snow getSnow() {
        return snow;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public static class Main {
        @SerializedName("temp")
        private float temp;
        @SerializedName("feels_like")
        private float tempFeelsLike;
        @SerializedName("pressure")
        private int pressure;
        @SerializedName("humidity")
        private int humidity;

        public float getTemp() {
            return temp;
        }

        public float getTempFeelsLike() {
            return tempFeelsLike;
        }

        public int getPressure() {
            return pressure;
        }

        public int getHumidity() {
            return humidity;
        }
    }

    public static class Sys {
        @SerializedName("sunrise")
        private int sunriseTime;
        @SerializedName("sunset")
        private int sunsetTime;

        public int getSunriseTime() {
            return sunriseTime;
        }

        public int getSunsetTime() {
            return sunsetTime;
        }
    }

    public static class Weather {
        @SerializedName("description")
        private String description;

        public String getDescription() {
            return description;
        }
    }

    public static class Wind {
        @SerializedName("speed")
        private float speed;

        public float getSpeed() {
            return speed;
        }
    }

    public static class Rain {
        @SerializedName("1h")
        private float rain1h;
        @SerializedName("2h")
        private float rain3h;

        public float getRain1h() {
            return rain1h;
        }

        public float getRain3h() {
            return rain3h;
        }
    }

    public static class Snow {
        @SerializedName("1h")
        private float snow1h;
        @SerializedName("2h")
        private float snow3h;

        public float getSnow1h() {
            return snow1h;
        }

        public float getSnow3h() {
            return snow3h;
        }
    }
}
