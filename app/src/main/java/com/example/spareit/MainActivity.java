package com.example.spareit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = this.getWindow();

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.white));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#000000\">" + getString(R.string.app_name) + "</font>",0));

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));

        Button itemButton=findViewById(R.id.item);
        Button statusButton=findViewById(R.id.status);
        Button addEmp=findViewById(R.id.addEmployee);
        Button addStock=findViewById(R.id.addStock);
        Button graph=findViewById(R.id.revenueGraph);

        itemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,ItemActivity.class);
                startActivity(intent);
            }
        });

        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,StatusActivity.class);
                startActivity(intent);
            }
        });

        addStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,AddStockActivity.class);
                startActivity(intent);
            }
        });

        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,RevenueGraphActivity.class);
                startActivity(intent);
            }
        });

        addEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEmployee();
                //TO DELETE ALL PREFS getApplicationContext().getSharedPreferences("PREF_NAME", 0).edit().clear().apply();
            }
        });
    }

    public void addEmployee(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_employee, null);
        dialogBuilder.setView(dialogView);

        final EditText empIDET = dialogView.findViewById(R.id.empID);
        final EditText passwordET = dialogView.findViewById(R.id.passEmp);

        dialogBuilder.setTitle("Enter Details");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                String empID= empIDET.getText().toString().trim();
                String password= passwordET.getText().toString().trim();
                if(!password.equalsIgnoreCase("")||!empID.equalsIgnoreCase("")){
                    prefs = getSharedPreferences("MY_PREFS",MODE_PRIVATE);
                    prefs.edit().putString(""+empID,""+password).apply();
                }else{
                    Toast.makeText(MainActivity.this,"Enter non-empty Details",Toast.LENGTH_LONG).show();
                }

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {

            }
        });
        AlertDialog ad = dialogBuilder.create();
        ad.setCancelable(false);
        ad.show();
    }
}