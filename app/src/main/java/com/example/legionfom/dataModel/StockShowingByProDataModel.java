package com.example.legionfom.dataModel;

public class StockShowingByProDataModel {
    private String name, price, volume, value;

    public StockShowingByProDataModel(String name, String price, String volume, String value)
    {
        this.name = name;
        this.price = price;
        this.volume = volume;
        this.value = value;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
