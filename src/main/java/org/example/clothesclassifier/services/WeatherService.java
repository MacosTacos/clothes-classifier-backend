package org.example.clothesclassifier.services;

import org.example.clothesclassifier.dtos.WeatherData;
import org.example.clothesclassifier.dtos.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Service
public class WeatherService {
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${API_KEY}")
    private String apiKey;
    private final Random random = new Random();

    public WeatherData getWeatherForLocation(double latitude, double longitude, boolean forecast) {
        long temp;
        if (forecast) {
            temp = Math.round(getCurrentTemperature(latitude, longitude));
        } else {
            temp = random.nextInt(46) - 10;
        }
        return new WeatherData(temp);
    }

    private double getCurrentTemperature(double latitude, double longitude) {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&units=metric&lang=ru&appid=%s",
                latitude, longitude, apiKey
        );

        WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);
        return response != null ? response.getMain().getTemp() : Double.NaN;
    }
}