package com.example.personalaccounting;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ViewBillRecords extends AppCompatActivity {

    DatabaseHelper myDB;
    private BillArrayAdapter billArrayAdapter;
    boolean income;
    int year, month, date;
    String type = "All";
    ListView listView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewbillrecords_layout);

        Intent intent = getIntent();
        income = intent.getBooleanExtra("income", false);
        year = intent.getIntExtra("year", -1);
        month = intent.getIntExtra("month", -1);
        date = intent.getIntExtra("date", -1);
        type = intent.getStringExtra("type");
        listView = (ListView) findViewById(R.id.billrecords_view);
        myDB = new DatabaseHelper(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Cursor billRecords = myDB.getBillTable();
        ArrayList<Bill> billArrayList = new ArrayList<>();
        if (billRecords.getCount() == 0) Toast.makeText(ViewBillRecords.this, R.string.NoBillRecordMessage, Toast.LENGTH_LONG).show();
        else{
            while (billRecords.moveToNext()){
                double amount = billRecords.getDouble(1);
                int billYear = billRecords.getInt(2);
                int billMonth = billRecords.getInt(3);
                int billDate = billRecords.getInt(4);
                String billType = billRecords.getString(5);

                if(Locale.getDefault().getDisplayLanguage().equals("English")){
                    switch(billType){
                        case"衣物":
                            billType = getResources().getString(R.string.clothing);
                            break;
                        case"飲食":
                            billType = getResources().getString(R.string.food);
                            break;
                        case"住屋":
                            billType = getResources().getString(R.string.housing);
                            break;
                        case"交通":
                            billType = getResources().getString(R.string.transport);
                            break;
                        case"其他":
                            billType = getResources().getString(R.string.other);
                            break;
                        case"工資":
                            billType = getResources().getString(R.string.salary);
                            break;

                    }
                }
                else if(Locale.getDefault().getDisplayLanguage().equals("中文")){
                    switch(billType){
                        case"Clothing":
                            billType = getResources().getString(R.string.clothing);
                            break;
                        case"Food":
                            billType = getResources().getString(R.string.food);
                            break;
                        case"Housing":
                            billType = getResources().getString(R.string.housing);
                            break;
                        case"Transport":
                            billType = getResources().getString(R.string.transport);
                            break;
                        case"Other":
                            billType = getResources().getString(R.string.other);
                            break;
                        case"Salary":
                            billType = getResources().getString(R.string.salary);
                            break;

                    }
                }

                Bill bill = new Bill(billRecords.getInt(0), amount,
                        new GregorianCalendar(billYear, billMonth, billRecords.getInt(4)),
                        billType,
                        billRecords.getString(6));
                myDB.updateBill(bill);
                //the bill fit the income/expense and required type/ all type
                if((amount > 0) == income &&(type.equals(getResources().getString(R.string.all)) || type.equals(billType))){
                    if(year == -1){
                        billArrayList.add(bill);
                    }
                    else if (year == billYear){
                        //required year fit
                        if(month == -1){
                            billArrayList.add(bill);
                        }
                        else if (month == billMonth){
                            //required month fit
                            if(date == -1 || date == billDate){
                                billArrayList.add(bill);
                            }
                        }
                    }
                }

                Collections.sort(billArrayList, new BillComparator());
                Collections.reverse(billArrayList);
                billArrayAdapter = new BillArrayAdapter(this, billArrayList);
                listView.setAdapter(billArrayAdapter);
                listView.setOnItemClickListener(new billRecordOnItemClickListener());
            }
        }

        if(billArrayList.size() == 0)
            Toast.makeText(this, R.string.NoBillRecordMessage, Toast.LENGTH_SHORT).show();
    }


    private class billRecordOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Object object = listView.getItemAtPosition(position);
            Bill bill = (Bill) object;
            openBillUpdateActivity(bill.getId());

        }
    }

    private void openBillUpdateActivity(long id){
        Intent intent = new Intent(this, BillUpdateActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}