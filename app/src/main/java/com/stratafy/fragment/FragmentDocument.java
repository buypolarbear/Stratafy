package com.stratafy.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.stratafy.R;
import com.stratafy.activities.MainActivity;
import com.stratafy.adapter.MySection;
import com.stratafy.helper.Glob;
import com.stratafy.helper.MyApplication;
import com.stratafy.model.DocumentChild;
import com.stratafy.service.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * Created by cn on 8/14/2017.
 */

public class FragmentDocument extends Fragment {

    private static final String EXTRA_TEXT = "text";
    private RecyclerView recyclerDocumet;
    private RecyclerView.LayoutManager mLayoutManager;
    private SectionedRecyclerViewAdapter adapterDocument;
    private List<DocumentChild> mDocumentFiles;
    private ProgressBar mProgressBar;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String property_id;
    private int uid;
    private String key;

    public static FragmentDocument instance(String text) {
        FragmentDocument fragment = new FragmentDocument();
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
        View view = inflater.inflate(R.layout.fragment_document, container, false);
        MainActivity.txtToolbarTitle.setVisibility(View.VISIBLE);
        MainActivity.txtToolbarTitle.setText(getResources().getString(R.string.strataDoc));
        MainActivity.imgStreaming.setVisibility(View.GONE);
        initialization(view);
        return view;
    }

    private void initialization(View view) {

        pref = getActivity().getSharedPreferences(Config.SHARED_PREF, 0);
        editor = pref.edit();
        property_id = pref.getString("property_id", null);
        uid = pref.getInt("uid", 0);

        adapterDocument = new SectionedRecyclerViewAdapter();
        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressbar);
        recyclerDocumet = (RecyclerView)view.findViewById(R.id.recycleDocument);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerDocumet.setLayoutManager(mLayoutManager);


        getLaws();
    }

    private void getLaws() {
        mProgressBar.setVisibility(View.VISIBLE);
        String tag_string_req = "req_login";
        String url = Glob.API_GET_DOCUMENT + property_id + ".json";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                longLog(response, "Low_response");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int status = jObj.getInt("status");
                    final String errorMsg = jObj.getString("errorMsg");
                    if (status != 0) {
                        mProgressBar.setVisibility(View.GONE);
                        JSONObject data = jObj.getJSONObject("data");
                        parseJson(data);
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
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public static void longLog(String str, String log) {
        if (str.length() > 4000) {
            Log.d(log, str.substring(0, 4000));
            longLog(str.substring(4000), log);
        } else
            Log.d(log, str);
    }

    private void parseJson(JSONObject data) {
        if (data != null) {
            Iterator<String> it = data.keys();
            while (it.hasNext()) {
                key = it.next();
                mDocumentFiles = new ArrayList<DocumentChild>();
                try {
                    if (data.get(key) instanceof JSONArray) {
                        JSONArray arry = data.getJSONArray(key);
                        int size = arry.length();
                        for (int i = 0; i < size; i++) {
                            DocumentChild files = new DocumentChild();
                            JSONObject obj = arry.getJSONObject(i);
                            files.setFile(obj.getString("file"));
                            files.setDescription(obj.getString("description"));
                            files.setCategoryName(obj.getString("categoryname"));
                            mDocumentFiles.add(files);
                        }
                        adapterDocument.addSection(new MySection(getActivity(), key, mDocumentFiles));
                    } else if (data.get(key) instanceof JSONObject) {

                    } else {
                        System.out.println("" + key + " : " + data.optString(key));
                    }
                } catch (Throwable e) {
                    System.out.println("" + key + " : " + data.optString(key));
                    e.printStackTrace();
                }
            }
        }
        recyclerDocumet.setAdapter(adapterDocument);
    }
}
