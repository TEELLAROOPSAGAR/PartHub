package com.example.spareit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.view.WindowManager;

import com.example.spareit.adapters.RVItemAdapter;

import java.lang.reflect.Array;

public class StatusActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RVItemAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setUpWindows();

        // Set local attributes to corresponding views
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_status_activity);

        // Set layout for the RecyclerView, because it's a list we are using the linear layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter=new RVItemAdapter(this,false);
        adapter.onlyThreshold();
        recyclerView.setAdapter(adapter);
        adapter.setListener(new RVItemAdapter.Listener() {
            @Override
            public void onClick(int position) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(),android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(StatusActivity.this, new String[]{android.Manifest.permission.CALL_PHONE},
                            0110);

                } else {
                    Intent intent=new Intent(StatusActivity.this,VendorProfileActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("NAME",adapter.items.get(position).vendorName);
                    extras.putString("PHONE",adapter.items.get(position).phoneNo);
                    extras.putString("ADDRESS",adapter.items.get(position).address);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            }
        });

    }

    public void setUpWindows(){
        Window window = this.getWindow();

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.white));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#000000\">" + "Status" + "</font>",0));

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