package com.example.sqlproject.entities;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private boolean admin;
    private String email;
    private String password;
    private String phoneNumber;
    private int plantCounter;
    private String joinDate;

    public User() {}

    public User(int id, String firstName, String lastName, boolean admin, String email, String password, String phoneNumber, int plantCounter, String joinDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.admin = admin;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.plantCounter = plantCounter;
        this.joinDate = joinDate;
    }

    public int getID() {
        return id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public int getPlantCounter() {
        return plantCounter;
    }

    public void setPlantCounter(int plantCounter) {
        this.plantCounter = plantCounter;
    }

    public String getJoinDate() {
        return this.joinDate;
    }

    public boolean isAdmin() {
        return this.admin;
    }

    public void setIsAdmin(boolean admin) {
        this.admin = admin;
    }
}