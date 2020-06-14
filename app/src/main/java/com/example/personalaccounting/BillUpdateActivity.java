package com.example.personalaccounting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class BillUpdateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private DatabaseHelper myDB;

    private Calendar newDate = new GregorianCalendar(0,0,0);

    private TextView idTextView;
    private CalendarView dateView;
    private Spinner typeSpinner;
    private EditText remarkInput;
    private EditText amountInput;
    private Switch typeSwitch;
    private Button cancelButton;
    private Button updateButton;
    private CheckBox deleteCheckBox;


    ArrayList<String> expenseTypeList;
    ArrayList<String> incomeTypeList;
    long id;
    Bill bill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_update);

        Intent intent = getIntent();
        id = intent.getLongExtra("id", -1);

        if(id < 0){
            Toast.makeText(this, R.string.BillRecordNotFoundMessage, Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            myDB = new DatabaseHelper(this);
            bill = myDB.getBillById(id);
            incomeTypeList = myDB.getIncomeTypeList();
            expenseTypeList = myDB.getExpenseTypeList();

            idTextView = (TextView) findViewById(R.id.billUpdate_id);
            amountInput = (EditText) findViewById(R.id.billUpdate_amountInput);
            typeSwitch = (Switch) findViewById(R.id.billUpdate_incomeSwitch);
            dateView = (CalendarView) findViewById(R.id.billUpdate_dateInput);
            typeSpinner = (Spinner) findViewById(R.id.billUpdate_typeSpinner);
            remarkInput = (EditText) findViewById(R.id.billUpdate_remarkInput);
            deleteCheckBox = (CheckBox) findViewById(R.id.billUpdate_deleteCheckBox);
            cancelButton = (Button) findViewById(R.id.billUpdate_cancel_button);
            updateButton = (Button) findViewById(R.id.billUpdate_update_button);

            idTextView.setText(Long.toString(id));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        expenseTypeList = myDB.getExpenseTypeList();
        incomeTypeList = myDB.getIncomeTypeList();

        if(bill.getAmount() < 0){
            amountInput.setHint(Double.toString(0 - bill.getAmount()));
            amountInput.setText(Double.toString(0 - bill.getAmount()));
            typeSwitch.setChecked(false);
            typeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, expenseTypeList.toArray()));
            for(int i = 0; i < expenseTypeList.size(); i++){
                if(bill.getType().equals(expenseTypeList.get(i))){
                    typeSpinner.setSelection(i,true);
                    break;
                }
            }
        }
        else{
            amountInput.setHint(Double.toString(bill.getAmount()));
            amountInput.setText(Double.toString(bill.getAmount()));
            typeSwitch.setChecked(true);
            typeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, incomeTypeList.toArray()));
            for(int i = 0; i < incomeTypeList.size(); i++){
                if(bill.getType().equals(incomeTypeList.get(i))){
                    typeSpinner.setSelection(i,true);
                    break;
                }
            }
        }

        dateView.setDate(bill.getDate().getTimeInMillis());
        remarkInput.setText(bill.getRemark());

        typeSpinner.setOnItemSelectedListener(this);
        typeSwitch.setOnCheckedChangeListener(new typeSwitchOnCheckedChangeListener());
        dateView.setOnDateChangeListener(new dateViewOnDateChangeListener());
        updateButton.setOnClickListener(new updateButtonOnClickListener());
        cancelButton.setOnClickListener(new cancelButtonOnClickListener());

    }

    private class updateButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(deleteCheckBox.isChecked()){
                if( myDB.deleteBill(id)){
                    Toast.makeText(BillUpdateActivity.this, R.string.DeleteBillSuccessMessage, Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(BillUpdateActivity.this, R.string.DeleteBillFailMessage, Toast.LENGTH_SHORT).show();
                }
            }
            else{
                String entry = amountInput.getText().toString();
                if(entry.length() > 0){
                    double amount = Double.valueOf(entry);
                    if(!typeSwitch.isChecked()) amount = 0 - amount;
                    bill.setAmount(amount);
                }

                bill.setType(typeSpinner.getSelectedItem().toString());
                bill.setRemark(remarkInput.getText().toString());
                if (myDB.updateBill(bill)){
                    Toast.makeText(BillUpdateActivity.this, R.string.BillUpdateSuccessMessage, Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(BillUpdateActivity.this, R.string.BillUpdateFailMessage, Toast.LENGTH_SHORT).show();
                }

            }
        }


    }

    private class dateViewOnDateChangeListener implements CalendarView.OnDateChangeListener{

        @Override
        public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
            bill.setDate(year, month, dayOfMonth);
        }
    }
    private class typeSwitchOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if(isChecked) {
                typeSpinner.setAdapter(new ArrayAdapter<>(BillUpdateActivity.this, android.R.layout.simple_spinner_item, incomeTypeList));
            }
            else{
                typeSpinner.setAdapter(new ArrayAdapter<>(BillUpdateActivity.this, android.R.layout.simple_spinner_item, expenseTypeList));
            }
        }
    }

    private class cancelButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            finish();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}