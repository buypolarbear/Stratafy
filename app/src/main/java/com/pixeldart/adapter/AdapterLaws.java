package com.pixeldart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pixeldart.R;
import com.pixeldart.helper.Glob;
import com.pixeldart.model.Laws;

import java.util.List;

/**
 * Created by cn on 8/21/2017.
 */

public class AdapterLaws extends RecyclerView.Adapter<AdapterLaws.MyViewHolder> {
    private Context context;
    private List<Laws> mLawsList;
    private final LayoutInflater inflater;

    public AdapterLaws(Context context, List<Laws> mLawsList){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.mLawsList = mLawsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_laws, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Laws laws = mLawsList.get(position);
        holder.txtLawName.setText(laws.getLaw_name());
        holder.txtLawText.setText(laws.getLaw_text());
    }

    @Override
    public int getItemCount() {
        return mLawsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtLawName, txtLawText, txtAgreed, txtDisagreed, txtComment;
        public MyViewHolder(View itemView) {
            super(itemView);

            txtLawName = (TextView)itemView.findViewById(R.id.txtLawName);
            txtLawText = (TextView)itemView.findViewById(R.id.txtLawText);
            txtAgreed = (TextView)itemView.findViewById(R.id.txtAgreed);
            txtDisagreed = (TextView)itemView.findViewById(R.id.txtDisagreed);
            txtComment = (TextView)itemView.findViewById(R.id.txtComment);

            txtLawName.setTypeface(Glob.avenir(context));
            txtLawText.setTypeface(Glob.avenir(context));
            txtAgreed.setTypeface(Glob.avenir(context));
            txtDisagreed.setTypeface(Glob.avenir(context));
            txtComment.setTypeface(Glob.avenir(context));
        }
    }
}
