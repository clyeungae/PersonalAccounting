package com.example.personalaccounting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Intent originalIntent;
    DatabaseHelper myDB;
    private User user;
    private CalendarView calendarView;
    private TextView budgetView;
    private TextView expenseView;
    private TextView incomeView;
    private Button budgetButton;
    private Button expenseButton;
    private Button incomeButton;

    private ImageButton chartButton;
    private ImageButton homeButton;
    private ImageButton settingButton;

    @Override
    protected void onStart() {
        super.onStart();
        user = myDB.getUserInfo();

        if (!myDB.getUserLanguage().equals(Locale.getDefault().getDisplayLanguage())){
            myDB.setUserLanguage(Locale.getDefault().getDisplayLanguage());
        }

        myDB.setUserLastActiveDate(Calendar.getInstance());
        updateButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDB.setUserLastActiveDate(Calendar.getInstance());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        originalIntent = getIntent();
        myDB = new DatabaseHelper(this);

        calendarView = findViewById(R.id.calendar_view);
        budgetView = findViewById(R.id.main_budgetRemain_view);
        expenseView = findViewById(R.id.main_monthlyExpense_view);
        incomeView = findViewById(R.id.main_monthlyIncome_view);
        budgetButton = findViewById(R.id.budget_button);
        expenseButton = findViewById(R.id.monthlyExpense_button);
        incomeButton = findViewById(R.id.income_button);
        chartButton = findViewById(R.id.chart_button);
        homeButton = findViewById(R.id.home_button);
        settingButton = findViewById(R.id.setting_button);

        calendarView.setOnDateChangeListener(new calenderOnDateChangeListener());
        budgetView.setOnClickListener(new budgetOnClickListener());
        budgetButton.setOnClickListener(new budgetOnClickListener());
        expenseView.setOnClickListener(new expenseRecordOnClickListener());
        expenseButton.setOnClickListener(new expenseRecordOnClickListener());
        incomeView.setOnClickListener(new incomeRecordOnClickListener());
        incomeButton.setOnClickListener(new incomeRecordOnClickListener());
        chartButton.setOnClickListener(new chartButtonOnClickListener());
        homeButton.setOnClickListener(new homeButtonOnClickListener());
        settingButton.setOnClickListener(new settingButtonOnClickListener());
    }

    private class settingButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (originalIntent.getBooleanExtra("setting", false)) {
                finish();
            }
            else{
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                intent.putExtra("home", true);
                startActivity(intent);
            }

        }

    }
    private class homeButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            onRestart();
        }

    }

    private class chartButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(originalIntent.getBooleanExtra("chart", false)){
                finish();
            }
            else{
                Intent intent = new Intent(MainActivity.this, ChartActivity.class);
                intent.putExtra("home" , true);
                startActivity(intent);
            }
        }

    }

    private class calenderOnDateChangeListener implements CalendarView.OnDateChangeListener{
        @Override
        public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
            openBillActivity(year,month,dayOfMonth);
        }

    }

    private class budgetOnClickListener implements View.OnClickListener{

        public void onClick(View view) {
            openBudgetActivity(user);
        }

    }

    private class expenseRecordOnClickListener implements View.OnClickListener{

        public void onClick(View view) {
            openBillRecord(false);
        }

    }

    private class incomeRecordOnClickListener implements View.OnClickListener{
        public void onClick(View view) {
            openBillRecord(true);
        }

    }

    private void updateButton(){
        double remain = user.getExpenseBudget() + user.getMonthlyExpense();
        if (remain < 0) {
            budgetButton.setTextColor(ContextCompat.getColor(this, R.color.expense));
        }
        else {

            budgetButton.setTextColor(ContextCompat.getColor(this, R.color.income));
        }
        budgetButton.setText(String.format("%.2f",remain));
        expenseButton.setText(String.format("%.2f", user.getMonthlyExpense()));
        incomeButton.setText(String.format("%.2f", user.getMonthlyIncome()));
    }
    private void openBudgetActivity(User user){
        Intent intent = new Intent(MainActivity.this, BudgetActivity.class);
        startActivity(intent);
    }
    private void openBillRecord (boolean income){
        Intent intent = new Intent(MainActivity.this, ViewBillRecords.class);
        intent.putExtra("income", income);
        intent.putExtra("type", getResources().getString(R.string.all));
        startActivity(intent);
    }
    private void openBillActivity(int year, int month, int dayOfMonth){
        Intent billIntent = new Intent(getApplicationContext(), BillActivity.class);
        billIntent.putExtra("Year", year);
        billIntent.putExtra("Month", month);
        billIntent.putExtra("DayOfMonth", dayOfMonth);
        billIntent.putExtra("MonthlyExpense", user.getMonthlyExpense());
        billIntent.putExtra("MonthlyIncome", user.getMonthlyIncome());
        startActivity(billIntent);

    }
}
