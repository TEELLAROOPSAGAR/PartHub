package com.example.spareit.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spareit.ItemSelectActivity;
import com.example.spareit.R;
import com.example.spareit.data.AppDatabase;
import com.example.spareit.data.Items;

import java.util.List;

public class RVItemAdapter extends RecyclerView.Adapter<RVItemAdapter.ViewHolder> {

    public List<Items> items;
    Context context;
    Boolean forClick;//true for item, false for status
    Listener listener;
    AppDatabase db;

    public RVItemAdapter(Context context,Boolean forClick) {
        db=AppDatabase.getDbInstance(context.getApplicationContext());
        items = db.userDao().getItems();
        this.context = context;
        this.forClick=forClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CardView cv = holder.cardView;
        TextView itemName = cv.findViewById(R.id.item_name);
        TextView count = cv.findViewById(R.id.item_count);
        TextView vendorName = cv.findViewById(R.id.item_vendor);

        itemName.setText(items.get(position).name);
        count.setText("Count : "+String.valueOf(items.get(position).count));
        vendorName.setText("Vendor : "+items.get(position).vendorName);

        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(forClick){
                    if(listener!=null)
                        listener.onClick(position);
                }else{
                    //Make a Phonecall for ordering items
                    if(listener!=null)
                        listener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView cv) {
            super(cv);
            cardView = cv;
        }
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void onlyThreshold() {
        items = AppDatabase.getDbInstance(context.getApplicationContext()).userDao().getItems();
        items.removeIf(s -> s.count >= s.threshold);
        notifyDataSetChanged();
    }

    public void dataSetChanged(){
        items = AppDatabase.getDbInstance(context.getApplicationContext()).userDao().getItems();
        notifyDataSetChanged();
    }

    public static interface Listener{
        void onClick(int position);
    }

    public void setListener(Listener listener ){
        this.listener=listener;
    }

}