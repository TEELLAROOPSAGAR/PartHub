package com.example.spareit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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

public class StartActivity extends AppCompatActivity {

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().hide();

        setUpWindows();

        Button adminLogin=findViewById(R.id.adminLogin);
        Button employeeLogin=findViewById(R.id.empLogin);

        prefs = getSharedPreferences("MY_PREFS",MODE_PRIVATE);
        prefs.edit().putString("PASSWORD_ADMIN","ADMIN123").apply();
        prefs.edit().putString("19JE0427","SELab").apply();

        adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(StartActivity.this);
                LayoutInflater inflater = StartActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_admin, null);
                dialogBuilder.setView(dialogView);

                final EditText passwordET = dialogView.findViewById(R.id.passAdmin);

                dialogBuilder.setTitle("Enter Password");
                dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        String password= String.valueOf(passwordET.getText().toString().trim());
                        if(!password.equalsIgnoreCase("")){
                            if(prefs.getString("PASSWORD_ADMIN","").equals(password)){
                                gotoMain();
                            }else{
                                Toast.makeText(StartActivity.this,"Enter correct Password",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(StartActivity.this,"Enter Password",Toast.LENGTH_LONG).show();
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
        });

        employeeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(StartActivity.this);
                LayoutInflater inflater = StartActivity.this.getLayoutInflater();
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
                            if(prefs.getString(""+empID,"").equals(password)){
                                gotoEmp();
                            }else{
                                Toast.makeText(StartActivity.this,"Enter correct Password",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(StartActivity.this,"Enter details correctly!",Toast.LENGTH_LONG).show();
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
        });
    }

    public void gotoMain(){
        Intent intent=new Intent(StartActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void gotoEmp(){
        Intent intent=new Intent(StartActivity.this,ItemSelectActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);;
        startActivity(intent);
    }

    public void setUpWindows(){
        Window window = this.getWindow();

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.white));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#000000\">" + getString(R.string.app_name) + "</font>",0));

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));
    }
}