package com.example.comprambcalma;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class CloserSupermarketsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Supermarket> myDataset;
    private static final String TAG = "CloserGroceries";
    private String uid;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        uid = MainActivity.readFromFile(this);
        uid = uid.replaceAll("[\\n\\t ]", "");

        Log.d(TAG, "UID:" + uid);

        String supermarketsString = getIntent().getExtras().getString("supermarkets");
        myDataset = new ArrayList<>(Arrays.asList(new Gson().fromJson(supermarketsString, Supermarket[].class)));

        Log.d(TAG, "Array: " + myDataset);

        myDataset.sort(new SupermarketSorter());


        setContentView(R.layout.closer_supermarkets);
        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyAdapter(myDataset);
        recyclerView.setAdapter(mAdapter);

        final GestureDetector mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }

            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                try {
                    View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                    if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {

                        int position = recyclerView.getChildAdapterPosition(child);
                        Supermarket supermarket = myDataset.get(position);

                        int color = ((ColorDrawable)(recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.myRectangleView)).getBackground()).getColor();

                        navigate(child, supermarket.getName(), supermarket.getDistance(), color);

                        return true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });
    }

    public void navigate(View view, String name, String distance, int color) {
        Intent intent = new Intent(this, GoShoppingActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("distance", distance);
        intent.putExtra("color", color);
        startActivity(intent);
    }
}
