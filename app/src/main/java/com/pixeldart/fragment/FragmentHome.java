package com.pixeldart.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.pixeldart.R;
import com.pixeldart.helper.Glob;
import com.pixeldart.helper.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cn on 8/14/2017.
 */

public class FragmentHome extends Fragment{

    private static final String EXTRA_TEXT = "text";

    private RelativeLayout cutLayout;
    private TextView txtStreet, txtAddress, txtLink, txtQuickLinks, txtLog, txtGetInTouch, txtByLaws;
    private ProgressDialog mProgressDialog;
    private ProgressBar mProgressBar;

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
        mProgressDialog = new ProgressDialog(getActivity());
        initialization(view);
        GetBuilding(getActivity());
        return view;
    }

    private void initialization(View view) {
        cutLayout = (RelativeLayout)view.findViewById(R.id.background);

        txtStreet = (TextView)view.findViewById(R.id.txtStreet);
        txtAddress = (TextView)view.findViewById(R.id.txtAddress);
        txtLink = (TextView)view.findViewById(R.id.txtLink);
        txtQuickLinks = (TextView)view.findViewById(R.id.txtQuickLinks);
        txtLog = (TextView)view.findViewById(R.id.txtLog);
        txtGetInTouch = (TextView)view.findViewById(R.id.txtGetInTouch);
        txtByLaws = (TextView)view.findViewById(R.id.txtViewLaw);

        txtStreet.setTypeface(Glob.avenir(getActivity()));
        txtAddress.setTypeface(Glob.avenir(getActivity()));
        txtLink.setTypeface(Glob.avenir(getActivity()));
        txtQuickLinks.setTypeface(Glob.avenir(getActivity()));
        txtLog.setTypeface(Glob.avenir(getActivity()));
        txtGetInTouch.setTypeface(Glob.avenir(getActivity()));
        txtByLaws.setTypeface(Glob.avenir(getActivity()));

        mProgressBar = (ProgressBar)view.findViewById(R.id.mProgressbar);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String text = getArguments().getString(EXTRA_TEXT);
      //  Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void GetBuilding(final Context context) {

        String tag_string_req = "req_login";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Glob.API_BUILDING_MANAGE, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("TAG", "onResponse = \n " + response.toString());
                try {
                    int status = response.getInt("status");
                    String errorMsg = response.getString("errorMsg");
                    if(status == 1){
                        JSONObject data = response.getJSONObject("data");
                        int id = data.getInt("id");
                        txtStreet.setText(data.getString("property_name"));
                        txtAddress.setText(data.getString("property_address"));
                        txtLink.setText(data.getString("url"));

                        LayerDrawable ld = (LayerDrawable) getResources().getDrawable(R.drawable.background);
                        Drawable replace = LoadImageFromWebOperations(data.getString("picture"));
                        ld.setDrawableByLayerId(R.id.first_img, replace);
                        if(replace != null){
                            cutLayout.setBackground(ld);
                        }
                    }else {
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
                dismissDialog();
            }
        });
        MyApplication.getInstance().addToRequestQueue(request, tag_string_req);
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "drawable");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}
