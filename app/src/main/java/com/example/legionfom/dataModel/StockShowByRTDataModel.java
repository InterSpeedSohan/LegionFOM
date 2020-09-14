package com.example.legionfom.dataModel;

public class StockShowByRTDataModel {
    private String retail, dmsCode, updateDate, status, volume, value;

    public StockShowByRTDataModel(String retail, String dmsCode, String updateDate, String status, String volume, String value)
    {
        this.retail = retail;
        this.dmsCode = dmsCode;
        this.updateDate = updateDate;
        this.status = status;
        this.volume = volume;
        this.value = value;
    }

    public String getDmsCode() {
        return dmsCode;
    }

    public void setDmsCode(String dmsCode) {
        this.dmsCode = dmsCode;
    }

    public String getRetail() {
        return retail;
    }

    public void setRetail(String retail) {
        this.retail = retail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
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
