package com.example.legionfom.dataModel;

public class DailySalesDataModel {
    private String date, saleVolume, saleValue;
    public DailySalesDataModel(String date, String saleVolume, String saleValue)
    {
        this.date = date;
        this.saleVolume = saleVolume;
        this.saleValue = saleValue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSaleValue() {
        return saleValue;
    }

    public void setSaleValue(String saleValue) {
        this.saleValue = saleValue;
    }

    public String getSaleVolume() {
        return saleVolume;
    }

    public void setSaleVolume(String saleVolume) {
        this.saleVolume = saleVolume;
    }
}
