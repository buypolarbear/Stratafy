package com.stratafy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stratafy.R;
import com.stratafy.model.Category;

import java.util.List;

/**
 * Created by cn on 8/28/2017.
 */

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.MyViewHolder> {
    private Context context;
    private List<Category> mList;
    private final LayoutInflater inflater;
    private int row_index;

    public AdapterCategory(Context context, List<Category> mList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_category, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Category logs = mList.get(position);
        holder.txtText.setText(logs.getCat());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index=position;
                notifyDataSetChanged();

            }
        });

        if(row_index==position){
            holder.txtText.setTextColor(context.getResources().getColor(R.color.blue));
            holder.ll.setBackground(context.getResources().getDrawable(R.drawable.border_radius_solid_white));
        }
        else
        {
            holder.ll.setBackground(context.getResources().getDrawable(R.drawable.border_radius));
            holder.txtText.setTextColor(context.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtText;
        LinearLayout ll;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtText = (TextView)itemView.findViewById(R.id.txtText);
            ll = (LinearLayout)itemView.findViewById(R.id.ll);
        }
    }
}

