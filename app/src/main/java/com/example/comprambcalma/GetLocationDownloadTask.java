package com.example.comprambcalma;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetLocationDownloadTask extends AsyncTask<String, Void, JSONObject> {

    @Override
    protected JSONObject doInBackground(String... strings) {
        Log.d("GetLocationDownloadTask", "geoLocate: Doing in background");


        String result = "";
        URL url;
        HttpURLConnection urlConnection;
        try {
            url = new URL(strings[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream is = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(is);

            int data = inputStreamReader.read();
            while (data != -1) {
                char curr = (char) data;
                result += curr;
                data = inputStreamReader.read();
            }
            return new JSONObject(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
