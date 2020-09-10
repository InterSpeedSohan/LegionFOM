package com.example.legionfom.dataModel;

public class AttendanceBySecDataModel {
    private String retail, sec, joinDate, present, training, casualLeave, sickLeave, halfDayLeave, leaveTotal,
        rtClose, absent;
    public AttendanceBySecDataModel(String retail, String sec, String joinDate, String present, String training, String casualLeave, String sickLeave,
                                    String halfDayLeave, String leaveTotal)
    {
        this.retail = retail;
        this.sec = sec;
        this.joinDate = joinDate;
        this.present = present;
        this.training = training;
        this.casualLeave = casualLeave;
        this.sickLeave = sickLeave;
        this.halfDayLeave = halfDayLeave;
        this.leaveTotal = leaveTotal;
    }

    public String getTraining() {
        return training;
    }

    public void setTraining(String training) {
        this.training = training;
    }

    public String getSickLeave() {
        return sickLeave;
    }

    public void setSickLeave(String sickLeave) {
        this.sickLeave = sickLeave;
    }

    public String getRtClose() {
        return rtClose;
    }

    public void setRtClose(String rtClose) {
        this.rtClose = rtClose;
    }

    public String getPresent() {
        return present;
    }

    public void setPresent(String present) {
        this.present = present;
    }

    public String getHalfDayLeave() {
        return halfDayLeave;
    }

    public void setHalfDayLeave(String halfDayLeave) {
        this.halfDayLeave = halfDayLeave;
    }

    public String getCasualLeave() {
        return casualLeave;
    }

    public void setCasualLeave(String casualLeave) {
        this.casualLeave = casualLeave;
    }

    public String getAbsent() {
        return absent;
    }

    public void setAbsent(String absent) {
        this.absent = absent;
    }

    public String getRetail() {
        return retail;
    }

    public void setRetail(String retail) {
        this.retail = retail;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public String getLeaveTotal() {
        return leaveTotal;
    }

    public void setLeaveTotal(String leaveTotal) {
        this.leaveTotal = leaveTotal;
    }

}
