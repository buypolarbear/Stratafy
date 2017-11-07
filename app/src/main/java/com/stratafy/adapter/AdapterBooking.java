package com.stratafy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stratafy.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by cn on 10/27/2017.
 */

public class AdapterBooking extends RecyclerView.Adapter<AdapterBooking.MyViewHolder> {
    private final LayoutInflater inflater;
 //   List<MyData> myData = Collections.EMPTY_LIST;
    Context context;

    public AdapterBooking(Context context) {
        inflater = LayoutInflater.from(context);
    //    this.myData = myData;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}