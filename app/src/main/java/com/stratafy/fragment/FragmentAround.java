package com.stratafy.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import com.stratafy.activities.MainActivity;
import com.stratafy.adapter.AdapterAround;
import com.stratafy.helper.Glob;
import com.stratafy.helper.MyApplication;
import com.stratafy.model.AroundPlaces;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by cn on 8/14/2017.
 */

public class FragmentAround extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String EXTRA_TEXT = "text";
    private LinearLayout ll1, ll2, llAroundList;
    RelativeLayout llAroundMap;
    private ProgressBar mProgressBar;
    private List<AroundPlaces> mAroundList = new ArrayList<>();
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private LocationManager manager;
    boolean openLocationSetting;
    Location mLastLocation;
    boolean isOnce = true;

    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFrag;
    private LatLng latLng;
    private LinearLayout llPlace;
    private TextView txtPlaceName, txtDistance;
    Button txtList, txtMap;
    private String time = "";

    private RecyclerView recyclerAround;
    private RecyclerView.LayoutManager mLayoutManager;
    private AdapterAround adapterAround;
    ArrayList<String> hourList;
    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();
    int count;

    public static FragmentAround instance(String text) {
        FragmentAround fragment = new FragmentAround();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_around, container, false);
        initialization(view);
        MainActivity.imgStreaming.setVisibility(View.GONE);

        mapFrag = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showSettingsAlert();
            } else {
                buildGoogleApiClient();
            }
        } else {
            checkLocationPermission();
        }

        txtList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll1.setVisibility(View.VISIBLE);
                ll2.setVisibility(View.GONE);
                llAroundList.setVisibility(View.VISIBLE);
                llAroundMap.setVisibility(View.GONE);
            }
        });

        txtMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll1.setVisibility(View.GONE);
                ll2.setVisibility(View.VISIBLE);
                llAroundList.setVisibility(View.GONE);
                llAroundMap.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    private void initialization(View view) {

        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressbar);

        ll1 = (LinearLayout) view.findViewById(R.id.ll1);
        ll2 = (LinearLayout) view.findViewById(R.id.ll2);
        llAroundList = (LinearLayout) view.findViewById(R.id.llAroundList);
        llAroundMap = (RelativeLayout) view.findViewById(R.id.llAroundMap);

        llPlace = (LinearLayout)view.findViewById(R.id.llPlace);

        txtPlaceName = (TextView)view.findViewById(R.id.txtPlacename);
        txtDistance = (TextView)view.findViewById(R.id.txtdistance);

        txtList = (Button) view.findViewById(R.id.txtList);
        txtMap = (Button)view.findViewById(R.id.txtMap);


        MainActivity.txtToolbarTitle.setVisibility(View.VISIBLE);
        MainActivity.txtToolbarTitle.setText("Around me");

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerAround = (RecyclerView)view.findViewById(R.id.recyclerAround);
        recyclerAround.setLayoutManager(mLayoutManager);
        adapterAround = new AdapterAround(getActivity(), mAroundList);
        recyclerAround.setAdapter(adapterAround);

        llPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AroundPlaces places = mAroundList.get(count);
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
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                DialogPlace newFragment = DialogPlace.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        if (location != null) {

            if (isOnce) {
                isOnce = false;
                getPlaces();
            }
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("RequestCode:", String.valueOf(requestCode));
        if (ActivityCompat.checkSelfPermission(getActivity(), permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_LOCATION:
                    manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        showSettingsAlert();
                    } else {
                        buildGoogleApiClient();
                    }
                    break;
            }
            Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (openLocationSetting) {
            Log.d("MArker enable:", "YES");
            if (mGoogleApiClient == null) {
                buildGoogleApiClient();
            }
        }
    }

    public void showSettingsAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getResources().getString(R.string.alert_gps_title));
        alertDialog.setMessage(getResources().getString(R.string.alert_gps_not_enable));
        alertDialog.setPositiveButton(getResources().getString(R.string.alert_gps_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                openLocationSetting = true;
            }
        });
        alertDialog.setNegativeButton(getResources().getString(R.string.alert_gps_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void getPlaces() {
        mProgressBar.setVisibility(View.VISIBLE);
        String url = Glob.API_GOOGLE_PLACES + MainActivity.property_id + ".json?lat=" + mLastLocation.getLatitude() + "&lng=" + mLastLocation.getLongitude();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                longLog(response, "Places_response");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int status = jObj.getInt("status");
                    final String errorMsg = jObj.getString("errorMsg");
                    if (status != 0) {
                        mProgressBar.setVisibility(View.GONE);
                        JSONArray data = jObj.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            AroundPlaces places = new AroundPlaces();
                            places.setId(obj.getString("id"));
                            places.setPlacename(obj.getString("name"));
                            places.setCategory(obj.getString("category"));
                            places.setOffer(obj.getString("offer"));
                            places.setTerms_condition(obj.getString("terms_conditions"));
                            places.setAddress(obj.getString("address"));
                            places.setLatitude(obj.getString("lat"));
                            places.setLongitude(obj.getString("lng"));
                            places.setDistance(obj.getString("distance"));
                            places.setPhone(obj.getString("phone"));
                            places.setImage(obj.getString("image"));
                            places.setTime(obj.getString("time"));
                            places.setWebsite(obj.getString("website"));

                            JSONArray hours = obj.getJSONArray("opening_hours");
                            if(hours != null){
                                hourList = new ArrayList<>();
                                for (int j=0; j < hours.length(); j++){
                                    hourList.add(hours.get(j).toString());
                                }
                                places.setOpeningHour(hourList);
                            }
                            mAroundList.add(places);
                        }
                        adapterAround.notifyDataSetChanged();
                        mapFrag.getMapAsync(FragmentAround.this);
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                Log.e("TAG", "Law_error: " + error.getMessage());
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    public static void longLog(String str, String log) {
        if (str.length() > 4000) {
            Log.d(log, str.substring(0, 4000));
            longLog(str.substring(4000), log);
        } else
            Log.d(log, str);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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


                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(places.getLatitude()), Double.parseDouble(places.getLongitude())))
                        .snippet(places.getDistance() + time)
                        .title(places.getPlacename())
                        .icon(getBitmapDescriptor(getActivity(), R.drawable.ico_map_marker)));
                mHashMap.put(marker, i);
            }
        }

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                llPlace.setVisibility(View.VISIBLE);
                txtPlaceName.setText(marker.getTitle());
                txtDistance.setText(marker.getSnippet());
                count = mHashMap.get(marker);
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
