package com.example.personalaccounting;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ChartActivity extends AppCompatActivity {
    DatabaseHelper myDB;
    HorizontalScrollView chartIconView;
    Intent intent;

    private BarChart barChart;
    private YAxis leftAxis;
    private YAxis rightAxis;
    private XAxis xAxis;
    private Legend legend;

    TableLayout chartParameterTable;

    ImageButton chartButton;
    ImageButton homeButton;
    ImageButton settingButton;

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

        chartButton.setOnClickListener(new chartButtonOnClickListener());
        homeButton.setOnClickListener(new homeButtonOnClickListener());
        settingButton.setOnClickListener(new settingButtonOnClickListener());

    }

    @Override
    protected void onStart() {
        super.onStart();
        initBarChart();
        showBarChart(getExpenseBillDataOfYear(Calendar.getInstance().get(Calendar.YEAR)) ,"Expense", ContextCompat.getColor(this, R.color.expense));

    }

    private void initBarChart(){
        barChart.setBackgroundColor(ContextCompat.getColor(this, R.color.main_button));
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setHighlightFullBarEnabled(false);
        barChart.setDrawBorders(true);

        barChart.animateY(1000);
        barChart.animateX(1000);

        xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        leftAxis = barChart.getAxisLeft();
        rightAxis = barChart.getAxisRight();

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
        barDataSet.setDrawValues(false);

    }

    private void showBarChart(List<Double> dateValueList, String name, int color){
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

        BarDataSet barDataSet = new BarDataSet(entries, name);
        initBarDataSet(barDataSet, color);

        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.5f);
        barChart.setData(data);
    }

    private ArrayList<Double> getExpenseBillDataOfYear(int year){
        Cursor data = myDB.getBillTable();
        ArrayList<Double> result = new ArrayList<>();
        for(int i = 0; i < 12; i++){
            result.add(0.0);
        }
        while(data.moveToNext()){
            double amount = data.getDouble(1);
            int month = data.getInt(3);
            if(amount < 0 && data.getInt(2) == year){
                result.set(month, result.get(month) + Math.abs(amount));
            }
        }

        return result;
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