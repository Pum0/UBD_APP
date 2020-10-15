package com.ubd_app.AppDatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "userData")
public class userData {


    @PrimaryKey(autoGenerate = true)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //들어가야할 데이터
    private String userID;
    private String PassWord;
    private int userWeight;
    private int syncCheck;

    public userData(int id, String userID, String PassWord, int userWeight, int syncCheck){
        this.id = id;
        this.userID = userID;
        this.PassWord = PassWord;
        this.userWeight = userWeight;
        this.syncCheck = syncCheck;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassWord() {
        return PassWord;
    }

    public void setPassWord(String passWord) {
        PassWord = passWord;
    }

    public int getUserWeight() {
        return userWeight;
    }

    public void setUserWeight(int userWeight) {
        this.userWeight = userWeight;
    }

    public int getSyncCheck() {
        return syncCheck;
    }

    public void setSyncCheck(int syncCheck) {
        this.syncCheck = syncCheck;
    }

    @Override
    public String toString() {
        return "userData{" + +id +
                ", userID='" + userID + '\'' +
                ", PassWord='" + PassWord + '\'' +
                ", userWeight='" + userWeight + '\'' +
                ", sys='" + syncCheck + '\'' +
                '}';
    }
}

