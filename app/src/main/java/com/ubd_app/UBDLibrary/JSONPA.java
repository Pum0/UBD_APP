package com.ubd_app.UBDLibrary;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONPA {

    public static JSONObject jsonParser(JSONObject jsonObject, String Separator) {

        JSONObject Object = null;

        try {
            Object = new JSONObject(stringParser(jsonObject, Separator));
        } catch (Exception e) {
            Log.d("lll1", e.getMessage());
        }
        return Object;
    }

    public static String stringParser(JSONObject jsonObject, String Separator) {
        String string = "";

        try {
            string = jsonObject.get(Separator).toString();
        } catch (JSONException e) {
            Log.d("lll2", e.getMessage());
        }
        return string;
    }

    public static String[] CreateLineStingArray(String s) {
        String[] Array = s.split("],");

        for (int i = 0; i < Array.length; i++) {
            Array[i] = Array[i].replace("]", "");
            Array[i] = Array[i].replace("[", "");
        }
        return Array;

    }

    public static int setTotalDistance(JSONObject propertiesList) {
        int TotalDistance = 0;

        try {
            TotalDistance = Integer.valueOf(String.valueOf(propertiesList.get("totalDistance")));

            return TotalDistance;
        } catch (Exception e) {
            Log.d("lll", String.valueOf(e.getMessage()));
            return TotalDistance;
        }
    }
}
