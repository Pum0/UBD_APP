package com.ubd_app.Ride;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.ubd_app.AppDatabase.DriveRecode;
import com.ubd_app.KeyValues;
import com.ubd_app.Main;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

public class RideRecordObject implements KeyValues {

    private Main main;

    private String DocumentID;
    private double weight =0;
    //미터단위
    private int totalRideDis;
    //초단위
    private int totalRideTime;
    //최고속도
    private double MaxSpeed = 0;
    //출,도착지
    private String StartPoint;
    private String DestinationPoint;

    //개인정보

    private List<Double> latitudeArray = new ArrayList<>();
    private List<Double> longitudeArray = new ArrayList<>();
    public RideRecordObject(){

    }

    public RideRecordObject(String StartPoint, String DestinationPoint, double weight, String documentID, Main main) {
        this.StartPoint = StartPoint;
        this.DestinationPoint = DestinationPoint;
        this.DocumentID = documentID;
        this.weight = weight;

        this.main = main;
    }

    public void AddGeoArray(double latitude, double longitude) {
        latitudeArray.add(latitude);
        longitudeArray.add(longitude);
    }

    public void AddTotalRideTime(){
        totalRideTime += 1;
    }

    public void AddTotalRideDis(int distance){
        totalRideDis += distance;
    }

    public String toString() {
        return "\"writer\" : " + "\"5f154add670ed53fd40cf935\"" +
                ", \"totalRideDis\" : " + "\""+totalRideDis+"\"" +
                ", \"totalRideTime\" : " + "\""+totalRideTime+"\"" +
                ", \"speedAvg\" : " + "\""+getSpeedAvg()+"\"" +
                ", \"latitude\" : " + latitudeArray.toString() +
                ", \"'longitude'\" : " + longitudeArray.toString();
    }

    public void setDocumentID(String documentID) {
        this.DocumentID = documentID;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setMaxSpeed(double maxSpeed) {
        MaxSpeed = maxSpeed;
    }

    public int getTotalRideDis() {
        return totalRideDis;
    }

    public int getTotalRideTime() {
        return totalRideTime;
    }

    public String getSpeedAvg() {
        return String.valueOf((totalRideDis*3600)/(totalRideTime*1000));
    }

    public DriveRecode PushRecorde(){

        DriveRecode driveRecode = new DriveRecode(getDate(),StartPoint,DestinationPoint,totalRideDis,totalRideTime,MaxSpeed,latitudeArray.toString(),longitudeArray.toString(),weight);

        return driveRecode;
    }

    private  String getDate(){
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy년 MM월 dd일");

        return simpleDate.format(cal.getTime());
    }

    public void upload() throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(main);
        String url = Server+"/api/rideinfoes/RideInfoAdd";

        JSONObject RoadData = new JSONObject();
        RoadData.put("writer", DocumentID);
        RoadData.put("totalRideDis", totalRideDis);
        RoadData.put("totalRideTime", totalRideTime);
        RoadData.put("speedAvg", getSpeedAvg());
        RoadData.put("latitude", setJsonArray(latitudeArray));
        RoadData.put("longitude", setJsonArray(longitudeArray));


        Log.d("##Road", RoadData.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, RoadData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("###", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        queue.add(jsonObjectRequest);
    }

    public JSONArray setJsonArray(List<Double> list){
        int size = list.size();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < size; i++) {
            jsonArray.put(list.get(i));
        }

        return jsonArray;
    }
}
