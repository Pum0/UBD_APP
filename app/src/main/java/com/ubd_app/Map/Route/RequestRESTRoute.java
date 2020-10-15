package com.ubd_app.Map.Route;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//Rest 통해서 데이터 받아오는 부분
public class RequestRESTRoute extends AsyncTask<String[], Void, JSONArray> {

    private String start;
    private String goal;

    public RequestRESTRoute(String[] strings) {
        this.start = strings[0];
        this.goal = strings[1];

    }

    private JSONObject CreateRequestJSON(String start, String goal) {

        String[] geoStart = start.split(",");
        String[] geoGoal = goal.split(",");

        JSONObject jsonObjectRequest = new JSONObject();
        try {
            jsonObjectRequest.put("startName", "start");
            jsonObjectRequest.put("startX", geoStart[0]);
            jsonObjectRequest.put("startY", geoStart[1]);
            jsonObjectRequest.put("endName", "goal");
            jsonObjectRequest.put("endX", geoGoal[0]);
            jsonObjectRequest.put("endY", geoGoal[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObjectRequest;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONArray doInBackground(String[]... strings) {

        JSONArray jsonArray = null;
        try {
            JSONObject jsonObjectRequest = CreateRequestJSON(start, goal);

            URL url = new URL("https://api2.sktelecom.com/tmap/routes/pedestrian");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);


            urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            urlConnection.setRequestProperty("appKey", "l7xx1a990bef13cb4c30ac5f29719a40bdf5");
            urlConnection.setRequestMethod("POST");

            OutputStream output = urlConnection.getOutputStream();
            output.write(jsonObjectRequest.toString().getBytes("UTF-8"));
            output.flush();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));

                JSONObject jsonObject = new JSONObject(br.readLine());

                jsonArray = jsonObject.getJSONArray("features");

            }
            output.close();
            urlConnection.disconnect();

        } catch (Exception e) {
            e.getStackTrace();
        }

        return jsonArray;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
