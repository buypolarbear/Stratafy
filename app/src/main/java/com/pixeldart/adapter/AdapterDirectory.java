package com.pixeldart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pixeldart.R;
import com.pixeldart.helper.Glob;
import com.pixeldart.model.Contact;

import java.util.List;

/**
 * Created by cn on 8/29/2017.
 */

public class AdapterDirectory extends RecyclerView.Adapter<AdapterDirectory.MyViewHolder> {

    List<Contact> mList;
    Context context;
    private final LayoutInflater inflater;

    public AdapterDirectory(Context context, List<Contact> mList) {
        inflater = LayoutInflater.from(context);
        this.mList = mList;
        this.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.section_item_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Contact contact = mList.get(position);

        if (contact.getLname().equals("null") || contact.getLname().isEmpty()) {
            holder.txtName.setText(contact.getFname());
            char fname = contact.getFname().toUpperCase().charAt(0);
            holder.txtNameLetter.setText(String.valueOf(fname));
        } else {
            holder.txtName.setText(contact.getFname() + " " + contact.getLname());
            char fname = contact.getFname().toUpperCase().charAt(0);
            char lname = contact.getLname().toUpperCase().charAt(0);
            holder.txtNameLetter.setText(String.valueOf(fname + "" + lname));
        }
        holder.txtPosition.setText(contact.getPosition());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNameLetter, txtName, txtPosition;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtNameLetter = (TextView) itemView.findViewById(R.id.txtNameLetter);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtPosition = (TextView) itemView.findViewById(R.id.txtPosition);
            txtNameLetter.setTypeface(Glob.avenir(context));
            txtName.setTypeface(Glob.avenir(context));
            txtPosition.setTypeface(Glob.avenir(context));
        }
    }
}