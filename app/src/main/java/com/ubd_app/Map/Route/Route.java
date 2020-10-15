package com.ubd_app.Map.Route;

import android.location.Location;
import android.util.Log;
import com.naver.maps.geometry.LatLng;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.ubd_app.UBDLibrary.JSONPA.*;
import static com.ubd_app.UBDLibrary.LocationCheck.*;

public class Route {

    //정제된 경로 관련된 데이터
    private List<String> PointList;
    private List<JSONObject> PointProperties;
    private List<String[]> LineStringList;
    private List<JSONObject> LineStringProperties;

    private List<Integer> TurnType;
    private List<Integer> PointToPointDistance;

    //패스를 만들 데이터
    private List<LatLng> LatLngList;

    private Location StartPoint;
    private Location GoalLocation;
    private int GoalPointNumber;

    private String SPointName = "";
    private String GPointName = "";

    private int TotalDistance = 0;

    public Route() {
        this.PointList = new ArrayList<>();
        this.PointProperties = new ArrayList<>();
        this.LineStringList = new ArrayList<>();
        this.LineStringProperties = new ArrayList<>();
        this.LatLngList = new ArrayList<>();
    }

    private void setPointList(List<String> pointList) {
        this.PointList = pointList;
        this.GoalPointNumber = pointList.size() - 1;
        this.GoalLocation = CreateLocation(pointList.get(GoalPointNumber));
        this.StartPoint = CreateLocation(pointList.get(0));
    }

    private void setPointProperties(List<JSONObject> pointProperties) {
        this.PointProperties = pointProperties;
        Log.d("lll",String.valueOf(pointProperties.get(0).toString()));
        this.TotalDistance = setTotalDistance(pointProperties.get(0));
    }

    private void setLineStringList(List<String[]> lineStringList) {
        this.LineStringList = lineStringList;
        this.LatLngList = setLatLngList(lineStringList);
    }

    private void setLineStringProperties(List<JSONObject> lineStringProperties) {
        this.LineStringProperties = lineStringProperties;
    }

    private void setTurnType(List<Integer> turnType) {
        TurnType = turnType;
    }

    private void setPointToPointDistance(List<Integer> pointToPointDistance) {
        PointToPointDistance = pointToPointDistance;
    }

    //경로를 받아올때
    //위경도 순서를 바꾼 출발지와 도착지를 받음
    public void requestRoute(Location location, LatLng latLng) {

        String[] SGPoint = {ConverterLocationToString(location), ConverterLatLngToString(latLng)};
        JSONArray jsonArray = null;

        try {
            jsonArray = new RequestRESTRoute(SGPoint).execute().get();
            PasingPathData pasingPathData = new PasingPathData(jsonArray);

            //경로설정
            setPointList(pasingPathData.getPointList());
            setPointProperties(pasingPathData.getPointProperties());

            setLineStringList(pasingPathData.getLineStringList());
            setLineStringProperties(pasingPathData.getLineStringProperties());

            setPointToPointDistance(pasingPathData.getPointToPointDistance());
            setTurnType(pasingPathData.getTurnType());

            SPointName = new RequestRESTName(StartPoint).execute().get();
            GPointName = new RequestRESTName(GoalLocation).execute().get();

        } catch (Exception e) {
            Log.d("lll error", e.getMessage());
        }
    }

    public List<String> getPointList() {
        return PointList;
    }

    public List<JSONObject> getPointProperties() {
        return PointProperties;
    }

    public List<String[]> getLineStringList() {
        return LineStringList;
    }

    public List<JSONObject> getLineStringProperties() {
        return LineStringProperties;
    }

    public List<LatLng> getLatLngList() {
        return LatLngList;
    }

    public int getTotalDistance() {
        return TotalDistance;
    }

    public Location getGoalLocation() {
        return GoalLocation;
    }

    public LatLng getGoalLatLng() {
        return new LatLng(GoalLocation);
    }

    public int getGoalPointNumber() {
        return GoalPointNumber;
    }

    public String getSPointName() {
        return SPointName;
    }

    public String getGPointName() {
        return GPointName;
    }

    public List<Integer> getTurnType() {
        return TurnType;
    }

    public List<Integer> getPointToPointDistance() {
        return PointToPointDistance;
    }
}
