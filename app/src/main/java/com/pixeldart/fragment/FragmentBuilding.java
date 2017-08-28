package com.pixeldart.fragment;

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
import com.pixeldart.R;
import com.pixeldart.activities.MainActivity;
import com.pixeldart.adapter.AdapterCategory;
import com.pixeldart.helper.Glob;
import com.pixeldart.helper.MyApplication;
import com.pixeldart.model.Category;
import com.pixeldart.service.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cn on 8/14/2017.
 */

public class FragmentBuilding extends Fragment {

    private RecyclerView recycleCategory, recycleDocument;
    private RecyclerView.LayoutManager mLayoutManager;
    private AdapterCategory adapter;
    private List<Category> mList = new ArrayList<>();
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
        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressbar);

        recycleCategory = (RecyclerView) view.findViewById(R.id.recycleCategory);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycleCategory.setLayoutManager(mLayoutManager);

        adapter = new AdapterCategory(getActivity(), mList);
        recycleCategory.setAdapter(adapter);
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
                       /* if(data != null){
                            for (int i = 0; i < data.length(); i++){
                                JSONObject r = data.getJSONObject(i);
                                Category notifications = new Category();
                                notifications.setCat(r.getString("message"));
                                mList.add(notifications);
                            }
                        }*/

                        mProgressBar.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
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

}
