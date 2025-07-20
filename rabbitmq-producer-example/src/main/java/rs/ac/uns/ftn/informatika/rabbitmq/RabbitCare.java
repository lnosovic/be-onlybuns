package rs.ac.uns.ftn.informatika.rabbitmq;

import java.io.Serializable;

public class RabbitCare implements Serializable {
    private Integer id;
    private String name;
    private double longitude;
    private double latitude;
    private String country;
    private String city;
    public RabbitCare() {
    }

    public RabbitCare(Integer id, String name, double longitude, double latitude, String country, String city) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
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
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
