package com.stratafy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.stratafy.R;
import com.stratafy.activities.MainActivity;
import com.stratafy.helper.Glob;

/**
 * Created by cn on 8/14/2017.
 */

public class FragmentBook extends Fragment {

    private static final String EXTRA_TEXT = "text";

    private TextView title1, txtFilter;

    public static FragmentBook instance(String text) {
        FragmentBook fragment = new FragmentBook();
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
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        MainActivity.txtToolbarTitle.setVisibility(View.GONE);
        initialization(view);
        return view;
    }

    private void initialization(View view) {
        title1 = (TextView)view.findViewById(R.id.txt2);
        txtFilter = (TextView)view.findViewById(R.id.txtFilter);


        title1.setTypeface(Glob.avenir(getActivity()));
        txtFilter.setTypeface(Glob.avenir(getActivity()));

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String text = getArguments().getString(EXTRA_TEXT);
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }
}
