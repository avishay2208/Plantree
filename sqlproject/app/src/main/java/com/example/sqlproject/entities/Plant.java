package com.example.sqlproject.entities;

public class Plant {

    private int plantID;
    private int userID;
    private String userName;
    private int treeID;
    private String treeType;
    private String plantAddress;
    private String plantDate;
    private double price;

    public Plant() {}

    public Plant(int plantID, int userID, String userName, int treeID, String treeType,String plantAddress,String plantDate, double price) {
        this.plantID = plantID;
        this.userID = userID;
        this.userName = userName;
        this.treeID = treeID;
        this.treeType = treeType;
        this.plantAddress = plantAddress;
        this.plantDate = plantDate;
        this.price = price;
    }


    public int getPlantID() {
        return plantID;
    }

    public int getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public int getTreeID() {
        return treeID;
    }

    public String getTreeType() {
        return treeType;
    }

    public String getPlantAddress() {
        return plantAddress;
    }

    public String getPlantDate() {
        return plantDate;
    }

    public double getPrice() {
        return price;
    }
}