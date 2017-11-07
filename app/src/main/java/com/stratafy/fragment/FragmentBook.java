package com.stratafy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.stratafy.R;
import com.stratafy.activities.MainActivity;
import com.stratafy.adapter.AdapterBooking;
import com.stratafy.helper.Glob;
import com.stratafy.helper.NegativeTopItemDecorator;

/**
 * Created by cn on 8/14/2017.
 */

public class FragmentBook extends Fragment {


    private TextView title1, txtFilter;

    private RecyclerView.LayoutManager mLayoutManager;
    RecyclerView recyclerView;
    AdapterBooking myAdapter;

    public static FragmentBook instance(String text) {
        FragmentBook fragment = new FragmentBook();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        MainActivity.txtToolbarTitle.setVisibility(View.GONE);
        MainActivity.imgStreaming.setVisibility(View.GONE);
        initialization(view);
        return view;
    }

    private void initialization(View view) {
        title1 = (TextView)view.findViewById(R.id.txt2);
        txtFilter = (TextView)view.findViewById(R.id.txtFilter);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);

        myAdapter = new AdapterBooking(getActivity());
        recyclerView.setAdapter(myAdapter);

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);


        int topMargin = - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        NegativeTopItemDecorator itemDecorator = new NegativeTopItemDecorator(topMargin);
        recyclerView.addItemDecoration(itemDecorator);
    }
}
