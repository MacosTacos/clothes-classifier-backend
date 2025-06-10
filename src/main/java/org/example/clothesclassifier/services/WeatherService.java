package org.example.clothesclassifier.services;

import org.example.clothesclassifier.dtos.WeatherData;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class WeatherService {
    private final Random random = new Random();

    public WeatherData getWeatherForLocation(double latitude, double longitude) {
        int temp = random.nextInt(46) - 10;
        return new WeatherData(temp);
    }
}