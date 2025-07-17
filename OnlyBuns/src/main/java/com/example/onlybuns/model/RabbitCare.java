package com.example.onlybuns.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "rabbit_care")
public class RabbitCare implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name="name",nullable = false)
    private String Name;
    @Column(name="longitude",nullable = false)
    private double Longitude;
    @Column(name="latitude",nullable = false)
    private double Latitude;
    @Column(name="country",nullable = false)
    private String country;
    @Column(name="city",nullable = false)
    private String city;

    public RabbitCare() {
    }

    public RabbitCare(Integer id, String name, double longitude, double latitude, String country, String city) {
        this.id = id;
        Name = name;
        Longitude = longitude;
        Latitude = latitude;
        this.country = country;
        this.city = city;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
