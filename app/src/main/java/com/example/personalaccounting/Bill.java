package com.example.personalaccounting;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Bill {
    private long id;
    private String type;
    private double amount;
    private Calendar date;
    private String remark;

    public Bill(long id, double amount, Calendar date, String type, String remark) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.remark = remark;
    }

    public Bill(double amount, Calendar date, String type, String remark){
        this.amount =amount;
        this.type = type;
        this.remark = remark;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Calendar getDate() { return date; }

    public String getDateText(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(date.getTime());
    }

    public int getDayOfMonth() {
        return date.get(Calendar.DAY_OF_MONTH);
    }

    public int getMonth(){
        return date.get(Calendar.MONTH);
    }

    public int getYear(){
        return date.get(Calendar.YEAR);
    }

    public double getAmount() {
        return amount;
    }

    public String getRemark() {
        return remark;
    }

    public String getType() {
        return type;
    }

    public void setDate(int year, int month, int dayofMonth) {
        this.date = new GregorianCalendar(year, month, dayofMonth);
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}