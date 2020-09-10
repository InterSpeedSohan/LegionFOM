package com.example.legionfom.dataModel;

public class MyDailyAttendanceDataModel {
    public String date,status;
    public MyDailyAttendanceDataModel(String date, String status)
    {
        this.date = date;
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}
