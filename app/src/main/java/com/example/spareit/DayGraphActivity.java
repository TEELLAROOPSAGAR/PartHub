package com.example.spareit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
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
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;


import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class DayGraphActivity extends AppCompatActivity {

    private BarChart chart;
    List<BarEntry> entries;
    ArrayList<String> xAxis;
    List<Items> items;
    AppDatabase db;
    int _MONTH_;
    Month month;
    int _DAYS_;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_graph);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null) {
            _MONTH_ = bundle.getInt("MONTH");
        }

        setDays();
        _DAYS_=month.length(false);

        db= AppDatabase.getDbInstance(DayGraphActivity.this);
        items = db.userDao().getItems();

        chart = findViewById(R.id.chart2);
        chart.setTouchEnabled(false);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(DayGraphActivity.this,""+xAxis.get((int)e.getX())+":",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(getXAxisValues());

        //Setting Values
        setUpBarData();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpBarData(){
        entries=new ArrayList<>();

        getData();

        BarDataSet set = new BarDataSet(entries, "BarDataSet");

        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);
        chart.animateXY(2000, 2000);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.enableScroll();
        chart.scrollBy(2,0);
        chart.invalidate(); // refresh
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getData(){
        if(items.size()<1)return;

        List<Pair<String,Integer>> intermediate=new ArrayList<>();

        for(int b=0;b<items.size();b++){
            intermediate.addAll(items.get(b).revenueInfo);
        }

        intermediate.removeIf(s -> {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis((long)Double.parseDouble(s.first));
            return cal.get(Calendar.MONTH)+1!=month.getValue();
        });
        //Map of each week and count sold\
        Log.e("INTERMEDIATE LIST",intermediate.toString());
        Map<String, List<Pair<String, Integer>>> map =intermediate.stream()
                .collect(Collectors.groupingBy(s -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis((long)Double.parseDouble(s.first));
                    return "~" + cal.get(Calendar.DAY_OF_MONTH);
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
            entries.add(new BarEntry(Integer.parseInt(entry.getKey().substring(1))-1,(int)entry.getValue().doubleValue()));
        }
        for(;i<12;i++){
            entries.add(new BarEntry(i++,0));
        }
    }

    private void setDays(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        switch(_MONTH_+1){
            case 0: month= Month.JANUARY;break;
            case 1:month= Month.FEBRUARY;break;
            case 2:month= Month.MARCH;break;
            case 3:month= Month.APRIL;break;
            case 4:month= Month.MAY;break;
            case 5:month= Month.JUNE;break;
            case 6:month= Month.JULY;break;
            case 7:month= Month.AUGUST;break;
            case 8:month= Month.SEPTEMBER;break;
            case 9:month= Month.OCTOBER;break;
            case 10:month= Month.NOVEMBER;break;
            case 11:month= Month.DECEMBER;break;
        }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ValueFormatter getXAxisValues() {
        xAxis = new ArrayList();

        for(int i=1;i<_DAYS_;i++){
            xAxis.add(i+" "+month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        }

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return xAxis.get((int) value);
            }
        };


        return formatter;
    }
}