package com.stratafy.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.stratafy.R;
import com.stratafy.fragment.FragmentPdf;
import com.stratafy.model.DocumentChild;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class MySection extends StatelessSection {

    String date;
    List<DocumentChild> documentFileList;
    Context context;

    public MySection(Context context, String date, List<DocumentChild> documentFileList) {
        // call constructor with layout resources for this Section header, footer and items 
        super(new SectionParameters.Builder(R.layout.section_item)
                .headerResourceId(R.layout.section_header)
                .build());
        this.date = date;
        this.documentFileList = documentFileList;
        this.context = context;
    }

    @Override
    public int getContentItemsTotal() {
        return documentFileList.size(); // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new MyItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyItemViewHolder itemHolder = (MyItemViewHolder) holder;
        final DocumentChild files = documentFileList.get(position);
        itemHolder.txtFile.setText(files.getDescription());
        itemHolder.txtFType.setText(files.getCategoryName());

        itemHolder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("file", files.getFile());
                FragmentTransaction ft = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                FragmentPdf newFragment = FragmentPdf.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
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

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date dates = format.parse(date);
            String dayOfTheWeek = (String) DateFormat.format("EEEE", dates);
            String day          = (String) DateFormat.format("dd",   dates);
            String monthString  = (String) DateFormat.format("MMM",  dates);
            String monthNumber  = (String) DateFormat.format("MM",   dates);
            String year         = (String) DateFormat.format("yyyy", dates);
            headerHolder.txtDate.setText(dayOfTheWeek.toUpperCase() + ", "+ monthString.toUpperCase() + " " + day);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    class MyItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtFile, txtFType;
        private final Button btnView;
        public MyItemViewHolder(View itemView) {
            super(itemView);
            txtFile = (TextView) itemView.findViewById(R.id.txtFname);
            txtFType = (TextView) itemView.findViewById(R.id.txtFType);
            btnView = (Button)itemView.findViewById(R.id.btnView);
        }
    }

    class MyHeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtDate;

        public MyHeaderViewHolder(View itemView) {
            super(itemView);
            txtDate = (TextView) itemView.findViewById(R.id.txtDate);
        }
    }
}