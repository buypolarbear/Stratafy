package com.stratafy.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stratafy.R;
import com.stratafy.adapter.AdapterNotification;
import com.stratafy.model.Notifications;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cn on 8/19/2017.
 */

public class FragmentNotification extends Fragment {

    private List<Notifications> mList;

    private RecyclerView recycleLog;
    private RecyclerView.LayoutManager mLayoutManager;
    private AdapterNotification adapter;

    private static final String EXTRA_TEXT = "list";

    public static FragmentNotification instance(List<Notifications> mList) {
        FragmentNotification fragment = new FragmentNotification();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_TEXT, (ArrayList<? extends Parcelable>) mList);
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
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        initialization(view);
        return view;
    }

    private void initialization(View view) {
        recycleLog = (RecyclerView) view.findViewById(R.id.recycleNotification);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycleLog.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mList = new ArrayList<>();
        mList = getArguments().getParcelableArrayList("list");

        adapter = new AdapterNotification(getActivity(), mList);
        recycleLog.setAdapter(adapter);
    }
}
