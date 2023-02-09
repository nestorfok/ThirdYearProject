package com.example.sushiroo;

public class OWLEntity {

    private float calorie;
    private float price;
    private String sushiName;

    public OWLEntity() {

    }

    public OWLEntity(String sushiName, float calorie, float price) {
        this.sushiName = sushiName;
        this.calorie = calorie;
        this.price = price;
    }

    public String getSushiName() {
        return sushiName;
    }

    public float getCalorie() {
        return calorie;
    }

    public float getPrice() {
        return price;
    }

    public void setSushiName(String sushiName) {
        this.sushiName = sushiName;
    }

    public void setCalorie(float calorie) {
        this.calorie = calorie;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
