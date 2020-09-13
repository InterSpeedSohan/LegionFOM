package com.example.legionfom.dataModel;

public class TeamAttendanceDataModel {
    public String id, name, territory, photo, status, time, gps;
    public TeamAttendanceDataModel(String id, String name, String territory, String photo, String status, String time, String gps)
    {
        this.id = id;
        this.name = name;
        this.territory = territory;
        this.photo = photo;
        this.status = status;
        this.time = time;
        this.gps = gps;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTerritory() {
        return territory;
    }

    public void setTerritory(String territory) {
        this.territory = territory;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
