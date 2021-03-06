package com.example.personalaccounting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class BillActivity extends AppCompatActivity {

    private DatabaseHelper myDB;
    double monthlyExpense, monthlyIncome;

    private Calendar targetDate;

    private TextView dateView;
    private Spinner typeSpinner;
    private EditText remarkText;
    private EditText amountText;
    private Switch typeSwitch;
    private Button submitButton;

    private TextView budgetTextView;
    private TextView amountTextView;
    private ProgressBar budgetProgressBar;
    private TextView typeBudgetTextView;
    private TextView typeAmountTextView;
    private ProgressBar typeBudgetProgressBar;

    private ImageButton chartButton;
    private ImageButton homeButton;
    private ImageButton settingButton;

    private ArrayList<String> expenseTypeList;
    private ArrayList<String> incomeTypeList;
    private boolean income = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        dateView = (TextView) findViewById(R.id.date_view);
        typeSpinner = (Spinner) findViewById(R.id.type_spinner);
        remarkText = (EditText) findViewById(R.id.remark_input);
        amountText = (EditText) findViewById(R.id.amount_input);
        submitButton = (Button) findViewById(R.id.submit_button);
        typeSwitch = (Switch) findViewById(R.id.income_switch);

        budgetTextView = (TextView) findViewById(R.id.bill_budgetTextView);
        amountTextView = (TextView) findViewById(R.id.bill_amountView);
        budgetProgressBar = (ProgressBar) findViewById(R.id.bill_budgetProgressBar);
        typeBudgetTextView = (TextView) findViewById(R.id.bill_typeBudgetTextView);
        typeAmountTextView = (TextView) findViewById(R.id.bill_typeAmountView);
        typeBudgetProgressBar = (ProgressBar) findViewById(R.id.bill_typeBudgetProgressBar);

        chartButton = (ImageButton) findViewById(R.id.chart_button);
        homeButton = (ImageButton) findViewById(R.id.home_button);
        settingButton = (ImageButton) findViewById(R.id.setting_button);

        Intent intent = getIntent();

        int year = intent.getIntExtra("Year", 0);
        int month = intent.getIntExtra("Month", 0);
        int dayOfMonth = intent.getIntExtra("DayOfMonth", 0);

        targetDate = new GregorianCalendar(year, month, dayOfMonth);

        monthlyExpense = intent.getDoubleExtra("MonthlyExpense", 0);
        monthlyIncome = intent.getDoubleExtra("MonthlyIncome", 0);

        myDB = new DatabaseHelper(this);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        dateView.setText(sdf.format(targetDate.getTime()));

        submitButton.setOnClickListener(new submitButtonOnClickListener());
        chartButton.setOnClickListener(new chartButtonOnClickListener());
        homeButton.setOnClickListener(new homeButtonOnClickListener());
        settingButton.setOnClickListener(new settingButtonOnClickListener());
    }

    @Override
    protected void onStart() {
        super.onStart();

        expenseTypeList = myDB.getExpenseTypeList();
        incomeTypeList = myDB.getIncomeTypeList();

       updateProgressBar();

        typeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, expenseTypeList.toArray()));
        typeSpinner.setOnItemSelectedListener(new typeSpinnerOnItemSelectedListener());

        typeSwitch.setTextOff(getResources().getString(R.string.expense));
        typeSwitch.setTextOn(getResources().getString(R.string.income));
        typeSwitch.setShowText(true);
        typeSwitch.setOnCheckedChangeListener(new typeSwitchOnCheckedChangeListener());

    }

    private void updateProgressBar(){
        double budget = income?myDB.getUserIncomeBudget():myDB.getUserExpenseBudget();
        double amount = Math.abs(income?myDB.getUserMonthlyIncome():myDB.getUserMonthlyExpense());

        budgetTextView.setText("/" + budget);
        amountTextView.setText(String.valueOf(amount));
        budgetProgressBar.setMax((int) budget);
        budgetProgressBar.setProgress((int) amount);

        if(amount >= budget){
            amountTextView.setTextColor(ContextCompat.getColor(this, income?R.color.income:R.color.expense));
        }
        else {
            amountTextView.setTextColor(ContextCompat.getColor(this, income?R.color.expense:R.color.income));
        }

    }

    private void updateTypeProgressBar(String type){
        double budget = income?myDB.getIncomeTypeBudget(type):myDB.getExpenseTypeBudget(type);
        double amount = Math.abs(income?myDB.getMonthlyIncomeOfType(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), type):
                myDB.getMonthlyExpenseOfType(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), type));

        typeBudgetTextView.setText("/" + budget);
        typeAmountTextView.setText(String.valueOf(amount));
        typeBudgetProgressBar.setMax((int) budget);
        typeBudgetProgressBar.setProgress((int) amount);

        if(amount >= budget){
            typeAmountTextView.setTextColor(ContextCompat.getColor(this, income?R.color.income:R.color.expense));
        }
        else {
            typeAmountTextView.setTextColor(ContextCompat.getColor(this, income?R.color.expense:R.color.income));
        }

    }
    private class settingButtonOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(BillActivity.this, SettingActivity.class);
            startActivity(intent);
        }
    }
    private class homeButtonOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            finish();
        }
    }

    private class chartButtonOnClickListener implements  View.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(BillActivity.this, ChartActivity.class);
            startActivity(intent);
        }
    }

    private class submitButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String entry = amountText.getText().toString();
            if (entry.length() > 0) {
                if(Double.valueOf(entry) == 0){
                    Toast.makeText(BillActivity.this, R.string.AmountEqualToZeroMessage, Toast.LENGTH_SHORT).show();
                }
                else{
                    double amount = Double.valueOf(entry);
                    if(!typeSwitch.isChecked()) amount = 0 - amount;

                    if(addBill(new Bill(amount, targetDate, typeSpinner.getSelectedItem().toString(), remarkText.getText().toString())))
                    {
                        amountText.setText("");
                        remarkText.setText("");
                        updateProgressBar();
                        updateTypeProgressBar(typeSpinner.getSelectedItem().toString());
                        Toast.makeText(BillActivity.this, R.string.AddBillSuccessMessage, Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(BillActivity.this, R.string.AddBillFailMessage, Toast.LENGTH_LONG).show();
                    }
                }

            }
            else Toast.makeText(BillActivity.this, R.string.EmptyBillMessage, Toast.LENGTH_SHORT).show();
        }


    }

    private class typeSwitchOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if(isChecked) {
                income = true;
                typeSpinner.setAdapter(new ArrayAdapter<>(BillActivity.this, android.R.layout.simple_spinner_item, incomeTypeList));

            }
            else{
                income = false;
                typeSpinner.setAdapter(new ArrayAdapter<>(BillActivity.this, android.R.layout.simple_spinner_item, expenseTypeList));
            }
            updateProgressBar();
            updateTypeProgressBar(income?incomeTypeList.get(0):expenseTypeList.get(0));

        }
    }

    private boolean addBill(Bill bill){
        long result = myDB.addBill(bill);

        if (result != -1){
            bill.setId(result);
            return true;
        }
        else return false;
    }

    private class typeSpinnerOnItemSelectedListener implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            updateTypeProgressBar(adapterView.getSelectedItem().toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

}