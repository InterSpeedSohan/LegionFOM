package com.example.legionfom.dataModel;

public class StockShowByTerrDataModel {
    private String territory, volume, value;

    public StockShowByTerrDataModel(String territory, String volume, String value)
    {
        this.territory = territory;
        this.volume = volume;
        this.value = value;
    }

    public String getTerritory() {
        return territory;
    }

    public void setTerritory(String territory) {
        this.territory = territory;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
}
