package com.example.spareit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spareit.adapters.RVItemAdapter;
import com.example.spareit.adapters.RVItemSelectAdapter;
import com.example.spareit.data.AppDatabase;
import com.example.spareit.data.Items;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ItemSelectActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RVItemSelectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_select);

        setUpWindows();

        TextView total=findViewById(R.id.total);

        // Set local attributes to corresponding views
        recyclerView = (RecyclerView) this.findViewById(R.id.recycler_view_item_select_activity);

        // Set layout for the RecyclerView, because it's a list we are using the linear layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter=new RVItemSelectAdapter(this);
        adapter.setListener(new RVItemAdapter.Listener() {
            @Override
            public void onClick(int position) {
                total.setText("Total : Rs."+adapter.Total);
                if(position==-1){
                    Toast.makeText(ItemSelectActivity.this,"Stock Unavailable",Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void placeOrder(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(ItemSelectActivity.this);
        builder.setMessage("Are you sure you want to bill?")
                .setTitle("Bill Details")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AppDatabase db=AppDatabase.getDbInstance(ItemSelectActivity.this);

                        for(int i=0;i<adapter.counts.size();i++){
                            if(adapter.counts.get(i)>0){
                                db.userDao().deleteItem(adapter.items.get(i).uid);
                                adapter.items.get(i).count-=adapter.counts.get(i);

                                //Formatting date to store in database
                                Calendar calendar=Calendar.getInstance();
                                String date=String.valueOf(calendar.getTimeInMillis());//sdf.format(calendar.getTime());
                                adapter.items.get(i).salesInfo.add(new Pair<>(date,adapter.counts.get(i)));
                                adapter.items.get(i).revenueInfo.add(new Pair<>(date,adapter.counts.get(i)*(adapter.items.get(i).salePrice-adapter.items.get(i).purchasePrice)));
                                updateThreshold(i);
                                db.userDao().insertItems(adapter.items.get(i));

                            }
                        }

                        adapter.notifyDataSetChanged();

                        Intent intent=new Intent(ItemSelectActivity.this,OrderPlacedActivity.class);
                        startActivity(intent);

                        ItemSelectActivity.this.recreate();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.notifyDataSetChanged();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void updateThreshold(int i){

        //Get Map as list of sales in each week.
        Map<String,List<Pair<String,Integer>>> map=adapter.items.get(i).salesInfo.stream()
                .collect(Collectors.groupingBy(s->{
                    Calendar cal=Calendar.getInstance();
                    cal.setTimeInMillis((long)Double.parseDouble(s.first));
                    return (cal.get(Calendar.YEAR))+"~"+cal.get(Calendar.WEEK_OF_YEAR);
                }));

        //Map of each week and count sold
        Map<String, Double> finalResult = map.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue()
                                .stream()
                                .mapToDouble(s -> s.second)
                                .sum()));
        //Assigning average
        adapter.items.get(i).threshold = (int)finalResult.entrySet()
                .stream()
                .mapToDouble(x -> x.getValue())
                .average()
                .orElse(10); ;

    }

    public void setUpWindows(){
        Window window = this.getWindow();

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.white));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#000000\">" + "Select Items" + "</font>",0));

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        final Drawable upArrow = this.getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }
}