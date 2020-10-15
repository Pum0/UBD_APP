package com.ubd_app.AppDatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "FavoriteList")
public class FavoriteDB {

    @PrimaryKey(autoGenerate = true)
    private int id;

    //place) 1 : 집 , 2 : 회사 , 3이상 : 일반 즐겨찾기
    private int Value;
    private String Place;
    private String Address;
    private Double Latitude;
    private Double Longitude;


    public FavoriteDB(int Value, String Place, String Address, Double Latitude, Double Longitude) {
        this.Value = Value;
        this.Place = Place;
        this.Address = Address;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        this.Value = value;
    }

    public String getPlace() { return Place; }

    public void setPlace(String place) { this.Place = place; }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    @Override
    public String toString() {
        return Place;
    }
}

