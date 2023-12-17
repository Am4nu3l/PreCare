package com.example.preg_women.Screens;

public class UserCurrentLocation {
    private final Double Latitude;
    private final Double Longitude;

    public UserCurrentLocation(Double latitude, Double longitude) {
        Latitude = latitude;
        Longitude = longitude;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }
}
