package com.stratafy.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.stratafy.R;
import com.stratafy.activities.MainActivity;
import com.stratafy.adapter.AdapterGallery;
import com.stratafy.helper.Glob;
import com.stratafy.helper.MyApplication;
import com.stratafy.model.Gallery;
import com.stratafy.service.Config;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Quadrant;
import com.yuyakaido.android.cardstackview.SwipeDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by cn on 8/14/2017.
 */

public class FragmentHome extends Fragment implements View.OnClickListener {

    private static final String EXTRA_TEXT = "text";

    private RelativeLayout cutLayout, root1, root2;
    private TextView txtStreet, txtAddress, txtLink, txtQuickLinks, txtLog, txtGetInTouch, txtByLaws;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar mProgressBar;
    private ProgressBar progressBar;
    private String property_id, description, address;
    private FloatingActionButton btnInfo, btnShare, btnMap, btnGallery, btnClose;
    private double latitude, longitude;
    private Intent intent;
    private CardStackView cardStackView;
    private AdapterGallery adapter;
    List<Gallery> mList = new ArrayList<>();


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
        setup(view);
      //  reload();

        return view;
    }

    private void initialization(View view) {
        cutLayout = (RelativeLayout) view.findViewById(R.id.llLayerList);
        root1 = (RelativeLayout) view.findViewById(R.id.root1);
        root2 = (RelativeLayout) view.findViewById(R.id.root2);

        txtStreet = (TextView) view.findViewById(R.id.txtStreet);
        txtAddress = (TextView) view.findViewById(R.id.txtAddress);
        txtLink = (TextView) view.findViewById(R.id.txtLink);
        txtQuickLinks = (TextView) view.findViewById(R.id.txtQuickLinks);
        txtLog = (TextView) view.findViewById(R.id.txtLog);
        txtGetInTouch = (TextView) view.findViewById(R.id.txtGetInTouch);
        txtByLaws = (TextView) view.findViewById(R.id.txtViewLaw);

        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressbar);

        btnInfo = (FloatingActionButton)view.findViewById(R.id.btnInfo);
        btnShare = (FloatingActionButton)view.findViewById(R.id.btnShare);
        btnMap = (FloatingActionButton)view.findViewById(R.id.btnMap);
        btnGallery = (FloatingActionButton)view.findViewById(R.id.btnGallery);
        btnClose = (FloatingActionButton)view.findViewById(R.id.btnClose);

        btnInfo.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnMap.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        txtLog.setOnClickListener(this);
        txtGetInTouch.setOnClickListener(this);
        txtByLaws.setOnClickListener(this);

        GetBuilding(getActivity());
    }

    private void setup(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.activity_main_progress_bar);

        cardStackView = (CardStackView) view.findViewById(R.id.card_stack_view);
        cardStackView.setCardEventListener(new CardStackView.CardEventListener() {
            @Override
            public void onCardDragging(float percentX, float percentY) {
                Log.d("CardStackView", "onCardDragging");
            }

            @Override
            public void onCardSwiped(Quadrant quadrant) {
                Log.d("CardStackView", "onCardSwiped: " + quadrant.toString());
                Log.d("CardStackView", "topIndex: " + cardStackView.getTopIndex());
                if (cardStackView.getTopIndex() == mList.size() - 1) {
                    Log.d("CardStackView", "Paginate: " + cardStackView.getTopIndex());
                    cardStackView.setTop(0);
                    paginate();
                }
            }

            @Override
            public void onCardReversed() {
                Log.d("CardStackView", "onCardReversed");
            }

            @Override
            public void onCardMovedToOrigin() {
                Log.d("CardStackView", "onCardMovedToOrigin");
            }

            @Override
            public void onCardClicked(int index) {
                Log.d("CardStackView", "onCardClicked: " + index);
            }
        });
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

                        JSONArray gallery = data.getJSONArray("gallery");
                        for(int i = 0; i<gallery.length(); i++){
                            Gallery gallery1 = new Gallery();
                            gallery1.setUrl(gallery.getString(i));
                            mList.add(gallery1);
                            Log.d("IMageUrl:", gallery.getString(i));
                        }
                       reload();
                    } else {
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
                if(isAdded()){
                    LayerDrawable layerDrawable = (LayerDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.background_home, null);
                    layerDrawable.setDrawableByLayerId(R.id.first_img1, new BitmapDrawable(getActivity().getResources(), x[0]));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        layerDrawable.setDrawable(1, new BitmapDrawable(getActivity().getResources(), x[0]));

                    }
                    cutLayout.setBackground(layerDrawable);
                    mProgressBar.setVisibility(View.GONE);
                }
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
            case R.id.btnGallery:
                root1.setVisibility(View.GONE);
                root2.setVisibility(View.VISIBLE);
                break;
            case R.id.btnClose:
                root2.setVisibility(View.GONE);
                root1.setVisibility(View.VISIBLE);
                break;
            case R.id.txtLog:
                FragmentCreateLog fragment2 = new FragmentCreateLog();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment2, "CREATE_LOG");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.txtGetInTouch:
                intent = new Intent(getActivity(), FragmentCreateLog.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
            case R.id.txtViewLaw:
                intent = new Intent(getActivity(), FragmentCreateLog.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
        }
    }

    public void openDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_info);
        TextView txtInfo = (TextView) dialog.findViewById(R.id.txtInfo);
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

    private void reload() {
        cardStackView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter = createTouristSpotCardAdapter();
                cardStackView.setAdapter(adapter);
                cardStackView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }, 1000);
    }

    private LinkedList<Gallery> extractRemainingTouristSpots() {
        LinkedList<Gallery> spots = new LinkedList<>();
        for (int i = cardStackView.getTopIndex(); i < adapter.getCount(); i++) {
            spots.add(adapter.getItem(i));
        }
        return spots;
    }

    private AdapterGallery createTouristSpotCardAdapter() {
        final AdapterGallery adapter = new AdapterGallery(getActivity());
        adapter.addAll(mList);
        return adapter;
    }

    /*private void addFirst() {
        LinkedList<Gallery> spots = extractRemainingTouristSpots();
        spots.addFirst(mList);
        adapter.clear();
        adapter.addAll(spots);
        adapter.notifyDataSetChanged();
    }

    private void addLast() {
        LinkedList<Gallery> spots = extractRemainingTouristSpots();
        spots.addLast(mList);
        adapter.clear();
        adapter.addAll(spots);
        adapter.notifyDataSetChanged();
    }*/

    private void removeFirst() {
        LinkedList<Gallery> spots = extractRemainingTouristSpots();
        if (spots.isEmpty()) {
            return;
        }

        spots.removeFirst();
        adapter.clear();
        adapter.addAll(spots);
        adapter.notifyDataSetChanged();
    }

    private void removeLast() {
        LinkedList<Gallery> spots = extractRemainingTouristSpots();
        if (spots.isEmpty()) {
            return;
        }

        spots.removeLast();
        adapter.clear();
        adapter.addAll(spots);
        adapter.notifyDataSetChanged();
    }

    private void paginate() {
        cardStackView.setPaginationReserved();
        adapter.addAll(mList);
        adapter.notifyDataSetChanged();
    }

    public void swipeLeft() {
        List<Gallery> spots = extractRemainingTouristSpots();
        if (spots.isEmpty()) {
            return;
        }

        View target = cardStackView.getTopView();

        ValueAnimator rotation = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("rotation", -10f));
        rotation.setDuration(200);
        ValueAnimator translateX = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationX", 0f, -2000f));
        ValueAnimator translateY = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationY", 0f, 500f));
        translateX.setStartDelay(100);
        translateY.setStartDelay(100);
        translateX.setDuration(500);
        translateY.setDuration(500);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(rotation, translateX, translateY);

        cardStackView.swipe(SwipeDirection.Left, set);
    }

    public void swipeRight() {
        List<Gallery> spots = extractRemainingTouristSpots();
        if (spots.isEmpty()) {
            return;
        }

        View target = cardStackView.getTopView();

        ValueAnimator rotation = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("rotation", 10f));
        rotation.setDuration(200);
        ValueAnimator translateX = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationX", 0f, 2000f));
        ValueAnimator translateY = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationY", 0f, 500f));
        translateX.setStartDelay(100);
        translateY.setStartDelay(100);
        translateX.setDuration(500);
        translateY.setDuration(500);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(rotation, translateX, translateY);

        cardStackView.swipe(SwipeDirection.Right, set);
    }

    private void reverse() {
        cardStackView.reverse();
    }
}
