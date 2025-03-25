package com.example.sqlproject.entities;

public class Location {
    private int id;
    private String address;
    private double latitude;
    private double longitude;

    public Location() {}

    public Location(int id, String address, double latitude, double longitude) {
        this.id = id;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getID() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }
}