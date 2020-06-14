package com.example.personalaccounting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.HashSet;

public class User {
    private double budget = 0;
    private double monthlyExpense = 0.00;
    private double monthlyIncome = 0.00;
    private ArrayList<String> incomeType = new ArrayList<String>(Arrays.asList("Salary", "Other"));
    private ArrayList<String> expenseType = new ArrayList<String>(Arrays.asList("Clothing", "Food", "Housing", "Transport", "Other"));
    private LinkedHashMap<String, Double> incomeTypeBudget = new LinkedHashMap<String, Double>(){
        {
            put("Salary", 0.0);
            put("Other", 0.0);
        }
    };
    private LinkedHashMap<String, Double> expenseTypeBudget = new LinkedHashMap<String, Double>(){
        {
            put("Clothing", 0.0);
            put("Food", 0.0);
            put("Housing", 0.0);
            put("Transport", 0.0);
            put("Other", 0.0);
        }
    };

    public User(){}

    public User(double budget, double monthlyExpense, double monthlyIncome) {
        this.budget = budget;
        this.monthlyExpense = monthlyExpense;
        this.monthlyIncome = monthlyIncome;
    }

    public User(double budget, double monthlyExpense, double monthlyIncome, LinkedHashMap<String, Double> expenseTypeBudget , LinkedHashMap<String, Double> incomeTypeBudget) {
        this.budget = budget;
        this.monthlyExpense = monthlyExpense;
        this.monthlyIncome = monthlyIncome;
        this.incomeTypeBudget = incomeTypeBudget;
        this.expenseTypeBudget = expenseTypeBudget;
    }

    public User(double budget, double monthlyExpense, double monthlyIncome, ArrayList<String> expenseType, ArrayList<String> incomeType) {
        this.budget = budget;
        this.monthlyIncome = monthlyIncome;
        this.monthlyExpense = monthlyExpense;
        for(String string: expenseType){
            expenseTypeBudget.put(string, 0.0);
        }
        for(String string: incomeType){
            incomeTypeBudget.put(string, 0.0);
        }
        this.incomeType = incomeType;
        this.expenseType = expenseType;
    }

    public double getMonthlyExpense() {
        return monthlyExpense;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public double getBudget() {
        return budget;
    }

    public LinkedHashMap<String, Double> getExpenseTypeBudgetMap() {
        return expenseTypeBudget;
    }

    public LinkedHashMap<String, Double> getIncomeTypeBudgetMap() {
        return incomeTypeBudget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
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
}