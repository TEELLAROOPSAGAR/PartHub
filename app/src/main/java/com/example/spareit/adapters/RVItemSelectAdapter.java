package com.example.spareit.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spareit.R;
import com.example.spareit.data.AppDatabase;
import com.example.spareit.data.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RVItemSelectAdapter extends RecyclerView.Adapter<RVItemSelectAdapter.ViewHolder> {

    public List<Items> items;
    public ArrayList<Integer> counts;
    Context context;
    RVItemAdapter.Listener listener;
    public long Total=0;

    public RVItemSelectAdapter(Context context) {
        items = AppDatabase.getDbInstance(context.getApplicationContext()).userDao().getItems();
        counts=new ArrayList<Integer>(Collections.nCopies(items.size(), 0));
        this.context = context;
    }

    @NonNull
    @Override
    public RVItemSelectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_basket, parent, false);
        return new RVItemSelectAdapter.ViewHolder(cv);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView cv) {
            super(cv);
            cardView = cv;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RVItemSelectAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CardView cv = holder.cardView;
        TextView itemName = cv.findViewById(R.id.item_name);
        TextView price = cv.findViewById(R.id.price);
        TextView itemCount = cv.findViewById(R.id.itemCount);
        Button add=cv.findViewById(R.id.incr);
        Button dec=cv.findViewById(R.id.decr);

        itemName.setText(items.get(position).name);
        price.setText("Rs."+String.valueOf(items.get(position).salePrice));


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count=Integer.parseInt(itemCount.getText().toString().trim());

                if(count<items.get(position).count){
                    count++;
                    Total+=items.get(position).salePrice;
                    counts.set(position,count);
                    listener.onClick(position);
                }else{
                    count=items.get(position).count;
                    counts.set(position,count);
                    listener.onClick(-1);
                }
                itemCount.setText(String.valueOf(count));

            }
        });

        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count=Integer.parseInt(itemCount.getText().toString().trim());

                if(count>0){
                    count--;
                    Total-=items.get(position).salePrice;
                    listener.onClick(position);
                    counts.set(position,count);
                }else{
                    count=0;
                    counts.set(position,count);
                }
                itemCount.setText(String.valueOf(count));
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void dataSetChanged(){
        items = AppDatabase.getDbInstance(context.getApplicationContext()).userDao().getItems();
        notifyDataSetChanged();
    }

    public static interface Listener{
        void onClick(int position);
    }

    public void setListener(RVItemAdapter.Listener listener ){
        this.listener=listener;
    }

}