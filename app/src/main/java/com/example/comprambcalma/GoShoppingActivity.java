package com.example.comprambcalma;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GoShoppingActivity extends AppCompatActivity {

    private String name;
    private String distance;
    private String TAG = "GoShopping";
    private TextView storeName;
    private TextView storeDistance;
    private Button goButton;
    private int color;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.go_shopping);

        storeName = findViewById(R.id.storeName);
        storeDistance = findViewById(R.id.storeDistance);
        goButton = findViewById(R.id.goButton);

        name = getIntent().getStringExtra("name");
        distance = getIntent().getStringExtra("distance");
        color = getIntent().getIntExtra("color", 0);

        storeName.setText(name);
        storeDistance.setText(distance + " km");

        goButton.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    public void navigateMainShopping(View view) {
        Intent intent = new Intent(this, MainShoppingActivity.class);

        intent.putExtra("name", name);

        System.currentTimeMillis();
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String stringDate = formatter.format(date);
        intent.putExtra("date", stringDate);
        intent.putExtra("color", color);

        startActivity(intent);
    }

    public void navigatePlanShopping(View view) {
        Intent intent = new Intent(this, PlanShoppingActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }
}
