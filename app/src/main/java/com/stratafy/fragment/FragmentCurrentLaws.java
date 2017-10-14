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

import android.widget.TextView;

import com.stratafy.R;
import com.stratafy.adapter.AdapterLaws;
import com.stratafy.helper.Glob;

import com.stratafy.model.Laws;

import java.util.ArrayList;
import java.util.List;

public class FragmentCurrentLaws extends Fragment {

    private RecyclerView recyclerLaws;
    private RecyclerView.LayoutManager mLayoutManager;
    private AdapterLaws adapterLaws;
    private List<Laws> mLawsList;
    private static final String EXTRA_TEXT = "list";

    private TextView txt3;

    public FragmentCurrentLaws() {
    }

    public static FragmentCurrentLaws newInstance(List<Laws> mLawsList) {
        FragmentCurrentLaws fragment = new FragmentCurrentLaws();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_TEXT, (ArrayList<? extends Parcelable>) mLawsList);
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
        View view = inflater.inflate(R.layout.fragment_fragment_current_laws, container, false);
        initialization(view);
        return view;
    }

    private void initialization(View view) {
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerLaws = (RecyclerView)view.findViewById(R.id.recyclerLaws);
        recyclerLaws.setLayoutManager(mLayoutManager);

        txt3 = (TextView) view.findViewById(R.id.txt3);
        txt3.setTypeface(Glob.avenir(getActivity()));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLawsList = new ArrayList<>();
        mLawsList = getArguments().getParcelableArrayList("list");
        adapterLaws = new AdapterLaws(getActivity(), mLawsList);
        recyclerLaws.setAdapter(adapterLaws);
    }
}
