package com.example.comprambcalma;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateProfileActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            if (latitude != -1.0 && longitude != -1) {
                userLocation = new LatLng(latitude, longitude);
                moveCamera(userLocation, DEFAULT_ZOOM);
            }

            init();
        }
    }

    private static final String TAG = "CreateProfile";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //widgets
    private EditText mSearchText;
    private EditText textPersonName;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private String uid;
    private String name;
    private Double latitude;
    private Double longitude;
    private LatLng userLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        uid = getIntent().getStringExtra("uid");
        if (uid != null) {
            uid = uid.replaceAll("[\\n\\t ]", "");
        }
        name = getIntent().getStringExtra("name");
        latitude = getIntent().getDoubleExtra("latitude", -1.0);
        longitude = getIntent().getDoubleExtra("longitude", -1.0);

        setContentView(R.layout.create_profile);
        mSearchText = (EditText) findViewById(R.id.input_search);
        textPersonName = (EditText) findViewById(R.id.name);

        textPersonName.setText(name);

        getLocationPermission();
    }

    private void init(){
        Log.d(TAG, "init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    try {
                        geoLocate();
                    } catch (IOException | ExecutionException | InterruptedException | JSONException e) {
                        e.printStackTrace();
                    }
                }

                return false;
            }
        });
    }

    private void geoLocate() throws IOException, ExecutionException, InterruptedException, JSONException {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();
        Log.d(TAG, "geoLocate:" + searchString);
        Geocoder geocoder = new Geocoder(CreateProfileActivity.this);

        String link = "https://maps.googleapis.com/maps/api/geocode/json?address=" + searchString + "&key=AIzaSyBIWX47JNJ8-VS701_mRXwe5Fy_G4ejEhk";

        GetLocationDownloadTask getLocation = new GetLocationDownloadTask();
        JSONObject results = getLocation.execute(link).get();
        JSONObject coordinates = results.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");

        Log.d(TAG, "geoLocate " + coordinates);

        LatLng introducedLocation = new LatLng(coordinates.getDouble("lat"), coordinates.getDouble("lng"));
        userLocation = introducedLocation;
        moveCamera(introducedLocation, DEFAULT_ZOOM);

    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        if (latitude == -1 && longitude == -1) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            try{
                if(mLocationPermissionsGranted){

                    final Task location = mFusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                Log.d(TAG, "onComplete: found location!");
                                Location currentLocation = (Location) task.getResult();

                                Log.d(TAG, "Found location: " + currentLocation.toString());
                                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                userLocation = latLng;

                                moveCamera(latLng, DEFAULT_ZOOM);
                            }else{
                                Log.d(TAG, "onComplete: current location is null");
                                Toast.makeText(CreateProfileActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }catch (SecurityException e){
                Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
            }
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        MarkerOptions options = new MarkerOptions()
                .position(latLng);
        mMap.addMarker(options);
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(CreateProfileActivity.this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    public void createProfileAndNavigate(View view) {
        if (uid == null) {
            uid = UUID.randomUUID().toString();
        }
        MainActivity.writeToFile(uid, this);

        Log.d(TAG, "UID: " + uid);
        String name = textPersonName.getText().toString();

        if (name.length() == 0 || userLocation == null) {
            Toast.makeText(this, "No has introduït totes les dades!", Toast.LENGTH_LONG).show();
            return;
        }

        HashMap<String, Object> profile = new HashMap<>();
        profile.put("name", name);
        profile.put("location", userLocation);

        storeProfile(uid, profile);

        navigate();
    }

    public void storeProfile(String uid, HashMap profile) {
        FirebaseFirestore db = MainActivity.getDbClient();

        db.collection("profiles").document(uid)
            .set(profile)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error writing document", e);
                }
            });
    }

    public void navigate() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}


