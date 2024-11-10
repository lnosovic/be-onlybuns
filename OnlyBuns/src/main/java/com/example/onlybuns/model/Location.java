package com.example.onlybuns.model;

public class Location {
    private Integer id;
    private double longitude;
    private double latitude;
    private String Country;
    private String city;

    public Location() {
    }

    public Location(Integer id, double longitude, double latitude, String country, String city) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        Country = country;
        this.city = city;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
