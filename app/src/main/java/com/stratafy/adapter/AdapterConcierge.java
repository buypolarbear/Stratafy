package com.stratafy.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.stratafy.R;
import com.stratafy.fragment.FragmentConcierge;
import com.stratafy.model.Concierge;

import java.util.List;

/**
 * Created by cn on 10/17/2017.
 */

public class AdapterConcierge extends RecyclerView.Adapter<AdapterConcierge.MyViewHolder> {

    private List<Concierge> mList;
    private Context context;
    private final LayoutInflater inflater;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    public AdapterConcierge(Context context, List<Concierge> mList){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.mList = mList;

        mPref = context.getSharedPreferences("service", Context.MODE_PRIVATE);
        mEditor = mPref.edit();
    }

    @Override
    public AdapterConcierge.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_concirge, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final AdapterConcierge.MyViewHolder holder, int position) {
        final Concierge concierge = mList.get(position);
        holder.txtTitle.setText(concierge.getTitle());
        Glide.with(context).load(concierge.getImage()).into(holder.img);

        if(mList.get(position).isSelected()){
            holder.llImg.setBackground(ContextCompat.getDrawable(context, R.drawable.border_radius_concierge));
        }else {
            holder.llImg.setBackground(null);
        }

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPref.contains("position")){
                    mList.get(mPref.getInt("position", 0)).setSelected(false);
                }
                mEditor.putInt("position", holder.getAdapterPosition());
                mEditor.commit();
                setSelected(holder.getAdapterPosition());
                FragmentConcierge.llFloat.setVisibility(View.VISIBLE);
                FragmentConcierge.number = concierge.getNumber();
                holder.llImg.setBackground(ContextCompat.getDrawable(context, R.drawable.border_radius_concierge));
            }
        });
    }

    public void setSelected(int pos) {
        try {
            mList.get(pos).setSelected(true);
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView txtTitle;
        private RelativeLayout llImg;
        public MyViewHolder(View itemView) {
            super(itemView);
            img = (ImageView)itemView.findViewById(R.id.img);
            txtTitle = (TextView)itemView.findViewById(R.id.txtTitle);
            llImg = (RelativeLayout)itemView.findViewById(R.id.llImag);
        }
    }
}
