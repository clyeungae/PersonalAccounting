package com.example.personalaccounting;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class BudgetActivity extends AppCompatActivity {

    DatabaseHelper myDB;
    User user;

    TextView budgetView;
    TextView remainView;
    TextView expenseView;

    TableLayout expenseDetailTable;
    TableLayout incomeDetailTable;

    ArrayList<String> expenseTypeList;
    ArrayList<String> incomeTypeList;
    LinkedHashMap<String, Double> expenseTypeBudgetMap;
    LinkedHashMap<String, Double> incomeTypeBudgetMap;

    HashMap<String, Double> expenseTypeAmountMap = new HashMap<>();
    HashMap<String, Double> incomeTypeAmountMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        myDB = new DatabaseHelper(this);
        budgetView = (TextView) findViewById(R.id.budget_view);
        remainView = (TextView) findViewById(R.id.budget_remain_view);
        expenseView = (TextView) findViewById(R.id.budget_expense_view);
        expenseDetailTable = (TableLayout) findViewById(R.id.budget_expense_detail);
        incomeDetailTable = (TableLayout) findViewById(R.id.budget_income_detail);

    }

    @Override
    protected void onStart() {
        super.onStart();

        user = myDB.getUserInfo();
        updateExpenseSummaryView();
        budgetView.setOnClickListener(new budgetViewOnClickerListener());

        expenseTypeList = myDB.getExpenseTypeList();
        incomeTypeList = myDB.getIncomeTypeList();

        Cursor currentMonthBillData = myDB.getBillWithYearAndMonth(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH));

        expenseTypeBudgetMap = myDB.getExpenseTypeBudgetMap();
        incomeTypeBudgetMap = myDB.getIncomeTypeBudgetMap();
        try{
            while(currentMonthBillData.moveToNext()){
                double amount = currentMonthBillData.getDouble(1);
                String type = currentMonthBillData.getString(5);
                if(amount > 0){
                    if(!incomeTypeList.contains(type)){
                        incomeTypeList.add(type);
                    }
                    if(incomeTypeAmountMap.containsKey(type)){
                        incomeTypeAmountMap.replace(type, incomeTypeAmountMap.get(type) + amount);
                    }
                    else{
                        incomeTypeAmountMap.put(type, amount);
                    }
                }
                else{
                    if (!expenseTypeList.contains(type)){
                        expenseTypeList.add(type);
                    }
                    if(expenseTypeAmountMap.containsKey(type)){
                        expenseTypeAmountMap.replace(type, expenseTypeAmountMap.get(type) + amount);
                    }
                    else{
                        expenseTypeAmountMap.put(type, amount);
                    }

                }
            }

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        for(String type: expenseTypeList){
            TableRow tableRow = new TableRow(getApplicationContext());
            TextView typeTextView = new TextView(getApplicationContext());
            TextView budgetView = new TextView(getApplicationContext());
            TextView amountView = new TextView(getApplicationContext());
            TextView remainView = new TextView(getApplicationContext());

            tableRow.setWeightSum(4.0f);
            tableRow.setOnClickListener(new tableRowOnclickListener(false, type));
            typeTextView.setText(type);
            typeTextView.setTextSize(15);
            typeTextView.setGravity(Gravity.CENTER);

            double expenseBudget = expenseTypeBudgetMap.getOrDefault(type, 0.0);
            double expenseAmount = expenseTypeAmountMap.getOrDefault(type, 0.0);
            budgetView.setGravity(Gravity.CENTER);
            budgetView.setText(String.valueOf(expenseBudget));

            amountView.setGravity(Gravity.CENTER);
            amountView.setText(String.valueOf(Math.abs(expenseAmount)));


            remainView.setGravity(Gravity.CENTER);
            double remain = expenseBudget + expenseAmount;
            remainView.setText(String.valueOf(remain));
            if(remain > 0){
                remainView.setTextColor(ContextCompat.getColor(this, R.color.income));
            }
            else{
                remainView.setTextColor(ContextCompat.getColor(this, R.color.expense));
            }

            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 100, 1.0f);
            tableRow.addView(typeTextView, rowParams);
            tableRow.addView(budgetView, rowParams);
            tableRow.addView(amountView, rowParams);
            tableRow.addView(remainView, rowParams);
            expenseDetailTable.addView(tableRow);

        }

        for(String type:incomeTypeList){
            TableRow tableRow = new TableRow(getApplicationContext());
            TextView typeTextView = new TextView(getApplicationContext());
            TextView budgetView = new TextView(getApplicationContext());
            TextView amountView = new TextView(getApplicationContext());
            TextView remainView = new TextView(getApplicationContext());

            tableRow.setWeightSum(4.0f);
            tableRow.setOnClickListener(new tableRowOnclickListener(true, type));
            typeTextView.setText(type);
            typeTextView.setTextSize(15);
            typeTextView.setGravity(Gravity.CENTER);

            double incomeBudget = incomeTypeBudgetMap.getOrDefault(type, 0.0);
            double incomeAmount = incomeTypeAmountMap.getOrDefault(type, 0.0);
            budgetView.setGravity(Gravity.CENTER);
            budgetView.setText(String.valueOf(incomeBudget));

            amountView.setGravity(Gravity.CENTER);
            amountView.setText(String.valueOf(incomeAmount));

            remainView.setGravity(Gravity.CENTER);
            double remain = incomeBudget - incomeAmount;
            remainView.setText(String.valueOf(remain));
            if(remain > 0){
                remainView.setTextColor(ContextCompat.getColor(this, R.color.expense));
            }
            else{
                remainView.setTextColor(ContextCompat.getColor(this, R.color.income));
            }

            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 100, 1.0f);
            tableRow.addView(typeTextView, rowParams);
            tableRow.addView(budgetView, rowParams);
            tableRow.addView(amountView, rowParams);
            tableRow.addView(remainView, rowParams);
            incomeDetailTable.addView(tableRow);
        }

    }

    private class tableRowOnclickListener implements View.OnClickListener{

        boolean income;
        String type;

        public tableRowOnclickListener(boolean income, String type){
            this.income = income;
            this.type = type;
        }
        @Override
        public void onClick(View view) {
            Intent intent = new Intent (BudgetActivity.this, ViewBillRecords.class);
            intent.putExtra("income", income);
            intent.putExtra("type", type);
            intent.putExtra("year", Calendar.getInstance().get(Calendar.YEAR));
            intent.putExtra("month", Calendar.getInstance().get(Calendar.MONTH));
            startActivity(intent);
        }
    }

    private class budgetViewOnClickerListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(BudgetActivity.this, BudgetSettingActivity.class);
            startActivity(intent);
        }
    }

    private void updateExpenseSummaryView(){

        budgetView.setText(String.format("%.2f", user.getExpenseBudget()));
        expenseView.setText(String.format("%.2f", Math.abs(user.getMonthlyExpense())));
        remainView.setText(String.format("%.2f", user.getExpenseBudget()+user.getMonthlyExpense()));
        if (user.getExpenseBudget() > -user.getMonthlyExpense())
            remainView.setTextColor(ContextCompat.getColor(this, R.color.income));
        else
            remainView.setTextColor(ContextCompat.getColor(this, R.color.expense));
    }

}