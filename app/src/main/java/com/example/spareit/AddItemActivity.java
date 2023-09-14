package com.example.spareit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.spareit.data.AppDatabase;
import com.example.spareit.data.Items;

import java.util.ArrayList;

public class AddItemActivity extends AppCompatActivity {
    EditText nameET;
    EditText countET;
    EditText vendorNameET;
    EditText vendorPhNoET;
    EditText pPriceET;
    EditText sPriceET;
    EditText addressET;

    String uid=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setUpWindows();

        nameET=findViewById(R.id.nameET);
        countET=findViewById(R.id.countET);
        vendorNameET=findViewById(R.id.VnameET);
        vendorPhNoET=findViewById(R.id.VnumET);
        pPriceET=findViewById(R.id.amountET);
        sPriceET=findViewById(R.id.saleET);
        addressET=findViewById(R.id.addressET);

        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            uid=extras.getString("uid");
            nameET.setText(extras.getString("Name"));
            countET.setText(String.valueOf(extras.getInt("count")));
            pPriceET.setText(String.valueOf(extras.getInt("purchasePrice")));
            sPriceET.setText(String.valueOf(extras.getInt("salePrice")));
            vendorNameET.setText(extras.getString("vendorName"));
            vendorPhNoET.setText(extras.getString("phoneNo"));
            addressET.setText(extras.getString("address"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.Save){

            String name=nameET.getText().toString().trim();
            String vendorName=vendorNameET.getText().toString().trim();
            String vendorPhNo=vendorPhNoET.getText().toString().trim();
            String address=addressET.getText().toString().trim();

            if(name.equals("")||countET.getText().toString().trim().equals("")||vendorName.equals("")||vendorPhNo.equals("")||pPriceET.getText().toString().trim().equals("")||sPriceET.getText().toString().trim().equals("")||address.equals("")){
                Toast.makeText(this,"Please enter valid details!",Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }
            int count=Integer.parseInt(countET.getText().toString().trim());
            int purchasePrice=Integer.parseInt(pPriceET.getText().toString().trim());
            int salePrice=Integer.parseInt(sPriceET.getText().toString().trim());

            Items items=new Items();
            items.name=name;
            items.count=count;
            items.purchasePrice=purchasePrice;
            items.salePrice=salePrice;
            items.vendorName=vendorName;
            items.phoneNo=vendorPhNo;
            items.address=address;
            items.threshold=10;
            items.salesInfo= new ArrayList<>();
            items.revenueInfo=new ArrayList<>();
            if(uid!=null)items.uid=Integer.parseInt(uid);
            AppDatabase.getDbInstance(this).userDao().insertItems(items);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setUpWindows(){
        Window window = this.getWindow();

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.white));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#000000\">" + "New Item"+ "</font>",0));

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