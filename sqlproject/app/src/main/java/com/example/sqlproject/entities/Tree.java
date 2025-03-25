package com.example.sqlproject.entities;

public class Tree {
    private int id;
    private String type;
    private int stock;
    private double price;
    private String imageURL;

    public Tree() {}

    public Tree(int id, String type, int stock, double price, String imageURL) {
        this.id = id;
        this.type = type;
        this.stock = stock;
        this.price = price;
        this.imageURL = imageURL;
    }

    public int getID() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public int getStock() {
        return this.stock;
    }

    public double getPrice() {
        return this.price;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}