package com.ubd_app.Map.Route;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.ubd_app.UBDLibrary.JSONPA.*;


//JSON에서 필요한 데이터 파싱하고 경로가 만들어지는 부분
public class PasingPathData {

    private JSONArray jsonArray;

    private List<String> PointList;
    private List<JSONObject> PointProperties;
    private List<String[]> LineStringList;
    private List<JSONObject> LineStringProperties;
    private List<Integer> TurnType;
    private List<Integer> PointToPointDistance;

    public PasingPathData() {

    }

    public PasingPathData(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
        geoDataProcessing();
    }

    private void geoDataProcessing() {

        PointList = new ArrayList<>();
        LineStringList = new ArrayList<>();
        PointProperties = new ArrayList<>();
        LineStringProperties = new ArrayList<>();
        TurnType = new ArrayList<>();
        PointToPointDistance = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonParser((JSONObject) jsonArray.get(i), "geometry");
                JSONObject jsonObject2 = jsonParser((JSONObject) jsonArray.get(i), "properties");
                String type = stringParser(jsonObject1, "type");

                if (type.equals("Point")) {
                    PointList.add(stringParser(jsonObject1, "coordinates"));
                    PointProperties.add(jsonObject2);
                    TurnType.add(Integer.valueOf(stringParser(jsonObject2,"turnType")));
                } else if (type.equals("LineString")) {
                    LineStringList.add(CreateLineStingArray(stringParser(jsonObject1, "coordinates")));
                    LineStringProperties.add(jsonObject2);
                    PointToPointDistance.add(Integer.valueOf(stringParser(jsonObject2,"distance")));
                }
            }

//            Log.d("lll 포인트와 포인트의 거리", PointToPointDistance.toString());
//            Log.d("lll 번째 방향정보 배열",TurnType.toString());
//
//
//            for (int i =0;i<PointList.size();i++ ){
//                Log.d("lll "+ i +"번째 포인트 속성",PointProperties.get(i).toString());
//                Log.d("lll "+ i +"번째 라인 속성",LineStringProperties.get(i).toString());
//                Log.d("lll "+ i +"번째 포인트와 포인트의 거리", PointToPointDistance.get(i).toString());
//                Log.d("lll "+ i +"번째 방향정보 배열",TurnType.get(i).toString());
//            }
        } catch (Exception e) {
            Log.d("lll error", e.getMessage());
        }
    }

    public List<String[]> getLineStringList() {
        return LineStringList;
    }

    public List<String> getPointList() {
        return PointList;
    }

    public List<JSONObject> getPointProperties() {
        return PointProperties;
    }

    public List<JSONObject> getLineStringProperties() {
        return LineStringProperties;
    }

    public List<Integer> getTurnType() {
        return TurnType;
    }

    public List<Integer> getPointToPointDistance() {
        return PointToPointDistance;
    }
}
