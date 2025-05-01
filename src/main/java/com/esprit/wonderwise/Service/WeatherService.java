package com.esprit.wonderwise.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class WeatherService {
    private static final String API_KEY = "9892c6b2029401fa36c0c7241896e2cb"; // Remplacez par votre clé API
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final Map<String, CachedWeather> weatherCache;

    public WeatherService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        this.mapper = new ObjectMapper();
        this.weatherCache = new ConcurrentHashMap<>();
    }

    public WeatherInfo getWeatherForCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            return null;
        }

        // Check cache first but with shorter duration
        CachedWeather cached = weatherCache.get(city.toLowerCase());
        if (cached != null && !cached.isExpired()) {
            return cached.getWeatherInfo();
        }

        try {
            String url = String.format("%s?q=%s&appid=%s&units=metric&lang=fr", BASE_URL, city, API_KEY);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.println("Erreur météo pour " + city + ": " + response.code());
                    return null;
                }

                JsonNode root = mapper.readTree(response.body().string());
                
                if (root.has("main") && root.has("weather") && root.path("weather").isArray() && root.path("weather").size() > 0) {
                    double temperature = root.path("main").path("temp").asDouble();
                    String description = root.path("weather").get(0).path("description").asText();
                    String icon = root.path("weather").get(0).path("icon").asText();
                    double humidity = root.path("main").path("humidity").asDouble();
                    double windSpeed = root.path("wind").path("speed").asDouble();
                    
                    WeatherInfo weatherInfo = new WeatherInfo(
                        temperature,
                        description,
                        icon,
                        humidity,
                        windSpeed
                    );
                    
                    // Cache for just 2 minutes to ensure fresh data
                    weatherCache.put(city.toLowerCase(), new CachedWeather(weatherInfo));
                    
                    return weatherInfo;
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de la météo pour " + city + ": " + e.getMessage());
        }
        return null;
    }

    private static class CachedWeather {
        private final WeatherInfo weatherInfo;
        private final long timestamp;
        private static final long CACHE_DURATION = TimeUnit.MINUTES.toMillis(2); // Cache for 2 minutes only

        public CachedWeather(WeatherInfo weatherInfo) {
            this.weatherInfo = weatherInfo;
            this.timestamp = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_DURATION;
        }

        public WeatherInfo getWeatherInfo() {
            return weatherInfo;
        }
    }

    public static class WeatherInfo {
        private final double temperature;
        private final String description;
        private final String iconCode;
        private final double humidity;
        private final double windSpeed;

        public WeatherInfo(double temperature, String description, String iconCode, double humidity, double windSpeed) {
            this.temperature = temperature;
            this.description = description;
            this.iconCode = iconCode;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
        }

        public double getTemperature() {
            return temperature;
        }

        public String getDescription() {
            return description;
        }

        public String getIconUrl() {
            return String.format("http://openweathermap.org/img/w/%s.png", iconCode);
        }

        public double getHumidity() {
            return humidity;
        }

        public double getWindSpeed() {
            return windSpeed;
        }
    }
}
