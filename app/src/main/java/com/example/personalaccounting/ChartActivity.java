package com.example.personalaccounting;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;



public class ChartActivity extends AppCompatActivity {
    public static final float PARAMETER_ROW_WEIGHT_SUM = 2f;
    public static final float PARAMETER_ROW_ELEMENT_WEIGHT = 1f;
    public static final float PARAMETER_TEXT_SIZE = 15f;

    DatabaseHelper myDB;
    HorizontalScrollView chartIconView;
    Intent intent;

    private BarChart barChart;
    private YAxis leftAxis;
    private YAxis rightAxis;
    private XAxis xAxis;
    private Legend legend;

    TableLayout chartParameterTable;
    ArrayList<Integer> yearList = new ArrayList<>();
    int selectedYear;
    boolean selectedIncome = false;
    String selectedType = "All";
    Spinner typeSpinner;

    ImageButton chartButton;
    ImageButton homeButton;
    ImageButton settingButton;

    ArrayList<String> expenseTypeList;
    ArrayList<String> incomeTypeList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        intent = getIntent();
        chartIconView = findViewById(R.id.chart_iconView);
        barChart = findViewById(R.id.barChart_view);
        chartParameterTable = findViewById(R.id.chart_parameter_table);
        chartButton = findViewById(R.id.chart_button);
        homeButton = findViewById(R.id.home_button);
        settingButton = findViewById(R.id.setting_button);

        myDB = new DatabaseHelper(this);
        yearList.add(myDB.getUserStartDate().get(Calendar.YEAR));
        selectedYear = yearList.get(0);

