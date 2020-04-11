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

    /*
    *   TODO List
    *       - MainShopping                                          [Designed]
    *           - Receive supermarket name + hour                   [Done]
    *           - Set supermarket name + hour                       [Done]
    *           - Barcode                                           [Done]
    *           - Set button color -> Create fake data              [Done]
    *           - Set occupation number                             [Done]
    *       - PlanShopping                                          [Designed]
    *           - DatePicker integration                            [Done]
    *           - Hour Picker                                       [Done]
    *           - Receive supermarket name                          [Done]
    *           - Set supermarket name                              [Done]
    *           - Navigation StoreOccupation                        [Done]
    *           - Navigation MainShopping                           [Done]
    *       - GoShopping                                            [Done]
    *           - Receive supermarket name + dist                   [Done]
    *           - Set supermarket name + dist                       [Done]
    *           - Set button color -> Create fake data              [Done]
    *           - Navigation MainShopping                           [Done]
    *           - Navigation PlanShopping                           [Done]
    *       - CloserGroceries                                       [Designed]
    *           - RecycleView integration                           [Done]
    *           - Adapter style                                     [Done]
    *           - Get profile location                              [Done]
    *           - Find closer supermarkets                          [Done]
    *           - Compute distance                                  [Done]
    *           - Fill list elements                                [Done]
    *           - Set elements color -> Create fake data            [Done]
    *           - Pretty Adapter (curr color, distance)             [Done]
    *           - Navigation GoShopping                             [Done]
    *       - CreateProfile /EditProfile                            [Designed]
    *           - Add google maps integration                       [Done]
    *           - Introduce & Search address                        [Done]
    *           - Show in map                                       [Done]
    *           - Store profile in Firebase                         [Done]
    *           - Store uid in device                               [Done]
    *           - Show stored data                                  [Done]
    *           - Edit profile                                      [Done]
    *           - Navigation to MainActivity                        [Done]
    *           - (Good To Have) AutoComplete
    *           - (Goot To Have) Disable newline on enter
    *       - StoreOccupation                                       [Designed]
    *           - Week Calendar integration                         [Done]
    *           - Retrieve occupation from DB -> Create fake data   [Done]
    *           - Week numbers instead of day
    *       - MainScreen                                            [Designed]
    *           - Get uid , if not exist go to create profile       [Done]              <- confirm working properly
    *           - Get name from db                                  [Done]
    *           - Set Hello "name" !                                [Done]
    *           - navigation CreateProfile + pass uid, loc          [Done]
    *       Others
    *           - createprofile as edit, if uid in device           [Done]
    *           - General back button                               [Done]
    *           - Force no rotation                                 [Done]
    *           - Dynamic main activity (profile? MS : CP)          [Done]
    *           - Icon                                              [Done]
    *           - Long logo within main activity                    [Done]
    *           - Status bar color                                  [Done]
    *           - "Tornar" buttons                                  [Done]
    *           - Device responsiveness
    *       MUST
    *           - Revise all possible null values + add Toasts
    *           - Delete all unnecessary comments and code
    */

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
