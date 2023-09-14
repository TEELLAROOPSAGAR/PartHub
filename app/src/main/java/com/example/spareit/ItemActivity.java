package com.example.spareit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.spareit.adapters.RVItemAdapter;
import com.example.spareit.data.AppDatabase;
import com.example.spareit.data.Items;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ItemActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RVItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        setUpWindows();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        // Set local attributes to corresponding views
        recyclerView = (RecyclerView) this.findViewById(R.id.recycler_view_item_activity);

        // Set layout for the RecyclerView, because it's a list we are using the linear layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter=new RVItemAdapter(this,true);
        recyclerView.setAdapter(adapter);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

            @Override
            public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(ItemActivity.this,R.color.green))
                        .addSwipeRightActionIcon(R.drawable.ic_edit)
                        .setSwipeRightActionIconTint(ContextCompat.getColor(ItemActivity.this,R.color.white))
                        .addSwipeRightLabel("EDIT")
                        .setSwipeRightLabelColor(ContextCompat.getColor(ItemActivity.this,R.color.white))
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(ItemActivity.this, R.color.red))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .setSwipeLeftActionIconTint(ContextCompat.getColor(ItemActivity.this, R.color.white))
                        .addSwipeLeftLabel("DELETE")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(ItemActivity.this, R.color.white))
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //Left dirction=4 and Right direction=8
                if(direction==8){
                    //Edit by delete and add
                    AlertDialog.Builder builder = new AlertDialog.Builder(ItemActivity.this);
                    builder.setMessage("Are you sure you want to edit?")
                            .setTitle("Edit Details")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    AppDatabase db=AppDatabase.getDbInstance(ItemActivity.this);
                                    int p=viewHolder.getAbsoluteAdapterPosition();
                                    Items s=adapter.items.get(p);

                                    db.userDao().deleteItem(s.uid);

                                    adapter.removeItem(p);

                                    Intent intent =new Intent(ItemActivity.this,AddItemActivity.class);
                                    Bundle extras = new Bundle();
                                    extras.putString("uid",String.valueOf(s.uid));
                                    extras.putString("Name",s.name);
                                    extras.putInt("count",s.count);
                                    extras.putInt("purchasePrice",s.purchasePrice);
                                    extras.putInt("salePrice",s.salePrice);
                                    extras.putString("vendorName",s.vendorName);
                                    extras.putString("phoneNo",s.phoneNo);
                                    extras.putString("address",s.address);
                                    intent.putExtras(extras);
                                    startActivity(intent);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else if(direction==4){

                    //Delete from database
                    AlertDialog.Builder builder = new AlertDialog.Builder(ItemActivity.this);
                    builder.setMessage("Are you sure you want to delete this item?")
                            .setTitle("Delete Spare part details")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AppDatabase db=AppDatabase.getDbInstance(ItemActivity.this);
                                    int p=viewHolder.getAbsoluteAdapterPosition();
                                    Items s=adapter.items.get(p);

                                    db.userDao().deleteItem(s.uid);

                                    adapter.removeItem(p);
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

            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        menu.getItem(0).setTitle("ADD");
        menu.getItem(0).setIcon(ContextCompat.getDrawable(this,R.drawable.ic_baseline_library_add));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.Save){
            Intent intent =new Intent(ItemActivity.this,AddItemActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        adapter.dataSetChanged();
        super.onResume();
    }

    public void setUpWindows(){
        Window window = this.getWindow();

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.white));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#000000\">" + "Items" + "</font>",0));

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