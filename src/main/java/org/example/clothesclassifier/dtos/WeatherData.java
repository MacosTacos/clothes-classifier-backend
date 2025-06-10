package org.example.clothesclassifier.dtos;

public class WeatherData {
    private long temp;

    public WeatherData(long temp) {
        this.temp = temp;
    }

    public long getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }
}