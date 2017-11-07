package com.stratafy.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.stratafy.R;
import com.stratafy.activities.MainActivity;
import com.stratafy.adapter.AdapterConcierge;
import com.stratafy.helper.Glob;
import com.stratafy.helper.MyApplication;
import com.stratafy.model.Concierge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cn on 8/14/2017.
 */

public class FragmentConcierge extends Fragment {

    private static final String EXTRA_TEXT = "text";
    private TextView txtText;
    private ProgressBar mProgressBar;

    private RecyclerView recycleview;
    private StaggeredGridLayoutManager layoutManager;
    private AdapterConcierge adapterConcierge;
    private List<Concierge> mList = new ArrayList<>();
    FloatingActionButton fab_chat, fab_call;

    public static LinearLayout llFloat;
    public static String number;

    public static FragmentConcierge instance(String text) {
        FragmentConcierge fragment = new FragmentConcierge();
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
        View view = inflater.inflate(R.layout.fragment_conceirge, container, false);

        MainActivity.txtToolbarTitle.setVisibility(View.VISIBLE);
        MainActivity.txtToolbarTitle.setText("Concierge Services");
        MainActivity.imgStreaming.setVisibility(View.GONE);

        initialization(view);

        return view;
    }

    private void initialization(View view) {
        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressbar);
        txtText = (TextView)view.findViewById(R.id.txtText);

        recycleview = (RecyclerView)view.findViewById(R.id.recyclerConcierge);
        recycleview.setHasFixedSize(true);

        layoutManager = new StaggeredGridLayoutManager(2, 1);
        recycleview.setLayoutManager(layoutManager);

        adapterConcierge = new AdapterConcierge(getActivity(), mList);
        recycleview.setAdapter(adapterConcierge);

        llFloat = (LinearLayout)view.findViewById(R.id.llFloat);
        fab_chat = (FloatingActionButton)view.findViewById(R.id.fab_chat);
        fab_call = (FloatingActionButton)view.findViewById(R.id.fab_call);

        fab_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialContactPhone(number);
            }
        });
    }

    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getConcierge(getActivity());
    }

    public static void longLog(String str, String log) {
        if (str.length() > 4000) {
            Log.d(log, str.substring(0, 4000));
            longLog(str.substring(4000), log);
        } else
            Log.d(log, str);
    }

    private void getConcierge(final Context context) {
        mProgressBar.setVisibility(View.VISIBLE);
        String url = Glob.API_CONCIERGE + MainActivity.property_id + ".json";
        Log.d("URL", url);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                longLog(response.toString(), "getConcierge");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int status = jObj.getInt("status");
                    final String errorMsg = jObj.getString("errorMsg");
                    if (status != 0) {
                        mProgressBar.setVisibility(View.GONE);
                        JSONArray data = jObj.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++){
                            JSONObject r = data.getJSONObject(i);
                            Concierge concierge = new Concierge();
                            concierge.setTitle(r.getString("title"));
                            concierge.setImage(r.getString("image"));
                            concierge.setId(r.getString("id"));
                            concierge.setNumber(r.getString("number"));
                            mList.add(concierge);
                        }
                        adapterConcierge.notifyDataSetChanged();
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                Log.e("TAG", "Login Error: " + error.getMessage());
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

}