        chartButton.setOnClickListener(new chartButtonOnClickListener());
        homeButton.setOnClickListener(new homeButtonOnClickListener());
        settingButton.setOnClickListener(new settingButtonOnClickListener());

    }

    @Override
    protected void onStart() {
        super.onStart();

        expenseTypeList = myDB.getExpenseTypeList();
        expenseTypeList.add(0,"All");
        incomeTypeList = myDB.getIncomeTypeList();
        incomeTypeList.add(0, "All");

        initBarChart();
        showBarChart();
        initBarChartParameterTable();
    }

    private void initBarChartParameterTable(){
        chartParameterTable.removeView(chartParameterTable.getRootView());
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.general_column_height), PARAMETER_ROW_ELEMENT_WEIGHT);

        TableRow yearRow = new TableRow(getApplicationContext());
        yearRow.setWeightSum(PARAMETER_ROW_WEIGHT_SUM);

        TextView yearText = new TextView(getApplicationContext());
        yearText.setText(R.string.year);
        yearText.setTextSize(PARAMETER_TEXT_SIZE);
        yearText.setGravity(Gravity.CENTER);
        yearText.setLayoutParams(rowParams);

        Spinner yearSpinner = new Spinner(getApplicationContext());
        yearSpinner.setLayoutParams(rowParams);
        yearSpinner.setGravity(Gravity.CENTER);

        Calendar lastActiveDate = myDB.getUserLastActiveDate();
        int year = yearList.get(0);
        while (year <= lastActiveDate.get(Calendar.YEAR)){
            if(!yearList.contains(year)){
                yearList.add(year);
            }
            year++;
        }

        Collections.sort(yearList);
        yearSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearList.toArray()));
        yearSpinner.setOnItemSelectedListener(new yearSpinnerOnItemSelectedListener());

        yearRow.addView(yearText);
        yearRow.addView(yearSpinner);
        chartParameterTable.addView(yearRow);

        TableRow incomeSwitchRow = new TableRow(getApplicationContext());
        incomeSwitchRow.setWeightSum(PARAMETER_ROW_WEIGHT_SUM);

        TextView switchText = new TextView(getApplicationContext());
        switchText.setText(getResources().getString(R.string.income));
        switchText.setTextSize(PARAMETER_TEXT_SIZE);
        switchText.setGravity(Gravity.CENTER);
        switchText.setLayoutParams(rowParams);

        Switch incomeSwitch = new Switch(getApplicationContext());
        incomeSwitch.setTextOff(getResources().getString(R.string.expense));
        incomeSwitch.setTextOn(getResources().getString(R.string.income));
        incomeSwitch.setShowText(true);
        incomeSwitch.setOnCheckedChangeListener(new incomeSwitchOnCheckedChangeListener());

        incomeSwitchRow.addView(switchText);
        incomeSwitchRow.addView(incomeSwitch);
        chartParameterTable.addView(incomeSwitchRow);

        TableRow typeRow = new TableRow(getApplicationContext());
        typeRow.setWeightSum(PARAMETER_ROW_WEIGHT_SUM);

        TextView typeText = new TextView(getApplicationContext());
        typeText.setText(getResources().getString(R.string.type));
        typeText.setTextSize(PARAMETER_TEXT_SIZE);
        typeText.setGravity(Gravity.CENTER);
        typeText.setLayoutParams(rowParams);

        typeSpinner = new Spinner(getApplicationContext());
        typeSpinner.setAdapter(new ArrayAdapter<>(ChartActivity.this, android.R.layout.simple_spinner_item, expenseTypeList));
        typeSpinner.setGravity(Gravity.CENTER);
        typeSpinner.setLayoutParams(rowParams);
        typeSpinner.setOnItemSelectedListener(new typeSpinnerOnItemSelectedListener());

        typeRow.addView(typeText);
        typeRow.addView(typeSpinner);
        chartParameterTable.addView(typeRow);
    }

    private void initBarChart(){
        barChart.setBackgroundColor(ContextCompat.getColor(this, R.color.chart_background));
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setHighlightFullBarEnabled(false);
        barChart.setDrawBorders(false);
        Description description = new Description();
        description.setEnabled(false);
        barChart.setDescription(description);

        barChart.animateY(1000);
        barChart.animateX(1000);

        barChart.setOnChartValueSelectedListener(new barChartOnChartValueSelectedListener());

        xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);

        leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawAxisLine(false);
        rightAxis = barChart.getAxisRight();
        rightAxis.setDrawAxisLine(false);

        legend = barChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(11f);

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        legend.setDrawInside(false);
    }

    private void initBarDataSet(BarDataSet barDataSet, int color){
        barDataSet.setColor(color);
        barDataSet.setFormLineWidth(1f);
        barDataSet.setFormSize(15.f);
        barDataSet.setDrawValues(true);
        barDataSet.setValueTextSize(12f);

    }

    private void showBarChart(){
        List<Double> dateValueList = getBillDataOfYear(selectedYear, selectedIncome, selectedType);

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            final String[] monthArray = getResources().getStringArray(R.array.monthArray);
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return  monthArray[(int) value];
            }
        });
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < dateValueList.size(); i++) {

            BarEntry barEntry = new BarEntry(i, dateValueList.get(i).floatValue());
            entries.add(barEntry);
        }

        String title = "";
        if(selectedType.equals("All")){
            title = selectedYear + (selectedIncome?" Income":" Expense");
        }
        else{
            title = selectedYear + " " + selectedType;
        }

        BarDataSet barDataSet = new BarDataSet(entries, title);
        initBarDataSet(barDataSet, ContextCompat.getColor(this, (selectedIncome?R.color.income:R.color.expense)));

        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.5f);
        barChart.setData(data);
    }

    private ArrayList<Double> getBillDataOfYear(int year, boolean income, String type){
        Cursor data = myDB.getBillTable();
        ArrayList<Double> result = new ArrayList<>();
        for(int i = 0; i < 12; i++){
            result.add(0.0);
        }
        while(data.moveToNext()){
            double amount = data.getDouble(1);
            int month = data.getInt(3);
            String billType = data.getString(5);
            if (data.getInt(2) == year){
                if(billType.equals(type) || type.equals("All")){
                    if(income && amount > 0){
                        result.set(month, result.get(month) + Math.abs(amount));
                    }
                    else if(!income && amount < 0){
                        result.set(month, result.get(month) + Math.abs(amount));
                    }
                }
            }

        }

        return result;
    }

    private class barChartOnChartValueSelectedListener implements OnChartValueSelectedListener{

        @Override
        public void onValueSelected(Entry e, Highlight h) {
            int month = (int) e.getX();
            Intent intent = new Intent(ChartActivity.this, ViewBillRecords.class);
            startActivity(intent);
        }

        @Override
        public void onNothingSelected() {

        }
    }
    private class typeSpinnerOnItemSelectedListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            selectedType = adapterView.getItemAtPosition(position).toString();
            initBarChart();
            showBarChart();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
    private class incomeSwitchOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if(isChecked){
                selectedIncome = true;
                typeSpinner.setAdapter(new ArrayAdapter<>(ChartActivity.this, android.R.layout.simple_spinner_item, incomeTypeList));

            }
            else {
                selectedIncome = false;
                typeSpinner.setAdapter(new ArrayAdapter<>(ChartActivity.this, android.R.layout.simple_spinner_item, expenseTypeList));
            }

            initBarChart();
            showBarChart();
        }
    }
    private class yearSpinnerOnItemSelectedListener implements Spinner.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            selectedYear = Integer.valueOf(adapterView.getItemAtPosition(position).toString());
            initBarChart();
            showBarChart();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class chartButtonOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            onRestart();
        }
    }

    private class homeButtonOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if(intent.getBooleanExtra("home", false)){
                finish();
            }
            else{
                Intent intent = new Intent(ChartActivity.this, MainActivity.class);
                intent.putExtra("chart", true);
                startActivity(intent);
            }
        }
    }

    private class settingButtonOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if(intent.getBooleanExtra("setting", false)){
                finish();
            }
            else{
                Intent intent = new Intent(ChartActivity.this, SettingActivity.class);
                intent.putExtra("chart", true);
                startActivity(intent);
            }
        }
    }
}