package com.stratafy.fragment;

import android.content.Context;
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
import com.stratafy.adapter.SnapAdapter;
import com.stratafy.helper.Glob;
import com.stratafy.helper.MyApplication;
import com.stratafy.model.Category;
import com.stratafy.model.Contact;
import com.stratafy.model.Snap;
import com.stratafy.service.Config;

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
    SnapAdapter snapAdapter;
    private List<Category> mList = new ArrayList<>();
    private List<Contact> mContactList = new ArrayList<>();
    private ProgressBar mProgressBar;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String property_id;
    private int uid;
    private String key = "1";
    List<Contact> contacts = new ArrayList<>();

    public static LinearLayout llConnect;

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
        MainActivity.imgStreaming.setVisibility(View.GONE);

        initialization(view);
        getCategory();

        return view;
    }

    private void initialization(View view) {

        llConnect = (LinearLayout) view.findViewById(R.id.llConnect);

        snapAdapter = new SnapAdapter(getActivity());
        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressbar);

        recycleCategory = (RecyclerView) view.findViewById(R.id.recycleCategory);
        recycleDocument = (RecyclerView) view.findViewById(R.id.recycleDocument);

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        recycleCategory.setLayoutManager(mLayoutManager);
        adapter = new AdapterCategory(getActivity(), mList);
        recycleCategory.setAdapter(adapter);

        recycleDocument.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleDocument.setHasFixedSize(true);
        recycleDocument.setAdapter(snapAdapter);

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
        String url = Glob.API_GET_DIRECTORY + property_id + ".json";
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
                    if (status == 1) {
                        JSONArray category = jObj.getJSONArray("categories");
                        for (int i = 0; i < category.length(); i++) {
                            JSONObject r = category.getJSONObject(i);
                            Category category1 = new Category();
                            category1.setCat(r.getString("name"));
                            category1.setId(r.getString("id"));
                            mList.add(category1);
                        }

                        JSONArray data = jObj.getJSONArray("data");
                        if (data != null) {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject r = data.getJSONObject(i);
                                Contact contact = new Contact();
                                contact.setFname(r.getString("first_name"));
                                contact.setLname(r.getString("last_name"));
                                contact.setEmail(r.getString("email"));
                                contact.setCno(r.getString("contact_num"));
                                contact.setPtype(r.getString("profile_type"));
                                contact.setPosition(r.getString("position"));
                                contact.setStatus(r.getString("status"));

                                JSONArray cat = r.getJSONArray("user_categories");
                                if (cat != null) {
                                    for (int j = 0; j < cat.length(); j++) {
                                        JSONObject cat_obj = cat.getJSONObject(j);
                                        if (cat_obj.has("id")) {
                                            contact.setCatId(cat_obj.getString("id"));
                                        }

                                    }
                                }

                                mContactList.add(contact);
                            }
                        }

                        for (char alphabet = 'A'; alphabet <= 'Z'; alphabet++) {
                            contacts = getContactsWithLetter1(alphabet);
                            Log.d("ContactSize:", String.valueOf(contacts.toString()));
                            if (contacts.size() > 0) {
                                snapAdapter.addSnap(new Snap(String.valueOf(alphabet), contacts));

                            }
                        }

                        mProgressBar.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                        snapAdapter.notifyDataSetChanged();

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

    private List<Contact> getContactsWithLetter1(char letter) {
        List<Contact> contacts = new ArrayList<>();

        for (Contact contact : mContactList) {
            if (contact.getFname().toUpperCase().charAt(0) == letter) {
                contacts.add(contact);
            }
        }
        return contacts;
    }

    private List<Contact> getContactsWithLetter(char letter) {
        List<Contact> contacts = new ArrayList<>();

        for (Contact contact : mContactList) {
            if (contact.getCatId() != null && !contact.getCatId().isEmpty()) {
                if (contact.getFname().toUpperCase().charAt(0) == letter && contact.getCatId().equals(key)) {
                    contacts.add(contact);
                }
            }
        }
        return contacts;
    }

    public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.MyViewHolder> {
        private Context context;
        private List<Category> mList;
        private final LayoutInflater inflater;
        private int row_index;

        public AdapterCategory(Context context, List<Category> mList) {
            inflater = LayoutInflater.from(context);
            this.context = context;
            this.mList = mList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.card_category, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final Category logs = mList.get(position);
            holder.txtText.setText(logs.getCat());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    row_index = position;
                    notifyDataSetChanged();
                    key = logs.getId();
                    snapAdapter = new SnapAdapter(getActivity());
                    for (char alphabet = 'A'; alphabet <= 'Z'; alphabet++) {
                        if(row_index == 0){
                            List<Contact> contacts = getContactsWithLetter1(alphabet);
                            if (contacts.size() > 0) {
                                snapAdapter.addSnap(new Snap(String.valueOf(alphabet), contacts));
                            }
                        }else {
                            List<Contact> contacts = getContactsWithLetter(alphabet);
                            if (contacts.size() > 0) {
                                snapAdapter.addSnap(new Snap(String.valueOf(alphabet), contacts));
                            }
                        }

                    }
                    recycleDocument.setAdapter(snapAdapter);
                }
            });

            if (row_index == position) {
                holder.txtText.setTextColor(context.getResources().getColor(R.color.blue));
                holder.ll.setBackground(context.getResources().getDrawable(R.drawable.border_radius_solid_white));
            } else {
                holder.ll.setBackground(context.getResources().getDrawable(R.drawable.border_radius));
                holder.txtText.setTextColor(context.getResources().getColor(R.color.white));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txtText;
            LinearLayout ll;
            public MyViewHolder(View itemView) {
                super(itemView);
                txtText = (TextView) itemView.findViewById(R.id.txtText);
                ll = (LinearLayout) itemView.findViewById(R.id.ll);
            }
        }
    }

}
