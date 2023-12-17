package com.example.preg_women.Screens;

public class LatLong {
    private Double latitude;
    private Double Longitude;
    private String id;

    public LatLong(Double latitude, Double longitude,String id) {
        this.latitude = latitude;
        this.Longitude = longitude;
        this.id=id;

    }

    public Double getLatitude() {
        return latitude;
    }
    public String getId() {
        return id;
    }
    public void setID( String id) {
        this.id=id;
    }
    public Double getLongitude() {
        return Longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }
}
