package com.weather.weather.interfaces;

public interface WeatherInterface {

    /**
     * Perform flip animation on panel cards.
     */
    void flipCards();

    /**
     * Set a current city to be shown.
     *
     * @param cityName New city name.ß
     */
    void setCity(String cityName);
}
