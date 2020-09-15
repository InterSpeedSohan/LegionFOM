package com.example.legionfom.dataModel;

public class SalesByRTDataModel {
    private String retailer, dmsCode, tgtVolume, achVolume, tgtValue, achValue;

    public SalesByRTDataModel(String retailer, String dmsCode, String tgtVolume, String achVolume, String tgtValue, String achValue)
    {
        this.retailer = retailer;
        this.dmsCode = dmsCode;
        this.tgtVolume = tgtVolume;
        this.achVolume = achVolume;
        this.tgtValue = tgtValue;
        this.achValue = achValue;
    }

    public String getDmsCode() {
        return dmsCode;
    }

    public void setDmsCode(String dmsCode) {
        this.dmsCode = dmsCode;
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

    public String getRetailer() {
        return retailer;
    }

    public void setRetailer(String retailer) {
        this.retailer = retailer;
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
