package com.pixeldart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pixeldart.R;
import com.pixeldart.helper.Glob;
import com.pixeldart.model.Notifications;

import java.util.List;

/**
 * Created by cn on 8/28/2017.
 */

public class AdapterNotification extends RecyclerView.Adapter<AdapterNotification.MyViewHolder> {
    private Context context;
    private List<Notifications> mList;
    private final LayoutInflater inflater;

    public AdapterNotification(Context context, List<Notifications> mList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_notification, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Notifications logs = mList.get(position);
        holder.txtText.setText(logs.getMessage());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtText;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtText = (TextView)itemView.findViewById(R.id.txtText);
            txtText.setTypeface(Glob.avenir(context));


        }
    }
}
