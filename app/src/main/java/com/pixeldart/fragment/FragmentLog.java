package com.pixeldart.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pixeldart.R;
import com.pixeldart.activities.MainActivity;
import com.pixeldart.helper.Glob;

/**
 * Created by cn on 8/14/2017.
 */

public class FragmentLog extends Fragment {

    private static final String EXTRA_TEXT = "text";
    private TextView txt2, txtNoMyLog, txtNoPLog, txtNoArchived, txtMyLog, txtPLog, txtArchived;

    public static FragmentLog instance(String text) {
        FragmentLog fragment = new FragmentLog();
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
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        MainActivity.txtToolbarTitle.setVisibility(View.VISIBLE);
        MainActivity.txtToolbarTitle.setText(getResources().getString(R.string.logs));
        initialization(view);
        return view;
    }

    private void initialization(View view) {
        txt2 = (TextView)view.findViewById(R.id.txt2);
        txtNoMyLog = (TextView)view.findViewById(R.id.txtNoMyLog);
        txtNoPLog = (TextView)view.findViewById(R.id.txtNoPLog);
        txtNoArchived = (TextView)view.findViewById(R.id.txtNoArchived);
        txtMyLog = (TextView)view.findViewById(R.id.txtMyLog);
        txtPLog = (TextView)view.findViewById(R.id.txtPLog);
        txtArchived = (TextView)view.findViewById(R.id.txtArchived);

        txt2.setTypeface(Glob.avenir(getActivity()));
        txtNoMyLog.setTypeface(Glob.avenir(getActivity()));
        txtNoPLog.setTypeface(Glob.avenir(getActivity()));
        txtNoArchived.setTypeface(Glob.avenir(getActivity()));
        txtMyLog.setTypeface(Glob.avenir(getActivity()));
        txtPLog.setTypeface(Glob.avenir(getActivity()));
        txtArchived.setTypeface(Glob.avenir(getActivity()));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String text = getArguments().getString(EXTRA_TEXT);
       // Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }
}
