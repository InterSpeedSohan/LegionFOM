package com.example.legionfom.dataModel;

public class SalesByTerrDataModel {
    private String territory, tgtVolume, achVolume, tgtValue, achValue;

    public SalesByTerrDataModel(String territory, String tgtVolume, String achVolume, String tgtValue, String achValue)
    {
        this.territory = territory;
        this.tgtVolume = tgtVolume;
        this.achVolume = achVolume;
        this.tgtValue = tgtValue;
        this.achValue = achValue;
    }

    public String getTerritory() {
        return territory;
    }

    public void setTerritory(String territory) {
        this.territory = territory;
    }

    public String getAchValue() {
        return achValue;
    }

    public void setAchValue(String achValue) {
        this.achValue = achValue;
    }

    public String getAchVolume() {
        return achVolume;
    }

    public void setAchVolume(String achVolume) {
        this.achVolume = achVolume;
    }

    public String getTgtValue() {
        return tgtValue;
    }

    public void setTgtValue(String tgtValue) {
        this.tgtValue = tgtValue;
    }

    public String getTgtVolume() {
        return tgtVolume;
    }

    public void setTgtVolume(String tgtVolume) {
        this.tgtVolume = tgtVolume;
    }
}
