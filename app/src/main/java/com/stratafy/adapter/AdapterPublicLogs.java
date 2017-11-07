package com.stratafy.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stratafy.R;
import com.stratafy.fragment.FragmentApprove;
import com.stratafy.model.PublicLogs;

import java.util.List;

/**
 * Created by cn on 8/25/2017.
 */

public class AdapterPublicLogs extends RecyclerView.Adapter<AdapterPublicLogs.MyViewHolder> {
    private Context context;
    private List<PublicLogs> myLogsList;
    private final LayoutInflater inflater;

    public AdapterPublicLogs(Context context, List<PublicLogs> mLogList) {
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
        final PublicLogs logs = myLogsList.get(position);
        holder.txtDate.setText(logs.getCreated());
        holder.txtCatName.setText(logs.getTitle());
        holder.txtCatType.setText(logs.getLogCategoryName());

        holder.setIsRecyclable(false);

        if(!logs.getStatus().equals("null") && !logs.getStatus().isEmpty()){
            if(logs.getStatus().substring(0, 1).equals("R")){
                holder.txtApprove.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                holder.txtApprove.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dot_pink, 0, 0, 0);
                holder.txtApprove.setText(logs.getStatus());
            }else {
                holder.txtApprove.setTextColor(ContextCompat.getColor(context, R.color.green));
                holder.txtApprove.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dot, 0, 0, 0);
                holder.txtApprove.setText(logs.getStatus());
            }
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

        }
    }

}

