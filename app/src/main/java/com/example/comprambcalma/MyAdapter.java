package com.example.comprambcalma;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Random;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<Supermarket> mDataset;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView supermarket;
        private TextView distance;
        private View colorRectangle;

        public MyViewHolder(View itemView) {
            super(itemView);

            supermarket = itemView.findViewById(R.id.supermarketName);
            distance = itemView.findViewById(R.id.supermarketDistance);
            colorRectangle = itemView.findViewById(R.id.myRectangleView);
        }

        public void setSupermarket(String name) {
            supermarket.setText(name);
        }

        public void setDistance(String dist) {
            distance.setText(dist);
        }

        public void setColor(int color) {
            colorRectangle.setBackgroundColor(color);
        }
    }

    public MyAdapter(ArrayList<Supermarket> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Supermarket supermarket = mDataset.get(position);

        holder.setSupermarket(supermarket.getName());
        holder.setDistance(supermarket.getDistance() + " km");

        Resources res = holder.itemView.getContext().getResources();
        Random ran = new Random();
        int colorIndex = ran.nextInt(4);

        switch(colorIndex) {
            case 0:
                holder.setColor(res.getColor(R.color.green));
                break;
            case 1:
                holder.setColor(res.getColor(R.color.yellow));
                break;
            case 2:
                holder.setColor(res.getColor(R.color.orange));
                break;
            case 3:
                holder.setColor(res.getColor(R.color.red));
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}