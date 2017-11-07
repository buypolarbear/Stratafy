package com.stratafy.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.stratafy.R;
import com.stratafy.fragment.DialogPlace;
import com.stratafy.model.AroundPlaces;

import java.util.List;

/**
 * Created by cn on 10/11/2017.
 */

public class AdapterAround extends RecyclerView.Adapter<AdapterAround.MyViewHolder> {
    private Context context;
    private List<AroundPlaces> mLawsList;
    private final LayoutInflater inflater;

    public AdapterAround(Context context, List<AroundPlaces> mLawsList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.mLawsList = mLawsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_around, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final AroundPlaces places = mLawsList.get(position);
        holder.txtPlaceName.setText(places.getPlacename());

        if (places.getTime() != null && !places.getTime().isEmpty()) {
            holder.txtDistance.setText(places.getDistance() + " | " + places.getTime());
        } else {
            holder.txtDistance.setText(places.getDistance());
        }

        Log.d("Image_url", String.valueOf(places.getImage()));
        if (places.getImage() != null && !places.getImage().isEmpty()) {
            Glide.with(context).load(places.getImage()).into(holder.imgPlace);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("address", places.getAddress());
                bundle.putString("title", places.getPlacename());
                bundle.putString("KM", places.getDistance());
                bundle.putString("Category", places.getCategory());
                bundle.putString("phone", places.getPhone());
                bundle.putString("website", places.getWebsite());
                bundle.putString("lat", places.getLatitude());
                bundle.putString("lng", places.getLongitude());
                bundle.putStringArrayList("opening_hours", places.getOpeningHour());
                FragmentTransaction ft = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                DialogPlace newFragment = DialogPlace.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLawsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtPlaceName, txtDistance;
        ImageView imgPlace;

        public MyViewHolder(View itemView) {
            super(itemView);

            txtPlaceName = (TextView) itemView.findViewById(R.id.txtPlacename);
            txtDistance = (TextView) itemView.findViewById(R.id.txtdistance);
            imgPlace = (ImageView) itemView.findViewById(R.id.imgPlace);

        }
    }
}

