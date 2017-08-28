package com.pixeldart.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.pixeldart.R;
import com.pixeldart.activities.MainActivity;
import com.pixeldart.adapter.AdapterCategory;
import com.pixeldart.adapter.AdapterContact;
import com.pixeldart.helper.Glob;
import com.pixeldart.helper.MyApplication;
import com.pixeldart.model.Category;
import com.pixeldart.model.Contact;
import com.pixeldart.service.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


/**
 * Created by cn on 8/14/2017.
 */

public class FragmentBuilding extends Fragment {

    private RecyclerView recycleCategory, recycleDocument;
    private RecyclerView.LayoutManager mLayoutManager, mLayoutManager2;
    private AdapterCategory adapter;
    private SectionedRecyclerViewAdapter adapterDocument;
    private List<Category> mList = new ArrayList<>();
    private List<Contact> mContactList = new ArrayList<>();
    private ProgressBar mProgressBar;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String property_id;
    private int uid;
    private String key;


    private static final String EXTRA_TEXT = "text";

    public static FragmentBuilding instance(String text) {
        FragmentBuilding fragment = new FragmentBuilding();
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
        View view = inflater.inflate(R.layout.fragment_building, container, false);

        pref = getActivity().getSharedPreferences(Config.SHARED_PREF, 0);
        editor = pref.edit();
        property_id = pref.getString("property_id", null);
        uid = pref.getInt("uid", 0);


        MainActivity.txtToolbarTitle.setVisibility(View.VISIBLE);
        MainActivity.txtToolbarTitle.setText("Directory");

        initialization(view);
        getCategory();

        return view;
    }

    private void initialization(View view) {

        adapterDocument = new SectionedRecyclerViewAdapter();
        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressbar);

        recycleCategory = (RecyclerView) view.findViewById(R.id.recycleCategory);
        recycleDocument = (RecyclerView) view.findViewById(R.id.recycleDocument);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycleCategory.setLayoutManager(mLayoutManager);
        recycleDocument.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        adapter = new AdapterCategory(getActivity(), mList);
        recycleCategory.setAdapter(adapter);
        recycleDocument.setAdapter(adapterDocument);
    }

    public static void longLog(String str, String log) {
        if (str.length() > 4000) {
            Log.d(log, str.substring(0, 4000));
            longLog(str.substring(4000), log);
        } else
            Log.d(log, str);
    }

    private void getCategory() {
        mProgressBar.setVisibility(View.VISIBLE);
        mList.clear();
        String tag_string_req = "req_login";
        String url = Glob.API_GET_DIRECTORY +  property_id + ".json";
        Log.d("URl", url);
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


                        JSONArray category = jObj.getJSONArray("categories");
                        for (int i = 0; i < category.length(); i++) {
                            JSONObject r = category.getJSONObject(i);
                            Category category1 = new Category();
                            category1.setCat(r.getString("name"));
                            category1.setId(r.getString("id"));
                            mList.add(category1);
                        }

                        JSONArray data = jObj.getJSONArray("data");
                        if(data != null){
                            for (int i = 0; i < data.length(); i++){
                                JSONObject r = data.getJSONObject(i);
                                Contact contact = new Contact();
                                contact.setFname(r.getString("first_name"));
                                contact.setLname(r.getString("last_name"));
                                contact.setEmail(r.getString("email"));
                                contact.setCno(r.getString("contact_num"));
                                contact.setPtype(r.getString("profile_type"));
                                contact.setPosition(r.getString("position"));
                                contact.setStatus(r.getString("status"));
                                mContactList.add(contact);
                            }
                        }

                        for(char alphabet = 'A'; alphabet <= 'Z';alphabet++) {
                            List<Contact> contacts = getContactsWithLetter(alphabet);

                            if (contacts.size() > 0) {
                                adapterDocument.addSection(new AdapterContact(getActivity(), String.valueOf(alphabet), contacts));

                            }
                        }

                        mProgressBar.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                        adapterDocument.notifyDataSetChanged();
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

    private List<Contact> getContactsWithLetter(char letter) {
        List<Contact> contacts = new ArrayList<>();

        for (Contact contact : mContactList) {
            if (contact.getFname().toUpperCase().charAt(0) == letter) {
                contacts.add(contact);
            }
        }
        return contacts;
    }

}
