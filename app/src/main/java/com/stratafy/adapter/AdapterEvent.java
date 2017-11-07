package com.stratafy.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stratafy.R;
import com.stratafy.helper.CustomDialogEvent;
import com.stratafy.model.Events;

import java.util.List;

/**
 * Created by cn on 8/31/2017.
 */

public class AdapterEvent extends RecyclerView.Adapter<AdapterEvent.MyViewHolder> {
    private Context context;
    private List<Events> mList;
    private final LayoutInflater inflater;

    public AdapterEvent(Context context, List<Events> mList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_event, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Events events = mList.get(position);
        holder.txtTitle.setText(events.getTitle());
        holder.txtSTime.setText(events.getsTime() + " -");
        holder.txtETime.setText(events.geteTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialogEvent cde=new CustomDialogEvent((Activity)context, events.getEvent_id(), events.getAvtar(),
                        events.getFname(), events.getLname(), events.getTitle(), events.getDetail(), events.getDate(),
                        events.getsTime(), events.geteTime());
                cde.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cde.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtSTime, txtETime;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView)itemView.findViewById(R.id.txtTitle);
            txtSTime = (TextView)itemView.findViewById(R.id.txtSTime);
            txtETime = (TextView)itemView.findViewById(R.id.txtETime);

        }
    }
}