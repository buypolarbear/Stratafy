package com.stratafy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.stratafy.R;
import com.stratafy.model.Gallery;


/**
 * Created by cn on 8/26/2017.
 */

public class AdapterGallery extends ArrayAdapter<Gallery> {

    public AdapterGallery(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        ViewHolder holder;

        if (contentView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            contentView = inflater.inflate(R.layout.layout_gallery_item, parent, false);
            holder = new ViewHolder(contentView);
            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }

        Gallery spot = getItem(position);

      //  holder.name.setText(spot.name);
    //    holder.city.setText(spot.city);
        Glide.with(getContext()).load(spot.getUrl()).into(holder.image);

        return contentView;
    }

    private static class ViewHolder {
        public TextView name;
        public TextView city;
        public ImageView image;

        public ViewHolder(View view) {
            this.name = (TextView) view.findViewById(R.id.item_tourist_spot_card_name);
            this.city = (TextView) view.findViewById(R.id.item_tourist_spot_card_city);
            this.image = (ImageView) view.findViewById(R.id.item_tourist_spot_card_image);
        }
    }

}