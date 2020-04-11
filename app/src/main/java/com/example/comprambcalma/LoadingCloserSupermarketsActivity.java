package com.example.comprambcalma;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class LoadingCloserSupermarketsActivity extends AppCompatActivity {
    private ProgressBar pgsBar;
    private Double latitude;
    private Double longitude;
    private ArrayList<Supermarket> supermarkets;
    private LoadingCloserSupermarketsActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        instance = this;

        latitude = getIntent().getDoubleExtra("latitude", -1.0);
        longitude = getIntent().getDoubleExtra("longitude", -1.0);

        setContentView(R.layout.loading_closer_groceries);
        pgsBar = (ProgressBar) findViewById(R.id.pBar);
        pgsBar.setVisibility(View.VISIBLE);

        String link = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ latitude + "," + longitude + "&rankBy=distance&radius=2000&type=supermarket&key=AIzaSyBIWX47JNJ8-VS701_mRXwe5Fy_G4ejEhk";

        GetLocationDownloadTask getLocation = new GetLocationDownloadTask();
        JSONObject results = new JSONObject();
        try {
            results = getLocation.execute(link).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONArray places;
        supermarkets = new ArrayList<>();
        try {
            places = results.getJSONArray("results");

            for (int i = 0; i < places.length(); i++) {
                JSONObject place = places.getJSONObject(i);
                String name = place.getString("name");
                JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
                String distance = computeDistance(latitude, longitude, location.getDouble("lat"), location.getDouble("lng"));

                Supermarket supermarket = new Supermarket(name, distance);
                supermarkets.add(supermarket);
            }

            navigate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String computeDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double dist = (Double) (earthRadius * c / 1000);

        DecimalFormat df2 = new DecimalFormat("#.##");
        return df2.format(dist);
    }

    public void navigate() {
        Intent intent = new Intent(this, CloserSupermarketsActivity.class);
        String supermarketsString = new Gson().toJson(supermarkets);
        intent.putExtra("supermarkets", supermarketsString);
        startActivity(intent);
        finish();
    }
}
