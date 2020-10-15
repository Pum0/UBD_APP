package com.ubd_app.UBDLibrary;

import android.location.Location;
import com.naver.maps.geometry.LatLng;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocationCheck {

    public static boolean GoalInCheck(Location location, Location goalLocation) {
        if ((int) location.distanceTo(goalLocation) < 20) {
            return true;
        }
        return false;
    }


    public static Location CreateLocation(String str) {

        String strings[] = str.split(",");

        Location location = new Location("Point");

        location.setLatitude(Double.parseDouble(strings[1].replace("]", "")));
        location.setLongitude(Double.parseDouble(strings[0].replace("[", "")));

        return location;
    }



    public static List<LatLng> setLatLngList(List<String[]> lineStringList) {
        List<LatLng> list = new ArrayList<>();
        for (int i = 0; i < lineStringList.size(); i++) {
            for (int j = 0; j < lineStringList.get(i).length; j++) {
                String[] s = lineStringList.get(i)[j].split(",");
                for (int l = 0; l < s.length; l += 2)
                    list.add(new LatLng(Double.parseDouble(s[l + 1]), Double.parseDouble(s[l])));
            }
        }
        return list;
    }


    public static String ConverterLatLngToString(LatLng latLng) {
        return Double.toString(latLng.longitude) + "," + Double.toString(latLng.latitude);
    }

    public static String ConverterLocationToString(Location location) {
        return Double.toString(location.getLongitude()) + "," + Double.toString(location.getLatitude());
    }
}
