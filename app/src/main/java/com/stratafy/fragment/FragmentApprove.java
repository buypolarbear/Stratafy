package com.stratafy.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.stratafy.R;
import com.stratafy.activities.MainActivity;
import com.stratafy.helper.Glob;

/**
 * Created by cn on 8/25/2017.
 */

public class FragmentApprove extends Fragment implements View.OnClickListener {

    private EditText edtCategory, edtNotes;
    private TextView txtTitle, txtDetails;
    private FloatingActionButton btnPhoto, btnClose;
    private ImageView img;
    private RelativeLayout llRoot, llRoot2;

    public static FragmentApprove instance(String title, String detail, String cat_name, String image) {
        FragmentApprove fragment = new FragmentApprove();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("detail", detail);
        args.putString("cat_name", cat_name);
        args.putString("image", image);
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
        View view = inflater.inflate(R.layout.fragment_approved, container, false);
        MainActivity.txtToolbarTitle.setVisibility(View.VISIBLE);
        MainActivity.txtToolbarTitle.setText("Approve");

        initialization(getActivity(), view);

        return view;
    }

    private void initialization(Context context, View view) {
        llRoot = (RelativeLayout) view.findViewById(R.id.llRoot);
        llRoot2 = (RelativeLayout) view.findViewById(R.id.llRoot2);

        img = (ImageView) view.findViewById(R.id.img);

        txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        txtDetails = (TextView) view.findViewById(R.id.txtDetails);

        edtCategory = (EditText) view.findViewById(R.id.edtCategory);
        edtNotes = (EditText) view.findViewById(R.id.edtNotes);

        edtCategory.setTypeface(Glob.avenir(context));
        edtNotes.setTypeface(Glob.avenir(context));

        txtTitle.setTypeface(Glob.avenir(context));
        txtDetails.setTypeface(Glob.avenir(context));

        btnPhoto = (FloatingActionButton) view.findViewById(R.id.btnPhoto);
        btnClose = (FloatingActionButton) view.findViewById(R.id.btnClose);

        btnPhoto.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        edtNotes.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.edtNotes:

                break;
            case R.id.btnPhoto:
                if (getArguments().getString("image")!= null) {
                    llRoot.setVisibility(View.GONE);
                    llRoot2.setVisibility(View.VISIBLE);
                } else {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                    dialog.setMessage("               There are no image");
                    dialog.show();
                }

                break;
            case R.id.btnClose:
                llRoot2.setVisibility(View.GONE);
                llRoot.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        txtTitle.setText(getArguments().getString("title"));
        edtCategory.setText(getArguments().getString("cat_name"));

        if (!getArguments().getString("detail").equals("null")) {
            txtDetails.setText(getArguments().getString("detail"));
        }

        if (getArguments().getString("image") != null) {
            Glide.with(getActivity()).load(getArguments().getString("image")).into(img);
            Log.d("ImageURL1:", String.valueOf(getArguments().getString("image")));
        }


    }

}


