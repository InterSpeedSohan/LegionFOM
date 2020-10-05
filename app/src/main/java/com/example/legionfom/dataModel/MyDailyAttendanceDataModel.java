package com.example.legionfom.dataModel;

public class MyDailyAttendanceDataModel {
    public String date,status, time;
    public MyDailyAttendanceDataModel(String date, String status, String time)
    {
        this.date = date;
        this.status = status;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getTime() {
        return time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

