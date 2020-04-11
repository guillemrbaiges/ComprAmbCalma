package com.example.comprambcalma;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainShoppingActivity extends AppCompatActivity {

    private TextView supermarketView;
    private TextView dateView;
    private String supermarket;
    private String date;
    private TextView occupation;
    private int color;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(R.layout.main_shopping);

        supermarketView = findViewById(R.id.supermarket);
        dateView = findViewById(R.id.date);

        supermarket = getIntent().getStringExtra("name");
        date = getIntent().getStringExtra("date");
        color = getIntent().getIntExtra("color", 0);
        supermarketView.setText(supermarket);
        dateView.setText(date);

        Random ran = new Random();
        int people = ran.nextInt(20);

        occupation = findViewById(R.id.occupation);
        switch(color) {
            case -13536512: // green
                occupation.setText(String.valueOf(people));
                break;
            case -256:      // yellow
                occupation.setText(String.valueOf(people + 20));
                break;
            case -23296:    // orange
                occupation.setText(String.valueOf(people + 40));
                break;
            case -65536:    // red
                occupation.setText(String.valueOf(people + 60));
                break;
            default:
                break;
        }
        occupation.setBackgroundColor(color);
    }

    public void navigate(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
