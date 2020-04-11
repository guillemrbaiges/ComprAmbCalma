package com.example.comprambcalma;

import android.os.Parcel;

public class Supermarket {
    private String name;
    private String distance;

    public Supermarket(String name, String distance) {
        this.name = name;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
