package com.stratafy.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stratafy.R;
import com.stratafy.helper.Glob;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by cn on 10/13/2017.
 */

public class DialogPlace extends DialogFragment {

    @BindView(R.id.txtTitle) TextView txtTitle;
    @BindView(R.id.txtCategory) TextView txtCategory;
    @BindView(R.id.txtKM) TextView txtKM;
    @BindView(R.id.txtLocation) TextView txtLocation;
    @BindView(R.id.txtHours) TextView txtHours;

    @BindView(R.id.fab_phone) FloatingActionButton fab_phone;
    @BindView(R.id.fab_link) FloatingActionButton fab_link;
    @BindView(R.id.fab_navigate) FloatingActionButton fab_navigate;

    @BindView(R.id.llHours) LinearLayout llHours;


    private Unbinder unbinder;
    private ArrayList<String> hours = new ArrayList<>();
    private String link, phone, lat, lng;

    public static DialogPlace newInstance() {
        DialogPlace dialog = new DialogPlace();
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyMaterialThemeFull);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_place, container, false);
        unbinder = ButterKnife.bind(this, v);

        txtTitle.setText(getArguments().getString("title"));
        txtTitle.setPaintFlags(txtTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtCategory.setText(getArguments().getString("Category"));
        txtKM.setText(getArguments().getString("KM"));
        txtLocation.setText(getArguments().getString("address"));
        link = getArguments().getString("website");
        phone = getArguments().getString("phone");
        lat = getArguments().getString("lat");
        lng = getArguments().getString("lng");
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        hours = getArguments().getStringArrayList("opening_hours");
        Log.d("OpeningHours:", String.valueOf(hours.toString()));

        if(hours != null && hours.size() > 0){
            for (int i = 0; i < hours.size(); i++){
                final TextView tv = new TextView(getActivity());
                tv.setText(hours.get(i));
                tv.setTypeface(Glob.avenir(getActivity()));
                tv.setPadding(0,5,0,5);
                tv.setTextSize(16);
                tv.setTextColor(ContextCompat.getColor(getActivity(),R.color.black));
                llHours.addView(tv);
            }
        }else {
            txtHours.setVisibility(View.GONE);
        }

        fab_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phone != null && !phone.isEmpty())
                    dialContactPhone(phone);
            }
        });

        fab_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebsite();
            }
        });

        fab_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void openMap(){
        String uri = "http://maps.google.com/maps?daddr="+lat +","+lng;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    private void openWebsite(){
        if(link != null && !link.isEmpty()){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browserIntent);
        }
    }

    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }
}
