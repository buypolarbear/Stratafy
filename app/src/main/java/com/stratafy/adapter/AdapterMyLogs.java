package com.stratafy.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stratafy.R;

import com.stratafy.fragment.FragmentApprove;
import com.stratafy.helper.Glob;
import com.stratafy.model.MyLogs;

import java.util.List;

/**
 * Created by cn on 8/25/2017.
 */

public class AdapterMyLogs extends RecyclerView.Adapter<AdapterMyLogs.MyViewHolder> {
    private Context context;
    private List<MyLogs> myLogsList;
    private final LayoutInflater inflater;

    public AdapterMyLogs(Context context, List<MyLogs> mLogList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.myLogsList = mLogList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_log, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final MyLogs logs = myLogsList.get(position);
        holder.txtDate.setText(logs.getCreated());
        holder.txtCatName.setText(logs.getTitle());
        holder.txtCatType.setText(logs.getLogCategoryName());
        if(!logs.getStatus().equals("null") && !logs.getStatus().isEmpty()){
            holder.txtApprove.setText(logs.getStatus());
        }else {
            holder.txtApprove.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentApprove fragment = FragmentApprove.instance(logs.getTitle(), logs.getDescription(),
                        logs.getLogCategoryName(), logs.getAttachments());
                FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment, "APPROVE");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return myLogsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtDate, txtCatName, txtCatType, txtApprove;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtDate = (TextView)itemView.findViewById(R.id.txtDate);
            txtCatName = (TextView)itemView.findViewById(R.id.txtCatName);
            txtCatType = (TextView)itemView.findViewById(R.id.txtCatType);
            txtApprove = (TextView)itemView.findViewById(R.id.txtApprove);

            txtDate.setTypeface(Glob.avenir(context));
            txtCatName.setTypeface(Glob.avenir(context));
            txtCatType.setTypeface(Glob.avenir(context));
            txtApprove.setTypeface(Glob.avenir(context));

        }
    }
}

