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

public class ViewBillRecords extends AppCompatActivity {

    DatabaseHelper myDB;
    private BillArrayAdapter billArrayAdapter;
    boolean income;
    int year;
    int month;
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
                String billType = billRecords.getString(5);
                if(year != -1 && month != -1){
                    if ((amount > 0) == income && billYear == year && billMonth == month){
                        if(type.equals("All") || type.equals(billType)){
                            billArrayList.add(new Bill(billRecords.getInt(0), billRecords.getDouble(1),
                                    new GregorianCalendar(billRecords.getInt(2), billRecords.getInt(3), billRecords.getInt(4)),
                                    billRecords.getString(5),
                                    billRecords.getString(6)));
                        }

                    }
                }
                else if((billRecords.getDouble(1) > 0) == income){
                    billArrayList.add(new Bill(billRecords.getInt(0), billRecords.getDouble(1),
                            new GregorianCalendar(billRecords.getInt(2), billRecords.getInt(3), billRecords.getInt(4)),
                            billRecords.getString(5),
                            billRecords.getString(6)));
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