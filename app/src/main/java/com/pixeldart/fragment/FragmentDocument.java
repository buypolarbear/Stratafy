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
import com.pixeldart.adapter.AdapterDocument;
import com.pixeldart.adapter.AdapterLaws;
import com.pixeldart.helper.Glob;
import com.pixeldart.helper.MyApplication;
import com.pixeldart.model.Document;
import com.pixeldart.model.Laws;
import com.pixeldart.service.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by cn on 8/14/2017.
 */

public class FragmentDocument extends Fragment {

    private static final String EXTRA_TEXT = "text";
    private RecyclerView recyclerDocumet;
    private RecyclerView.LayoutManager mLayoutManager;
    private AdapterDocument adapterDocument;
    private List<Document> mDocumentList = new ArrayList<>();
    private ProgressBar mProgressBar;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String property_id;
    private int uid;

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
        initialization(view);
        return view;
    }

    private void initialization(View view) {

        pref = getActivity().getSharedPreferences(Config.SHARED_PREF, 0);
        editor = pref.edit();
        property_id = pref.getString("property_id", null);
        uid = pref.getInt("uid", 0);


        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressbar);
        recyclerDocumet = (RecyclerView)view.findViewById(R.id.recycleDocument);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerDocumet.setLayoutManager(mLayoutManager);
        adapterDocument = new AdapterDocument(getActivity(), mDocumentList);
        recyclerDocumet.setAdapter(adapterDocument);

        getLaws();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String text = getArguments().getString(EXTRA_TEXT);
       // Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
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
                       /* JSONArray laws = data.getJSONObject("by_law").getJSONArray("laws");
                        for(int i= 0; i < laws.length(); i++){
                            JSONObject obj = laws.getJSONObject(i);
                            Laws laws1 = new Laws();
                            laws1.setLaw_name(obj.getString("title"));
                            laws1.setLaw_text(obj.getString("description"));
                            mLawsList.add(laws1);
                        }
                        adapterLaws.notifyDataSetChanged();*/

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
                String key = it.next();
                Log.d("Key:", key);

                try {
                    if (data.get(key) instanceof JSONArray) {
                        JSONArray arry = data.getJSONArray(key);
                        int size = arry.length();
                        for (int i = 0; i < size; i++) {
                            longLog(arry.getJSONObject(i).toString(), "JsonObject");
                        }
                    } else if (data.get(key) instanceof JSONObject) {
                      //  parseJson(data.getJSONObject(key));
                    } else {
                        System.out.println("" + key + " : " + data.optString(key));
                    }
                } catch (Throwable e) {
                    System.out.println("" + key + " : " + data.optString(key));
                    e.printStackTrace();

                }
            }
        }
    }
}
