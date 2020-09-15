package com.example.legionfom.dataModel;

public class SalesByProductDataModel {
    private String name, price, tgtVolume, achVolume, tgtValue, achValue;

    public SalesByProductDataModel(String name, String price, String tgtVolume, String achVolume, String tgtValue, String achValue){
        this.name = name;
        this.price = price;
        this.tgtVolume = tgtVolume;
        this.achVolume = achVolume;
        this.tgtValue = tgtValue;
        this.achValue = achValue;
    }

    public String getTgtVolume() {
        return tgtVolume;
    }

    public void setTgtVolume(String tgtVolume) {
        this.tgtVolume = tgtVolume;
    }

    public String getTgtValue() {
        return tgtValue;
    }

    public void setTgtValue(String tgtValue) {
        this.tgtValue = tgtValue;
    }

    public String getAchVolume() {
        return achVolume;
    }

    public void setAchVolume(String achVolume) {
        this.achVolume = achVolume;
    }

    public String getAchValue() {
        return achValue;
    }

    public void setAchValue(String achValue) {
        this.achValue = achValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

}
