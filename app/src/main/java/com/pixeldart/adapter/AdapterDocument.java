package com.pixeldart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pixeldart.R;
import com.pixeldart.model.Document;

import java.util.List;

/**
 * Created by cn on 8/21/2017.
 */

public class AdapterDocument extends RecyclerView.Adapter<AdapterDocument.MyViewHolder> {
    private Context context;
    private List<Document> mDocumentList;
    private final LayoutInflater inflater;

    public AdapterDocument(Context context, List<Document> mDocumentList){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.mDocumentList = mDocumentList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_laws, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Document document = mDocumentList.get(position);

    }

    @Override
    public int getItemCount() {
        return mDocumentList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
