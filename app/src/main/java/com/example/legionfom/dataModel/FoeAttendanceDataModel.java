package com.example.legionfom.dataModel;

public class FoeAttendanceDataModel {
    private String date, foe, foeId , meeting, present, dayOff, training, casualLeave,sickLeave,
    halfDayLeave, leaveTotal, absent, total;
    public FoeAttendanceDataModel(String date, String foe, String foeId, String present, String dayOff, String training, String casualLeave, String sickLeave,
                                  String halfDayLeave, String leaveTotal, String meeting ,String absent, String total)
    {
        this.date = date;
        this.foe = foe;
        this.foeId = foeId;
        this.meeting = meeting;
        this.present = present;
        this.dayOff = dayOff;
        this.training = training;
        this.casualLeave = casualLeave;
        this.sickLeave = sickLeave;
        this.halfDayLeave = halfDayLeave;
        this.leaveTotal = leaveTotal;
        this.absent = absent;
        this.total = total;
    }

    public String getFoeId() {
        return foeId;
    }

    public void setFoeId(String foeId) {
        this.foeId = foeId;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setAbsent(String absent) {
        this.absent = absent;
    }

    public String getAbsent() {
        return absent;
    }

    public void setCasualLeave(String casualLeave) {
        this.casualLeave = casualLeave;
    }

    public String getCasualLeave() {
        return casualLeave;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDayOff() {
        return dayOff;
    }

    public void setDayOff(String dayOff) {
        this.dayOff = dayOff;
    }

    public String getFoe() {
        return foe;
    }

    public void setFoe(String foe) {
        this.foe = foe;
    }

    public String getHalfDayLeave() {
        return halfDayLeave;
    }

    public void setHalfDayLeave(String halfDayLeave) {
        this.halfDayLeave = halfDayLeave;
    }

    public String getLeaveTotal() {
        return leaveTotal;
    }

    public void setLeaveTotal(String leaveTotal) {
        this.leaveTotal = leaveTotal;
    }

    public String getMeeting() {
        return meeting;
    }

    public void setMeeting(String meeting) {
        this.meeting = meeting;
    }

    public String getPresent() {
        return present;
    }

    public void setPresent(String present) {
        this.present = present;
    }

    public String getSickLeave() {
        return sickLeave;
    }

    public void setSickLeave(String sickLeave) {
        this.sickLeave = sickLeave;
    }

    public String getTraining() {
        return training;
    }

    public void setTraining(String training) {
        this.training = training;
    }

}
