package com.stratafy.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.stratafy.R;
import com.stratafy.helper.Glob;
import com.stratafy.model.AroundPlaces;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cn on 10/12/2017.
 */

public class FragmentAroundMap extends Fragment implements OnMapReadyCallback {

    private static final String EXTRA_TEXT = "list";

    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFrag;

    private LatLng latLng;
    private List<AroundPlaces> mAroundList;
    private LinearLayout llPlace;
    private TextView txtPlaceName, txtDistance;
    private String time = "";
    private static View view;

    public FragmentAroundMap() {
    }

    public static FragmentAroundMap newInstance(List<AroundPlaces> mList) {
        FragmentAroundMap fragment = new FragmentAroundMap();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_TEXT, (ArrayList<? extends Parcelable>) mList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_around_map, container, false);
            mapFrag = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
        } catch (InflateException e) {
        }

        initialization(view);
        return view;
    }

    private void initialization(View view) {
        llPlace = (LinearLayout)view.findViewById(R.id.llPlace);

        txtPlaceName = (TextView)view.findViewById(R.id.txtPlacename);
        txtDistance = (TextView)view.findViewById(R.id.txtdistance);

        txtPlaceName.setTypeface(Glob.avenir(getActivity()));
        txtDistance.setTypeface(Glob.avenir(getActivity()));
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mAroundList = new ArrayList<>();
        mAroundList = getArguments().getParcelableArrayList("list");

        for(int i = 0; i < mAroundList.size(); i++){
            AroundPlaces places = mAroundList.get(i);
            if(places.getTime() != null && !places.getTime().isEmpty()){
                time =  " | " + places.getTime();
            }

            if(mGoogleMap != null){
                if(i == 0){
                    latLng = new LatLng(Double.parseDouble(places.getLatitude()), Double.parseDouble(places.getLongitude()));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                }


                mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(places.getLatitude()), Double.parseDouble(places.getLongitude())))
                        .snippet(places.getDistance() + time)
                        .title(places.getPlacename())
                        .icon(getBitmapDescriptor(getActivity(), R.drawable.ico_map_marker)));
            }
        }

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                llPlace.setVisibility(View.VISIBLE);
                txtPlaceName.setText(marker.getTitle());
                txtDistance.setText(marker.getSnippet());
                return true;
            }
        });
    }

    private BitmapDescriptor getBitmapDescriptor(Context context, int id) {
        Drawable vd = ContextCompat.getDrawable(context, id);
        vd.setBounds(0, 0, vd.getIntrinsicWidth(), vd.getIntrinsicHeight());
        Bitmap bm = Bitmap.createBitmap(vd.getIntrinsicWidth(), vd.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vd.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }
}
