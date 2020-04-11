package com.example.comprambcalma;

import java.util.Comparator;

public class SupermarketSorter implements Comparator<Supermarket>{

    @Override
    public int compare(Supermarket s1, Supermarket s2) {
        return s1.getDistance().compareToIgnoreCase(s2.getDistance());
    }

}
