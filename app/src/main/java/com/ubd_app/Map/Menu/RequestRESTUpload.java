package com.ubd_app.Map.Menu;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RequestRESTUpload extends AsyncTask<String[], Void, Integer> {

    private String UploadData ="";

    public RequestRESTUpload(String uploadData) {
        this.UploadData = uploadData;
    }

    public String getUploadData() {
        return UploadData;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String[]... strings) {
        Log.d("###", UploadData);
        try {
            URL url = new URL("http://121.150.35.204:5000/api/users/login");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            urlConnection.setRequestMethod("POST");

            OutputStream output = urlConnection.getOutputStream();
            output.write(getUploadData().getBytes("UTF-8"));
            output.flush();
            output.close();

            urlConnection.disconnect();

        } catch (Exception e) {
            e.getStackTrace();

        }

        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
