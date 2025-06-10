package org.example.clothesclassifier.dtos;

public class WeatherData {
    private int temp;

    public WeatherData(int temp) {
        this.temp = temp;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }
}