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
import com.stratafy.adapter.AdapterArchived;
import com.stratafy.model.Archived;

import java.util.ArrayList;
import java.util.List;

public class FragmentArchived extends Fragment {

    private List<Archived> myLogsList;

    private RecyclerView recycleLog;
    private RecyclerView.LayoutManager mLayoutManager;
    private AdapterArchived adapter;

    public static FragmentArchived instance(List<Archived> logsList) {
        FragmentArchived fragment = new FragmentArchived();
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
        View view = inflater.inflate(R.layout.fragment_archived, container, false);
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

        adapter = new AdapterArchived(getActivity(), myLogsList);
        recycleLog.setAdapter(adapter);
    }
}
