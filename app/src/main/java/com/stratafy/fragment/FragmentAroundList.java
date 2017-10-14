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
import com.stratafy.adapter.AdapterAround;
import com.stratafy.model.AroundPlaces;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cn on 10/11/2017.
 */

public class FragmentAroundList extends Fragment {
    private RecyclerView recyclerAround;
    private RecyclerView.LayoutManager mLayoutManager;
    private AdapterAround adapterAround;
    private List<AroundPlaces> mAroundList;
    private static final String EXTRA_TEXT = "list";

    public FragmentAroundList() {
    }

    public static FragmentAroundList newInstance(List<AroundPlaces> mList) {
        FragmentAroundList fragment = new FragmentAroundList();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_TEXT, (ArrayList<? extends Parcelable>) mList);
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
        View view = inflater.inflate(R.layout.fragment_around_list, container, false);
        initialization(view);
        return view;
    }

    private void initialization(View view) {
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerAround = (RecyclerView)view.findViewById(R.id.recyclerAround);
        recyclerAround.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mAroundList = new ArrayList<>();
        mAroundList = getArguments().getParcelableArrayList("list");

        adapterAround = new AdapterAround(getActivity(), mAroundList);
        recyclerAround.setAdapter(adapterAround);
    }
}
