package com.ubd_app.AppDatabase;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recentSearchList")
public class SearchWord {



    @PrimaryKey(autoGenerate = true)
    private int id;

    private String Name;
    private String Address;
    private Double Latitude;
    private Double Longitude;

    public SearchWord(String Name, String Address, Double Latitude, Double Longitude) {

        this.Name = Name;
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

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

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
        return Name;
    }

}
