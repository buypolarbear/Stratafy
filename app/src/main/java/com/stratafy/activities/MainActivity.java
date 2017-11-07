package com.stratafy.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.stratafy.R;
import com.stratafy.fragment.FragmentAround;
import com.stratafy.fragment.FragmentBoard;
import com.stratafy.fragment.FragmentBook;
import com.stratafy.fragment.FragmentBuilding;
import com.stratafy.fragment.FragmentConcierge;
import com.stratafy.fragment.FragmentDocument;
import com.stratafy.fragment.FragmentEvent;
import com.stratafy.fragment.FragmentHome;
import com.stratafy.fragment.FragmentInbox;
import com.stratafy.fragment.FragmentLaws;
import com.stratafy.fragment.FragmentLog;
import com.stratafy.fragment.FragmentProfile;
import com.stratafy.helper.ConnectionDetector;
import com.stratafy.helper.Glob;
import com.stratafy.helper.MyApplication;
import com.stratafy.helper.SessionManager;
import com.stratafy.menu.DrawerAdapter;
import com.stratafy.menu.DrawerItem;
import com.stratafy.menu.SimpleItem;
import com.stratafy.service.Config;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener, View.OnClickListener {

    private static final int POS_HOME= 0;
    private static final int POS_INBOX = 1;
    private static final int POS_LOG = 2;
    private static final int POS_BOOK = 3;
    private static final int POS_BUILDING = 4;
    private static final int POS_LAWS = 5;
    private static final int POS_DOCUMENT = 6;
    private static final int POS_EVENT = 7;
    private static final int POS_BOARD = 8;
    private static final int POS_AROUND = 9;
    private static final int POS_PROFILE = 10;
    private static final int POS_CONCIERGE = 11;

    SlidingRootNav mSlideMenu;
    private String[] screenTitles;
    private Drawable[] screenIcons;

    private Toolbar mToolbar;
    private TextView txtSetting, txtLogout;
    private ImageView imgClose, imgRefresh;
    private FrameLayout llContainer;

    private ConnectionDetector cd;
    private Boolean isInternetPresent = false;
    private SessionManager session;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    public static String property_id, profile_type;
    public static int uid;
    private ProgressBar mProgressBar;

    List<DrawerItem> items;
    DrawerAdapter adapter;
    RecyclerView list;

    public static TextView txtToolbarTitle;
    public static ImageView imgStreaming;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.mProgressbar);
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        editor = pref.edit();
        property_id = pref.getString("property_id", null);
        uid = pref.getInt("uid", 0);
        profile_type = pref.getString("profile_type", null);

        session = new SessionManager(this);
        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            Toast.makeText(this, getResources().getString(R.string.noconnection), Toast.LENGTH_SHORT).show();
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);
        mToolbar.setNavigationIcon(R.drawable.ic_menu);

        mSlideMenu = new SlidingRootNavBuilder(this)
                .withMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();



        list = (RecyclerView) findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        getMenu(this);

        initialization(this);
        listener(this);
    }

    public static void longLog(String str, String log) {
        if (str.length() > 4000) {
            Log.d(log, str.substring(0, 4000));
            longLog(str.substring(4000), log);
        } else
            Log.d(log, str);
    }

    private void getMenu(final Context context) {
        String tag_string_req = "req_login";
        String url = Glob.API_MENU_DRAWER + property_id + ".json";
        Log.d("URL", url);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                longLog(response.toString(), "TAG");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int status = jObj.getInt("status");
                    final String errorMsg = jObj.getString("errorMsg");
                    if (status == 1) {
                        JSONArray services = jObj.getJSONArray("services");
                        items = new ArrayList<DrawerItem>();
                        for(int i = 0; i < services.length(); i++) {
                            JSONObject r = services.getJSONObject(i);
                            if(r.getString("key").equals("home_lable")){
                                items.add(createItemFor(POS_HOME));
                            }else if(r.getString("key").equals("inbox_lable")){
                                items.add(createItemFor(POS_INBOX));
                            }else if(r.getString("key").equals("log_lable")){
                                items.add(createItemFor(POS_LOG));
                            }else if(r.getString("key").equals("amenities_lable")){
                                items.add(createItemFor(POS_BOOK));
                            }else if(r.getString("key").equals("directory_lable")){
                                items.add(createItemFor(POS_BUILDING));
                            }else if(r.getString("key").equals("laws_lable")){
                                items.add(createItemFor(POS_LAWS));
                            }else if(r.getString("key").equals("documents_lable")){
                                items.add(createItemFor(POS_DOCUMENT));
                            }else if(r.getString("key").equals("events_lable")){
                                items.add(createItemFor(POS_EVENT));
                            }else if(r.getString("key").equals("board_lable")){
                                items.add(createItemFor(POS_BOARD));
                            }else if(r.getString("key").equals("around_me_lable")){
                                items.add(createItemFor(POS_AROUND));
                            }else if(r.getString("key").equals("profile_lable")){
                                items.add(createItemFor(POS_PROFILE));
                            }else if(r.getString("key").equals("concierge_lable")){
                                items.add(createItemFor(POS_CONCIERGE));
                            }
                        }
                        adapter = new DrawerAdapter(items);
                        adapter.setListener(MainActivity.this);
                        list.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        adapter.setSelected(0);
                        mProgressBar.setVisibility(View.GONE);
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
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void listener(MainActivity context) {
        txtSetting.setOnClickListener(context);
        txtLogout.setOnClickListener(context);
        imgClose.setOnClickListener(context);
        imgRefresh.setOnClickListener(context);
    }

    private void initialization(final Context context) {

        txtToolbarTitle = (TextView)findViewById(R.id.txtToolbarTitle);
        imgStreaming = (ImageView)findViewById(R.id.imgStreaming);

        txtSetting = (TextView)findViewById(R.id.txtSetting);
        txtLogout = (TextView)findViewById(R.id.txtLogout);
        imgClose = (ImageView)findViewById(R.id.imgClose);
        imgRefresh = (ImageView)findViewById(R.id.imgRefresh);

        imgStreaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StreamingActivity.class);
                startActivity(intent);

            }
        });
    }

    private void showFragment(Fragment fragment) {
        llContainer = (FrameLayout)findViewById(R.id.container);
        if (fragment != null) {
            llContainer.removeAllViewsInLayout();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment, fragment.getClass().getName());
            fragmentTransaction.commit();
            fragmentManager.popBackStack();
        }
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(MainActivity.this, screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.white))
                .withTextTint(color(R.color.white))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @Override
    public void onItemSelected(int position) {
        Fragment frag = null;
        switch (position) {
            case POS_HOME:
                frag = FragmentHome.instance(screenTitles[position]);
                break;
            case POS_INBOX:
                txtToolbarTitle.setVisibility(View.VISIBLE);
                txtToolbarTitle.setText("Notifications");
                imgStreaming.setVisibility(View.VISIBLE);
                frag = FragmentInbox.instance(screenTitles[position]);
                break;
            case POS_LOG:
                frag = FragmentLog.instance(screenTitles[position]);;
                break;
            case POS_BOOK:
                frag = FragmentBook.instance(screenTitles[position]);
                break;
            case POS_BUILDING:
                frag = FragmentBuilding.instance(screenTitles[position]);
                break;
            case POS_LAWS:
                frag = FragmentLaws.instance();
                break;
            case POS_DOCUMENT:
                frag = FragmentDocument.instance(screenTitles[position]);
                break;
            case POS_EVENT:
                frag = FragmentEvent.instance(screenTitles[position]);
                break;
            case POS_BOARD:
                frag = FragmentBoard.instance(screenTitles[position]);
                break;
            case POS_AROUND:
                frag = FragmentAround.instance(screenTitles[position]);
                break;
            case POS_PROFILE:
                frag = FragmentProfile.instance(screenTitles[position]);
                break;
            case POS_CONCIERGE:
                frag = FragmentConcierge.instance(screenTitles[position]);
                break;
        }

        showFragment(frag);
        mSlideMenu.closeMenu();
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.imgClose:
                mSlideMenu.closeMenu();
                break;
            case R.id.imgRefresh:
                break;
            case R.id.txtSetting:
                break;
            case R.id.txtLogout:
                session.setLogin(false);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        mSlideMenu.openMenu();
        return true;
    }

}
