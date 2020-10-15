package com.ubd_app.AppDatabase;

import androidx.room.TypeConverter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DriveRecordeConverter {
    
    @TypeConverter
    public static String fromGpsString(JSONObject jsonObject){
        return jsonObject == null ? null : new String(String.valueOf(jsonObject));
    }

    @TypeConverter
    public static JSONObject fromGpsString(String string) throws JSONException {
        return string == null ? null : new JSONObject(string);
    }

}
