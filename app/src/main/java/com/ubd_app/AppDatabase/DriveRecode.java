package com.ubd_app.AppDatabase;

import android.app.KeyguardManager;
import android.util.Log;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "DriveRecode")
public class DriveRecode {

    @PrimaryKey(autoGenerate = true)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //들어가야할 데이터
    private String Date;

    private String StartingPoint;
    private String Destination;

    private double Weight = 0;

    private double TotalMileage = 0;
    private int TotalDrivingTime = 0;

    private double AverageSpeed = 0;
    private double TopSpeed = 0;

    private double Kcal = 0;
    private String latitude = "";
    private String longitude = "";

    public DriveRecode() {

    }

    public DriveRecode(String date, String startingPoint, String destination, int totalMileage, int totalDrivingTime, double topSpeed, String latitude, String longitude, double Weight) {
        this.Date = date;
        this.StartingPoint = startingPoint;
        this.Destination = destination;
        this.TotalMileage = totalMileage;
        this.TotalDrivingTime = totalDrivingTime;
        this.TopSpeed = topSpeed;
        this.Weight = Weight;
        this.latitude = latitude;
        this.longitude = longitude;
        this.AverageSpeed = getAvgSpeed(totalMileage, totalDrivingTime);
        this.Kcal = getCalKcal(Weight, getAvgSpeed(totalMileage,totalDrivingTime), totalDrivingTime);
        Log.d("lll",String.valueOf(Kcal));
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getStartingPoint() {
        return StartingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        StartingPoint = startingPoint;
    }

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }

    public double getTotalMileage() {
        return TotalMileage;
    }

    public void setTotalMileage(double totalMileage) {
        TotalMileage = totalMileage;
    }

    public int getTotalDrivingTime() {
        return TotalDrivingTime;
    }

    public void setTotalDrivingTime(int totalDrivingTime) {
        TotalDrivingTime = totalDrivingTime;
    }

    public double getAverageSpeed() {
        return AverageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        AverageSpeed = averageSpeed;
    }

    public double getTopSpeed() {
        return TopSpeed;
    }

    public void setTopSpeed(double topSpeed) {
        TopSpeed = topSpeed;
    }

    public double getKcal() {
        return Kcal;
    }

    public void setKcal(double kcal) {
        Kcal = kcal;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public double getWeight() {
        return Weight;
    }

    public void setWeight(double weight) {
        this.Weight = weight;
    }

    @Override
    public String toString() {
        return "DriveRecode{" +
                "id=" + id +
                ", Date='" + Date + '\'' +
                ", StartingPoint='" + StartingPoint + '\'' +
                ", Destination='" + Destination + '\'' +
                ", TotalMileage=" + TotalMileage +
                ", TotalDrivingTime=" + TotalDrivingTime +
                ", AverageSpeed=" + AverageSpeed +
                ", TopSpeed=" + TopSpeed +
                ", Kcal=" + Kcal +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }

    private double getAvgSpeed(int totalRideDis, int totalRideTime) {
        return (totalRideDis * 3.6) / (totalRideTime);
    }

    private double getCalKcal(double weight, double avgSpeed, int time) {

        double value = 0;

        if (avgSpeed < 13) {
            value = 0.43;
        } else if (13 <= avgSpeed && avgSpeed < 16) {
            value = 0.0650;
        } else if (16 <= avgSpeed && avgSpeed < 19) {
            value = 0.0783;
        } else if (19 <= avgSpeed && avgSpeed < 22) {
            value = 0.0939;
        } else if (22 <= avgSpeed && avgSpeed < 24) {
            value = 0.113;
        } else if (24 <= avgSpeed && avgSpeed < 26) {
            value = 0.124;
        } else if (26 <= avgSpeed && avgSpeed < 27) {
            value = 0.136;
        } else if (27 <= avgSpeed && avgSpeed < 29) {
            value = 0.149;
        } else if (29 <= avgSpeed && avgSpeed < 31) {
            value = 0.163;
        } else if (31 <= avgSpeed && avgSpeed < 32) {
            value = 0.179;
        } else if (32 <= avgSpeed && avgSpeed < 34) {
            value = 0.196;
        } else if (34 <= avgSpeed && avgSpeed < 37) {
            value = 0.215;
        } else if (37 <= avgSpeed && avgSpeed < 40) {
            value = 0.259;
        } else if (40 <= avgSpeed) {
            value = 0.311;
        }

        double ctime = ((time / 60) % 60);

        if (ctime==0){
            ctime = 1;
        }
        // (몸무게 + 10) * 칼로리 소비계수 * 분
        return ((weight + 10) * value * ctime);
    }
}

