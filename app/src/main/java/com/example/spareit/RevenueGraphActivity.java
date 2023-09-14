package com.example.spareit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.WindowManager;

import android.widget.Toast;

import com.example.spareit.data.AppDatabase;
import com.example.spareit.data.Items;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class RevenueGraphActivity extends AppCompatActivity {

    private BarChart chart;
    List<BarEntry> entries;
    ArrayList<String> xAxis;
    List<Items> items;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_graph);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        db= AppDatabase.getDbInstance(RevenueGraphActivity.this);
        items = db.userDao().getItems();

        chart = findViewById(R.id.chart1);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Intent intent=new Intent(RevenueGraphActivity.this,DayGraphActivity.class);
                Bundle extras=new Bundle();
                extras.putInt("MONTH",(int)e.getX());
                intent.putExtras(extras);
                startActivity(intent);
            }

            @Override
            public void onNothingSelected() {

            }
        });
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setMaxVisibleValueCount(60);
        chart.setScaleX(1f);

        chart.setPinchZoom(true);
        chart.enableScroll();


        chart.setDrawGridBackground(false);

        chart.setTouchEnabled(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(2f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(getXAxisValues());
        //Setting Values
        setUpBarData();

    }

    private void setUpBarData(){
        entries=new ArrayList<>();

        getData();

        BarDataSet set = new BarDataSet(entries, "BarDataSet");

        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);
        chart.animateXY(2000, 2000);
        chart.fitScreen();
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
    }

    private void getData(){
        if(items.size()<1)return;

        List<Pair<String,Integer>> intermediate=new ArrayList<>();

        for(int b=0;b<items.size();b++){
            intermediate.addAll(items.get(b).revenueInfo);
        }

        //Map of each day and get revenue
        Log.e("INTERMEDIATE LIST",intermediate.toString());
        Map<String, List<Pair<String, Integer>>> map =intermediate.stream()
                .collect(Collectors.groupingBy(s -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis((long)Double.parseDouble(s.first));
                    return "~" + cal.get(Calendar.MONTH);
                }));
        Map<String, Double> finalResult = map.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue()
                                .stream()
                                .mapToDouble(s -> s.second)
                                .sum()));
        Log.e("FINAL RESULT",finalResult.toString());
        finalResult=new TreeMap<String, Double>(finalResult);

        int i=0;
        for(Map.Entry<String, Double> entry : finalResult.entrySet()){
            Log.e("--------------------FINAL RESULT","Key:"+entry.getKey()+" Value:"+entry.getValue());
            entries.add(new BarEntry(Integer.parseInt(entry.getKey().substring(1)),(int)entry.getValue().doubleValue()));
        }
        for(;i<12;i++){
            entries.add(new BarEntry(i++,0));
        }
    }

    private ValueFormatter getXAxisValues() {
        xAxis = new ArrayList();

        xAxis.add("Jan");
        xAxis.add("Feb");
        xAxis.add("Mar");
        xAxis.add("Apr");
        xAxis.add("May");
        xAxis.add("Jun");
        xAxis.add("Jul");
        xAxis.add("Aug");
        xAxis.add("Sept");
        xAxis.add("Oct");
        xAxis.add("Nov");
        xAxis.add("Dec");

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return xAxis.get((int) value);
            }
        };

        return formatter;
    }
}