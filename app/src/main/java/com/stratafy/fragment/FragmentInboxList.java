package com.stratafy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stratafy.R;

/**
 * Created by cn on 8/19/2017.
 */

public class FragmentInboxList extends Fragment {

    private static final String EXTRA_TEXT = "text";

    public static FragmentInboxList instance(String text) {
        FragmentInboxList fragment = new FragmentInboxList();
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
        View view = inflater.inflate(R.layout.fragment_inbox_list, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String text = getArguments().getString(EXTRA_TEXT);
      //  Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

}
