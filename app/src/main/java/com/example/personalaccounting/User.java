package com.example.personalaccounting;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class User {
    private double expenseBudget = 0;
    private double incomeBudget = 0;
    private double monthlyExpense = 0.00;
    private double monthlyIncome = 0.00;
    private ArrayList<String> incomeType = new ArrayList<>(Arrays.asList("Salary", "Other"));
    private ArrayList<String> expenseType = new ArrayList<>(Arrays.asList("Clothing", "Food", "Housing", "Transport", "Other"));
    private final LinkedHashMap<String, Double> defaultIncomeTypeBudget;
    private final LinkedHashMap<String, Double> defaultExpenseTypeBudget;
    private LinkedHashMap<String, Double> incomeTypeBudget;
    private LinkedHashMap<String, Double> expenseTypeBudget;

    public User(final Context context){
        defaultExpenseTypeBudget = new LinkedHashMap<String, Double>(){
            {
                put(context.getResources().getString(R.string.clothing), 0.0);
                put(context.getResources().getString(R.string.food), 0.0);
                put(context.getResources().getString(R.string.housing), 0.0);
                put(context.getResources().getString(R.string.transport), 0.0);
                put(context.getResources().getString(R.string.other), 0.0);
            }
        };
        expenseTypeBudget = new LinkedHashMap<>(defaultExpenseTypeBudget);

        defaultIncomeTypeBudget = new LinkedHashMap<String, Double>(){
            {
                put(context.getResources().getString(R.string.salary), 0.0);
                put(context.getResources().getString(R.string.other), 0.0);
            }
        };
        incomeTypeBudget = new LinkedHashMap<>(defaultIncomeTypeBudget);
    }

    public double getMonthlyExpense() {
        return monthlyExpense;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public double getExpenseBudget() {
        return expenseBudget;
    }

    public LinkedHashMap<String, Double> getExpenseTypeBudgetMap() {
        return expenseTypeBudget;
    }

    public LinkedHashMap<String, Double> getIncomeTypeBudgetMap() {
        return incomeTypeBudget;
    }

    public LinkedHashMap<String, Double> getDefaultExpenseTypeBudget() {
        return defaultExpenseTypeBudget;
    }

    public LinkedHashMap<String, Double> getDefaultIncomeTypeBudget() {
        return defaultIncomeTypeBudget;
    }

    public void setExpenseBudget(double expenseBudget) {
        this.expenseBudget = expenseBudget;
    }

    public void setMonthlyIncome(double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public void setMonthlyExpense(double monthlyExpense) {
        this.monthlyExpense = monthlyExpense;
    }

    public void setExpenseTypeBudgetMap(LinkedHashMap<String, Double> expenseTypeBudget) {
        this.expenseTypeBudget = expenseTypeBudget;
    }

    public void setIncomeTypeBudgetMap(LinkedHashMap<String, Double> incomeTypeBudget) {
        this.incomeTypeBudget = incomeTypeBudget;
    }

    public double getIncomeBudget() {
        return incomeBudget;
    }

    public void setIncomeBudget(double incomeBudget) {
        this.incomeBudget = incomeBudget;
    }
}