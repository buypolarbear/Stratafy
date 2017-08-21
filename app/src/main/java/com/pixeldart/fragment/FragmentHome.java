package com.pixeldart.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pixeldart.R;
import com.pixeldart.activities.CreateLogActivity;
import com.pixeldart.activities.MainActivity;
import com.pixeldart.helper.Glob;
import com.pixeldart.helper.MyApplication;
import com.pixeldart.service.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Created by cn on 8/14/2017.
 */

public class FragmentHome extends Fragment implements View.OnClickListener {

    private static final String EXTRA_TEXT = "text";

    private RelativeLayout cutLayout;
    private TextView txtStreet, txtAddress, txtLink, txtQuickLinks, txtLog, txtGetInTouch, txtByLaws;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar mProgressBar;
    private String property_id, description, address;
    private FloatingActionButton btnInfo, btnShare, btnMap;
    private double latitude, longitude;
    private Intent intent;


    public static FragmentHome instance(String text) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        MainActivity.txtToolbarTitle.setVisibility(View.GONE);
        pref = getActivity().getSharedPreferences(Config.SHARED_PREF, 0);
        editor = pref.edit();
        property_id = pref.getString("property_id", null);

        initialization(view);

        return view;
    }

    private void initialization(View view) {
        cutLayout = (RelativeLayout) view.findViewById(R.id.llLayerList);
        txtStreet = (TextView) view.findViewById(R.id.txtStreet);
        txtAddress = (TextView) view.findViewById(R.id.txtAddress);
        txtLink = (TextView) view.findViewById(R.id.txtLink);
        txtQuickLinks = (TextView) view.findViewById(R.id.txtQuickLinks);
        txtLog = (TextView) view.findViewById(R.id.txtLog);
        txtGetInTouch = (TextView) view.findViewById(R.id.txtGetInTouch);
        txtByLaws = (TextView) view.findViewById(R.id.txtViewLaw);

        txtStreet.setTypeface(Glob.avenir(getActivity()));
        txtAddress.setTypeface(Glob.avenir(getActivity()));
        txtLink.setTypeface(Glob.avenir(getActivity()));
        txtQuickLinks.setTypeface(Glob.avenir(getActivity()));
        txtLog.setTypeface(Glob.avenir(getActivity()));
        txtGetInTouch.setTypeface(Glob.avenir(getActivity()));
        txtByLaws.setTypeface(Glob.avenir(getActivity()));
        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressbar);

        btnInfo = (FloatingActionButton)view.findViewById(R.id.btnInfo);
        btnShare = (FloatingActionButton)view.findViewById(R.id.btnShare);
        btnMap = (FloatingActionButton)view.findViewById(R.id.btnMap);

        btnInfo.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnMap.setOnClickListener(this);
        txtLog.setOnClickListener(this);
        txtGetInTouch.setOnClickListener(this);
        txtByLaws.setOnClickListener(this);

        GetBuilding(getActivity());
    }


    private void GetBuilding(final Context context) {

        String tag_string_req = "req_login";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Glob.API_BUILDING_MANAGE + property_id + ".json", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("TAG", "onResponse = \n " + response.toString());
                try {
                    int status = response.getInt("status");
                    String errorMsg = response.getString("errorMsg");
                    if (status == 1) {
                        JSONObject data = response.getJSONObject("data");
                        int id = data.getInt("id");
                        txtStreet.setText(data.getString("property_name"));
                        txtAddress.setText(data.getString("property_address"));
                        address = data.getString("property_address");
                        txtLink.setText(data.getString("url"));
                        description = data.getString("description");
                        latitude = data.getDouble("lat");
                        longitude = data.getDouble("lng");
                        drawableFromUrl(data.getString("picture"));
                    } else {
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mProgressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "response error \n" + error);
                mProgressBar.setVisibility(View.GONE);
            }
        });
        MyApplication.getInstance().addToRequestQueue(request, tag_string_req);
    }

    public void drawableFromUrl(final String url) {
        final Bitmap[] x = new Bitmap[1];
        new AsyncTask<Void, Void, Object[]>() {

            @Override
            protected Object[] doInBackground(Void... params) {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    x[0] = BitmapFactory.decodeStream(input);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return new Object[0];
            }

            @Override
            protected void onPostExecute(Object[] objects) {
                super.onPostExecute(objects);

                LayerDrawable layerDrawable = (LayerDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.background_home, null);
                layerDrawable.setDrawableByLayerId(R.id.first_img1, new BitmapDrawable(getActivity().getResources(), x[0]));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    layerDrawable.setDrawable(1, new BitmapDrawable(getActivity().getResources(), x[0]));

                }
                cutLayout.setBackground(layerDrawable);
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnInfo:
                openDialog();
                break;
            case R.id.btnShare:
                ShareAddress();
                break;
            case R.id.btnMap:
                openMap();
                break;
            case R.id.txtLog:
                intent = new Intent(getActivity(), CreateLogActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
            case R.id.txtGetInTouch:
                intent = new Intent(getActivity(), CreateLogActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
            case R.id.txtViewLaw:
                intent = new Intent(getActivity(), CreateLogActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
        }
    }

    public void openDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_info);
        TextView txtInfo = (TextView) dialog.findViewById(R.id.txtInfo);
        txtInfo.setTypeface(Glob.avenir(getActivity()));
        txtInfo.setText(description);
        dialog.show();
    }

    private void ShareAddress() {
        String chooserTitle = "Select Sharing Media";
        ShareCompat.IntentBuilder.from(getActivity())
                .setType("text/plain")
                .setText(address)
                //.setHtmlText(body) //If you are using HTML in your body text
                .setChooserTitle(chooserTitle)
                .startChooser();

    }

    private void openMap(){
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }
}
