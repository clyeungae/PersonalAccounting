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

public class BillActivity extends AppCompatActivity{

    private DatabaseHelper myDB;
    double monthlyExpense, monthlyIncome;

    private Calendar targetDate;

    private TextView dateView;
    private Spinner typeSpinner;
    private EditText remarkText;
    private EditText amountText;
    private Switch typeSwitch;
    private Button submitButton;

    private TextView budgetCurrentAmountTextView;
    private TextView budgetTextView;
    private ProgressBar budgetProgressBar;
    private TextView typeCurrentAmountTextView;
    private TextView typeBudgetTextView;
    private ProgressBar typeBudgetProgressBar;

    private ImageButton chartButton;
    private ImageButton homeButton;
    private ImageButton settingButton;

    ArrayList<String> expenseTypeList;
    ArrayList<String> incomeTypeList;
    boolean income = false;

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

        budgetCurrentAmountTextView = (TextView) findViewById(R.id.bill_currentAmount);
        budgetTextView = (TextView) findViewById(R.id.bill_budgetTotal);
        budgetProgressBar = (ProgressBar) findViewById(R.id.bill_budgetProgressBar);

        typeCurrentAmountTextView = (TextView) findViewById(R.id.bill_typeCurrentAmount);
        typeBudgetTextView = (TextView) findViewById(R.id.bill_typeBudget);
        typeBudgetProgressBar = (ProgressBar) findViewById(R.id.bill_type_progressBar);

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

        typeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, expenseTypeList.toArray()));
        typeSpinner.setOnItemSelectedListener(new typeSpinnerOnItemSelectedListener());

        typeSwitch.setTextOff(getResources().getString(R.string.expense));
        typeSwitch.setTextOn(getResources().getString(R.string.income));
        typeSwitch.setShowText(true);
        typeSwitch.setOnCheckedChangeListener(new typeSwitchOnCheckedChangeListener());

        updateProgressBar();
        updateTypeProgressBar(expenseTypeList.get(0));
    }

    private void updateProgressBar(){
        double budget = myDB.getUserExpenseBudget();
        double currentAmount = Math.abs(income?myDB.getUserMonthlyIncome():myDB.getUserMonthlyExpense());

        budgetTextView.setText("/" + String.valueOf(budget));
        budgetCurrentAmountTextView.setText(String.valueOf(currentAmount));
        budgetProgressBar.setMax((int) budget);
        budgetProgressBar.setProgress((int) currentAmount);

        if(currentAmount < budget){
            budgetCurrentAmountTextView.setTextColor(ContextCompat.getColor(this, (income?R.color.expense:R.color.income)));

        }
        else{
            budgetCurrentAmountTextView.setTextColor(ContextCompat.getColor(this, (income?R.color.income:R.color.expense)));
        }

    }

    private void updateTypeProgressBar(String type){
        double typeBudget = myDB.getUserBudgetOfExpenseType(type);
        double typeCurrentAmount = Math.abs(myDB.getMonthlyExpenseOfType(type, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH)));

        typeBudgetTextView.setText("/" + String.valueOf(typeBudget));
        typeCurrentAmountTextView.setText(String.valueOf(typeCurrentAmount));
        typeBudgetProgressBar.setMax((int) typeBudget);
        typeBudgetProgressBar.setProgress((int) typeCurrentAmount);
        if(typeCurrentAmount < typeBudget){
            typeCurrentAmountTextView.setTextColor(ContextCompat.getColor(this, (income?R.color.expense:R.color.income)));
        }
        else{
            typeCurrentAmountTextView.setTextColor(ContextCompat.getColor(this, (income?R.color.income:R.color.expense)));
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
                //updateTypeProgressBar(true, incomeTypeList.get(0));
            }
            else{
                income = false;
                typeSpinner.setAdapter(new ArrayAdapter<>(BillActivity.this, android.R.layout.simple_spinner_item, expenseTypeList));
                //updateTypeProgressBar(true, expenseTypeList.get(0));
            }
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
            updateTypeProgressBar(adapterView.getItemAtPosition(position).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
           updateTypeProgressBar((income?incomeTypeList.get(0):expenseTypeList.get(0)));

        }
    }

}
