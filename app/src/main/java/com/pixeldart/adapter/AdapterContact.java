package com.pixeldart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pixeldart.R;
import com.pixeldart.helper.Glob;
import com.pixeldart.model.Contact;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by cn on 8/28/2017.
 */

public class AdapterContact extends StatelessSection {
    String letter;
    List<Contact> mList;
    Context context;


    public AdapterContact(Context context, String date, List<Contact> mList) {
        super(new SectionParameters.Builder(R.layout.section_item_contact)
                .headerResourceId(R.layout.section_header2)
                .build());
        this.letter = date;
        this.mList = mList;
        this.context = context;
    }

    @Override
    public int getContentItemsTotal() {
        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new MyItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyItemViewHolder itemHolder = (MyItemViewHolder) holder;
        final Contact files = mList.get(position);
        if(files.getLname().equals("null") || files.getLname().isEmpty()){
            itemHolder.txtName.setText(files.getFname());
            char fname = files.getFname().toUpperCase().charAt(0);
            itemHolder.txtNameLetter.setText(String.valueOf(fname));
        }else {
            itemHolder.txtName.setText(files.getFname() + " " + files.getLname());
            char fname = files.getFname().toUpperCase().charAt(0);
            char lname = files.getLname().toUpperCase().charAt(0);
            itemHolder.txtNameLetter.setText(String.valueOf(fname + "" + lname));
        }

        itemHolder.txtPosition.setText(files.getPosition());

        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new MyHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        MyHeaderViewHolder headerHolder = (MyHeaderViewHolder) holder;
        headerHolder.txtLetter.setText(letter);
    }

    class MyItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtNameLetter, txtName, txtPosition;
        public MyItemViewHolder(View itemView) {
            super(itemView);
            txtNameLetter = (TextView) itemView.findViewById(R.id.txtNameLetter);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtPosition = (TextView) itemView.findViewById(R.id.txtPosition);
            txtNameLetter.setTypeface(Glob.avenir(context));
            txtName.setTypeface(Glob.avenir(context));
            txtPosition.setTypeface(Glob.avenir(context));

        }
    }

    class MyHeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtLetter;

        public MyHeaderViewHolder(View itemView) {
            super(itemView);
            txtLetter = (TextView) itemView.findViewById(R.id.txtLetter);
            txtLetter.setTypeface(Glob.avenir(context));
        }
    }
}
