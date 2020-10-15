package com.ubd_app.Safety;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.naver.maps.map.LocationSource;
import com.ubd_app.Main;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GpsInfomation implements LocationListener {

    public double DistanceTraveled = 0;
    public Location PreviousLocation = null;
    public String StartTime = null;
    public int DrivingTime = 0;
    public double speed = 0;
    public double avgSpeed = 0;
    public double HighSpeed = 0;
    public boolean check = true;

    //gps 이동시 연산
    @Override
    public void onLocationChanged(Location location) {
        //GPS가 바 뀔때 마다 하는 작업=
        if (PreviousLocation != null) {
            double deltaTime = (location.getTime() - PreviousLocation.getTime()) / 1000.0;
            DrivingTime += Integer.parseInt(String.valueOf(Math.round(deltaTime)));


            float distance = PreviousLocation.distanceTo(location);
            DistanceTraveled += Double.parseDouble(String.valueOf(distance));

            speed = PreviousLocation.distanceTo(location) / deltaTime * 3600 / 1000;

            if (HighSpeed == 0) {
                HighSpeed = speed;
            } else if (speed > HighSpeed) {
                HighSpeed = speed;
            }

            avgSpeed = DistanceTraveled / DrivingTime * 3600 / 1000;

            PreviousLocation = location;
        } else {
            SimpleDateFormat simpleDate = new SimpleDateFormat("hh:mm:ss");
            StartTime = simpleDate.format(new Date(location.getTime()));
            PreviousLocation = location;

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
