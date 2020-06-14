package com.example.personalaccounting;

import java.util.Comparator;

public class BillComparator implements Comparator<Bill> {

    public int compare(Bill bill1, Bill bill2) {
        return bill1.getDate().compareTo(bill2.getDate());
    }
}