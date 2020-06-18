package com.example.personalaccounting;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
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
import java.util.GregorianCalendar;
import java.util.List;



public class ChartActivity extends AppCompatActivity {
    public static final float PARAMETER_ROW_WEIGHT_SUM = 2f;
    public static final float PARAMETER_ROW_ELEMENT_WEIGHT = 1f;
    public static final float PARAMETER_TEXT_SIZE = 15f;

    DatabaseHelper myDB;
    Intent intent;

    private BarChart barChart;
    private XAxis xAxis;
    private PieChart pieChart;

    ArrayList<Integer> yearList = new ArrayList<>();
    int selectedYear;
    int selectedMonth = -1;
    boolean selectedIncome = false;
    boolean selectedBarChart = true;
    String selectedType;
    Switch incomeSwitch;
    Spinner yearSpinner;
    Spinner monthSpinner;
    Spinner typeSpinner;
    ImageButton barChartButton;
    ImageButton pieChartButton;
    ImageButton chartButton;
    ImageButton homeButton;
    ImageButton settingButton;

    ArrayList<String> expenseTypeList;
    ArrayList<String> incomeTypeList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        selectedType = getResources().getString(R.string.all);

        intent = getIntent();
        barChart = findViewById(R.id.barChart_view);
        pieChart = findViewById(R.id.pieChart_view);
        yearSpinner = findViewById(R.id.chart_yearSpinner);
        monthSpinner = findViewById(R.id.chart_monthSpinner);
        typeSpinner = findViewById(R.id.chart_typeSpinner);
        incomeSwitch = findViewById(R.id.chart_incomeSwitch);
        barChartButton = findViewById(R.id.barChart_icon);
        pieChartButton = findViewById(R.id.pieChart_icon);
        chartButton = findViewById(R.id.chart_button);
        homeButton = findViewById(R.id.home_button);
        settingButton = findViewById(R.id.setting_button);

        myDB = new DatabaseHelper(this);
        yearList.add(myDB.getUserStartDate().get(Calendar.YEAR));
        selectedYear = yearList.get(0);


        barChartButton.setOnClickListener(new barChartIconOnClickListener());
        pieChartButton.setOnClickListener(new pieChartIconOnClickListener());
        chartButton.setOnClickListener(new chartButtonOnClickListener());
        homeButton.setOnClickListener(new homeButtonOnClickListener());
        settingButton.setOnClickListener(new settingButtonOnClickListener());

        expenseTypeList = myDB.getExpenseTypeList();
        expenseTypeList.add(0,getResources().getString(R.string.all));
        incomeTypeList = myDB.getIncomeTypeList();
        incomeTypeList.add(0, getResources().getString(R.string.all));

        initBarChart();
        showBarChart();
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

        ArrayList<String> monthList = new ArrayList<>();
        monthList.add(getResources().getString(R.string.not_available));
        for(String string: getResources().getStringArray(R.array.monthArray)){
            monthList.add(string);
        }
        monthSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthList.toArray()));
        monthSpinner.setOnItemSelectedListener(new monthSpinnerOnItemSelectedListener());

        incomeSwitch.setOnCheckedChangeListener(new incomeSwitchOnCheckedChangeListener());

        typeSpinner.setAdapter(new ArrayAdapter<>(ChartActivity.this, android.R.layout.simple_spinner_item, expenseTypeList));
        typeSpinner.setOnItemSelectedListener(new typeSpinnerOnItemSelectedListener());
    }

    private void initPieChart(){
        barChart.setVisibility(View.INVISIBLE);
        pieChart.setVisibility(View.VISIBLE);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDragDecelerationFrictionCoef(0.9f);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);


    }

    private void initBarChart(){
        pieChart.setVisibility(View.INVISIBLE);
        barChart.setVisibility(View.VISIBLE);
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

        YAxis leftAxis;
        YAxis rightAxis;
        Legend legend;

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
        final String[] monthArray = getResources().getStringArray(R.array.monthArray);
        List<Double> dateValueList = getBillDataOfYear(selectedYear, selectedIncome, selectedType);
        ArrayList<BarEntry> entries = new ArrayList<>();
        StringBuilder title = new StringBuilder(selectedYear + " ");

        if(selectedMonth == 1){
            GregorianCalendar calendar = new GregorianCalendar();
            title.append(monthArray[selectedMonth]);
            if(calendar.isLeapYear(selectedYear)){

            }
            else{

            }
        }
        else if (selectedMonth == 0 || selectedMonth == 2 || selectedMonth == 4 || selectedMonth == 6 ||
                selectedMonth == 7 || selectedMonth == 9 || selectedMonth == 11 ){
            title.append(monthArray[selectedMonth] + " ");
        }
        else if(selectedMonth == 3 || selectedMonth == 5 || selectedMonth == 8 || selectedMonth == 10){
            title.append(monthArray[selectedMonth] + " ");
        }
        else{
            xAxis.setValueFormatter(new IAxisValueFormatter() {

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return  monthArray[(int) value];
                }
            });
            for (int i = 0; i < dateValueList.size(); i++) {

                BarEntry barEntry = new BarEntry(i, dateValueList.get(i).floatValue());
                entries.add(barEntry);
            }

        }

        if(selectedType.equals(getResources().getString(R.string.all))){
            title.append(selectedIncome? getResources().getString(R.string.income):getResources().getString(R.string.expense));
        }
        else{
            title.append(selectedType);
        }

        BarDataSet barDataSet = new BarDataSet(entries, title.toString());
        initBarDataSet(barDataSet, ContextCompat.getColor(this, (selectedIncome?R.color.income:R.color.expense)));

        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.5f);
        barChart.setData(data);
    }

    private ArrayList<Double> getBillDataOfMonth(int year, int month, int numberOfDays, boolean income, String selectedType){
        ArrayList<Double> result = new ArrayList<>();
        Cursor data = myDB.getBillTable();
        for(int i = 0; i < numberOfDays; i++){
            result.add(0.0);
        }
        return result;
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
                if(billType.equals(type) || type.equals(getResources().getString(R.string.all))){
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

    private class barChartIconOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            recreate();
        }
    }

    private class pieChartIconOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {

        }
    }

    private class incomeSwitchOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if(isChecked) {
                typeSpinner.setAdapter(new ArrayAdapter<>(ChartActivity.this, android.R.layout.simple_spinner_item, incomeTypeList));
                selectedIncome = true;
            }
            else{
                typeSpinner.setAdapter(new ArrayAdapter<>(ChartActivity.this, android.R.layout.simple_spinner_item, expenseTypeList));
                selectedIncome = false;
            }
        }
    }
    private class barChartOnChartValueSelectedListener implements OnChartValueSelectedListener{

        @Override
        public void onValueSelected(Entry e, Highlight h) {
            Intent intent = new Intent(ChartActivity.this, ViewBillRecords.class);
            intent.putExtra("year", selectedYear);
            intent.putExtra("income", selectedIncome);
            intent.putExtra("month", (int) e.getX());
            intent.putExtra("type", selectedType);
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

    private class monthSpinnerOnItemSelectedListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            selectedMonth = position - 1;

            if(selectedBarChart){
                initBarChart();
                showBarChart();
            }


        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            selectedMonth = -1;
        }
    }

    private class yearSpinnerOnItemSelectedListener implements Spinner.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            selectedYear = Integer.valueOf(adapterView.getItemAtPosition(position).toString());
            if(selectedBarChart){
                initBarChart();
                showBarChart();
            }

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