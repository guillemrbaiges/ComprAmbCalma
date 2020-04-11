package com.example.comprambcalma;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.Random;

public class StoreOccupationActivity extends AppCompatActivity {
    int mRows = 12;
    int mCols = 8;
    private String name;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        name = getIntent().getStringExtra("name");

        setContentView(R.layout.store_occupation);

        ConstraintLayout layout = findViewById(R.id.layout);

        TextView textView;
        ConstraintLayout.LayoutParams lp;
        int id;
        int idArray[][] = new int[mRows][mCols];
        ConstraintSet cs = new ConstraintSet();

        String[] weekDays = {"dilluns", "dimarts", "dimecres", "dijous", "divendres", "dissabte", "diumenge"};
        String[] hours = {"9:00h", "10:00h", "11:00h", "12:00h", "13:00h", "14:00h", "15:00h", "15:00", "16:00h", "17:00h", "18:00h", "19:00h", "20:00h", "21:00h"};

        for (int iRow = 0; iRow < mRows; iRow++) {
            for (int iCol = 0; iCol < mCols; iCol++) {
                textView = new TextView(this);
                lp = new ConstraintLayout.LayoutParams(125,
                        75);
                id = View.generateViewId();
                idArray[iRow][iCol] = id;
                textView.setId(id);
                textView.setTextSize(9);

                if (iRow == 0 && iCol != 0) {
                    textView.setText(weekDays[iCol - 1]);
                } else if (iCol == 0 && iRow != 0) {
                    textView.setText(hours[iRow - 1]);
                } else if (iCol != 0 && iRow != 0) {

                    Random ran = new Random();
                    int people = ran.nextInt(20);
                    int colorIndex = ran.nextInt(4);

                    switch(colorIndex) {
                        case 0:
                            textView.setText(String.valueOf(people));
                            textView.setBackgroundColor(getResources().getColor(R.color.green));
                            break;
                        case 1:
                            textView.setText(String.valueOf(people + 20));
                            textView.setBackgroundColor(getResources().getColor(R.color.yellow));
                            break;
                        case 2:
                            textView.setText(String.valueOf(people + 40));
                            textView.setBackgroundColor(getResources().getColor(R.color.orange));
                            break;
                        case 3:
                            textView.setText(String.valueOf(people + 60));
                            textView.setBackgroundColor(getResources().getColor(R.color.red));
                            break;
                        default:
                            break;
                    }
                }
                textView.setGravity(Gravity.CENTER);
                layout.addView(textView, lp);
            }
        }

        cs.clone(layout);
        cs.setDimensionRatio(R.id.gridFrame, mCols + ":" + mRows);
        for (int iRow = 0; iRow < mRows; iRow++) {
            for (int iCol = 0; iCol < mCols; iCol++) {
                id = idArray[iRow][iCol];
                cs.setDimensionRatio(id, "1:1");
                if (iRow == 0) {
                    cs.connect(id, ConstraintSet.TOP, R.id.gridFrame, ConstraintSet.TOP);
                } else {
                    cs.connect(id, ConstraintSet.TOP, idArray[iRow - 1][0], ConstraintSet.BOTTOM);
                }
            }

            cs.createHorizontalChain(R.id.gridFrame, ConstraintSet.LEFT,
                    R.id.gridFrame, ConstraintSet.RIGHT,
                    idArray[iRow], null, ConstraintSet.CHAIN_PACKED);
        }

        cs.applyTo(layout);
    }

    public void navigatePlanShopping(View view) {
        Intent intent = new Intent(this, PlanShoppingActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }
}