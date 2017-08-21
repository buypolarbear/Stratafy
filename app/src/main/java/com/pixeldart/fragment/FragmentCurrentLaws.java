package com.pixeldart.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.pixeldart.R;
import com.pixeldart.adapter.AdapterLaws;
import com.pixeldart.helper.Glob;
import com.pixeldart.helper.MyApplication;
import com.pixeldart.model.Laws;
import com.pixeldart.service.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentCurrentLaws extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ProgressBar mProgressBar;
    private LinearLayout llProgress;

    private RecyclerView recyclerLaws;
    private RecyclerView.LayoutManager mLayoutManager;
    private AdapterLaws adapterLaws;
    private List<Laws> mLawsList = new ArrayList<>();

    private TextView txt3;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String property_id;
    private int uid;

    public FragmentCurrentLaws() {
    }

    public static FragmentCurrentLaws newInstance(String param1, String param2) {
        FragmentCurrentLaws fragment = new FragmentCurrentLaws();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_current_laws, container, false);
        initialization(view);
        return view;
    }

    private void initialization(View view) {

        pref = getActivity().getSharedPreferences(Config.SHARED_PREF, 0);
        editor = pref.edit();
        property_id = pref.getString("property_id", null);
        uid = pref.getInt("uid", 0);

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerLaws = (RecyclerView)view.findViewById(R.id.recyclerLaws);
        recyclerLaws.setLayoutManager(mLayoutManager);
        adapterLaws = new AdapterLaws(getActivity(), mLawsList);
        recyclerLaws.setAdapter(adapterLaws);

        txt3 = (TextView) view.findViewById(R.id.txt3);
        txt3.setTypeface(Glob.avenir(getActivity()));

        llProgress = (LinearLayout)view.findViewById(R.id.llProgress);
        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressbar);

        getLaws();
    }

    private void getLaws() {
        String tag_string_req = "req_login";
        String url = Glob.API_GET_LAWS + property_id + ".json?user_id="+uid;
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
                        llProgress.setVisibility(View.GONE);
                        JSONObject data = jObj.getJSONObject("data");
                        JSONArray laws = data.getJSONObject("by_law").getJSONArray("laws");
                        for(int i= 0; i < laws.length(); i++){
                            JSONObject obj = laws.getJSONObject(i);
                            Laws laws1 = new Laws();
                            laws1.setLaw_name(obj.getString("title"));
                            laws1.setLaw_text(obj.getString("description"));
                            mLawsList.add(laws1);
                        }
                       adapterLaws.notifyDataSetChanged();

                    } else {
                        llProgress.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                    llProgress.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                Log.e("TAG", "Law_error: " + error.getMessage());
                llProgress.setVisibility(View.GONE);
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
}
