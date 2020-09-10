package com.example.legionfom.dataModel;

public class SecDailyAttendanceDataModel {
    private String retailName,secName, secId ,date, present, dayOff, training, casualLeave, sickLeave, halfDayLeave, totalLeave, rtClose, absent, total;

    public SecDailyAttendanceDataModel(String retailName,String secName, String secId,String date, String present, String dayOff, String training, String casualLeave,
                                       String sickLeave, String halfDayLeave, String totalLeave, String rtClose, String absent, String total)
    {
        this.retailName = retailName;
        this.secName = secName;
        this.secId = secId;
        this.date = date;
        this.present = present;
        this.dayOff = dayOff;
        this.training = training;
        this.casualLeave = casualLeave;
        this.sickLeave = sickLeave;
        this.halfDayLeave = halfDayLeave;
        this.totalLeave = totalLeave;
        this.rtClose = rtClose;
        this.absent = absent;
        this.total = total;
    }

    public String getRetailName() {
        return retailName;
    }

    public void setRetailName(String retailName) {
        this.retailName = retailName;
    }

    public String getSecName() {
        return secName;
    }

    public void setSecName(String secName) {
        this.secName = secName;
    }

    public String getSecId() {
        return secId;
    }

    public void setSecId(String secId) {
        this.secId = secId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAbsent() {
        return absent;
    }

    public void setAbsent(String absent) {
        this.absent = absent;
    }

    public String getCasualLeave() {
        return casualLeave;
    }

    public void setCasualLeave(String casualLeave) {
        this.casualLeave = casualLeave;
    }

    public String getDayOff() {
        return dayOff;
    }

    public void setDayOff(String dayOff) {
        this.dayOff = dayOff;
    }

    public String getHalfDayLeave() {
        return halfDayLeave;
    }

    public void setHalfDayLeave(String halfDayLeave) {
        this.halfDayLeave = halfDayLeave;
    }

    public String getPresent() {
        return present;
    }

    public void setPresent(String present) {
        this.present = present;
    }

    public String getRtClose() {
        return rtClose;
    }

    public void setRtClose(String rtClose) {
        this.rtClose = rtClose;
    }

    public String getSickLeave() {
        return sickLeave;
    }

    public void setSickLeave(String sickLeave) {
        this.sickLeave = sickLeave;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTotalLeave() {
        return totalLeave;
    }

    public void setTotalLeave(String totalLeave) {
        this.totalLeave = totalLeave;
    }

    public String getTraining() {
        return training;
    }

    public void setTraining(String training) {
        this.training = training;
    }

}
