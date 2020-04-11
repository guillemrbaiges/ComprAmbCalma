package com.example.comprambcalma;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;


public class PlanShoppingActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etPlannedDate;
    private EditText etPlannedTime;
    private TextView storeName;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(R.layout.plan_shopping);

        name = getIntent().getStringExtra("name");

        storeName = findViewById(R.id.storeName);
        storeName.setText(name);

        etPlannedDate = findViewById(R.id.etPlannedDate);
        etPlannedDate.setOnClickListener(this);

        etPlannedTime = findViewById(R.id.etPlannedTime);
        etPlannedTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    etPlannedTime.setText( selectedHour + ":" + selectedMinute);
                }
            }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.etPlannedDate:
                showDatePickerDialog();
                break;
        }
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
            final String selectedDate = day + " / " + (month+1) + " / " + year;
            etPlannedDate.setText(selectedDate);
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void navigateMainShopping(View view) {
        Intent intent = new Intent(this, MainShoppingActivity.class);
        intent.putExtra("name", name);

        String date = etPlannedDate.getText() + " " + etPlannedTime.getText();
        intent.putExtra("date", date);

        intent.putExtra("color", -13536512);
        startActivity(intent);
    }

    public void navigateOccupation(View view) {
        Intent intent = new Intent(this, StoreOccupationActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }
}

