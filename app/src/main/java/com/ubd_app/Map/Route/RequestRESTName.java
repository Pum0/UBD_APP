package com.ubd_app.Map.Route;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import com.naver.maps.geometry.LatLng;
import com.ubd_app.KeyValues;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//Rest 통해서 데이터 받아오는 부분
public class RequestRESTName extends AsyncTask<Void, Void, String> implements KeyValues {

    private Location location;

    public RequestRESTName(Location location) {
        this.location = location;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        String name = "에러로 인한 오류";
        JSONObject jsonObject = null;
        HttpsURLConnection urlConnection = null;
        try {

            String https = "https://apis.openapi.sk.com/tmap/geo/reversegeocoding?";
            String version = "version=1&";
            String Location = "lat=" + location.getLatitude() + "&lon=" + location.getLongitude()+"&";
            String AddType = Tmap_key;
            String AppKey = Tmap_secret;

            URL url = new URL(https + version + Location + AddType + AppKey);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("GET");


            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                jsonObject = new JSONObject(br.readLine());
            }

            name = (String) ((JSONObject) jsonObject.get("addressInfo")).get("fullAddress");
//            Log.d("lll", name);

            urlConnection.disconnect();

            return name;

        } catch (Exception e) {
            Log.d("lll", "예외 발생");
            Log.d("lll 에러 메세지", String.valueOf(e.getMessage()));
            urlConnection.disconnect();
            return name;
        }
    }

    @Override
    protected void onPostExecute(String name) {
        super.onPostExecute(name);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
