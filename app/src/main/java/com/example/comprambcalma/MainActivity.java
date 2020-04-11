package com.example.comprambcalma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private static FirebaseFirestore db;
    private TextView greetingView;
    private String uid;
    private String name;
    private Double latitude;
    private Double longitude;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        View view = getWindow().getDecorView().getRootView();

        uid = readFromFile(this);
        if (uid == "") {
            navigateCreateProfile(view);
        } else {
            uid = uid.replaceAll("[\\n\\t ]", "");
            Log.d(TAG, "UID:" + uid);

            db = FirebaseFirestore.getInstance();

            DocumentReference docRef = db.collection("profiles").document(uid);
            docRef.get().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData().get("location"));
                        name = document.getData().get("name").toString();

                        try {
                            JSONObject jsonLocation = new JSONObject(document.getData().get("location").toString());
                            latitude = jsonLocation.getDouble("latitude");
                            longitude = jsonLocation.getDouble("longitude");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

                Log.d(TAG, "Coordinates: " + latitude + " " + longitude);

                setContentView(R.layout.activity_main);

                greetingView = findViewById(R.id.userGreeting);
                if (name != "") {
                    greetingView.setText("Hola " + name + "!");
                }
            });
        }
    }

    public static FirebaseFirestore getDbClient() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        return db;
    }

    public static String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();

                Log.d("MainActivity", "UID: " + ret);

            }
        }
        catch (FileNotFoundException e) {
            Log.e("MainActivity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("MainActivity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public void navigateCreateProfile(View view) {
        Intent intent = new Intent(this, CreateProfileActivity.class);
        if (uid != "") {
            intent.putExtra("uid", uid);
        }
        intent.putExtra("name", name);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivity(intent);
    }

    public void navigateLoadingCloserSupermarkets(View view) {
        Intent intent = new Intent(this, LoadingCloserSupermarketsActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivity(intent);
    }
}
