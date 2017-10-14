package com.stratafy.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stratafy.R;
import com.stratafy.adapter.AdapterPublicLogs;
import com.stratafy.model.PublicLogs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cn on 8/25/2017.
 */

public class FragmentPublicLogs extends Fragment {

    private List<PublicLogs> myLogsList;

    private RecyclerView recycleLog;
    private RecyclerView.LayoutManager mLayoutManager;
    private AdapterPublicLogs adapter;

    public static FragmentPublicLogs instance(List<PublicLogs> logsList) {
        FragmentPublicLogs fragment = new FragmentPublicLogs();
        Bundle args = new Bundle();
        args.putParcelableArrayList("log_list", (ArrayList<? extends Parcelable>) logsList);
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
        View view = inflater.inflate(R.layout.fragment_public_logs, container, false);
        initialization(view);
        return view;
    }

    private void initialization(View view) {
        recycleLog = (RecyclerView) view.findViewById(R.id.recycle_logs);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycleLog.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        myLogsList = new ArrayList<>();
        myLogsList = getArguments().getParcelableArrayList("log_list");

        adapter = new AdapterPublicLogs(getActivity(), myLogsList);
        recycleLog.setAdapter(adapter);

        Log.d("PublicLogs::", "View Called Again");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("OnResume Called:", "FragmentPublic");

    }
}
