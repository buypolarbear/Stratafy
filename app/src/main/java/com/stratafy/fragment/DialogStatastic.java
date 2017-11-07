package com.stratafy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stratafy.R;
import com.stratafy.helper.ArcProgress;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by cn on 10/28/2017.
 */

public class DialogStatastic extends DialogFragment {
    @BindView(R.id.txtTitle) TextView txtTitle;
    @BindView(R.id.txtLawName) TextView txtLawName;
    @BindView(R.id.txtLawText) TextView txtLawText;
    @BindView(R.id.txtPercentAgree) TextView txtPercentAgree;
    @BindView(R.id.txtPercentDisagree) TextView txtPercentDisagree;
    @BindView(R.id.arc_progress1) ArcProgress arc_p1;
    @BindView(R.id.arc_progress2) ArcProgress arc_p2;

    int agree, disagree;

    private Unbinder unbinder;

    public static DialogStatastic newInstance() {
        DialogStatastic dialog = new DialogStatastic();
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyMaterialThemeFull);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_statastic, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        agree = Integer.parseInt(getArguments().getString("agree"));
        disagree = Integer.parseInt(getArguments().getString("disagree"));

        txtLawName.setText(getArguments().getString("law_name"));
        txtLawText.setText(getArguments().getString("law_text"));
        arc_p1.setProgress(agree);
        arc_p2.setProgress(disagree);

        int total = agree + disagree;
        if(total != 0){
            int percentAgree = agree * 100 / total;
            int percentDisagree = disagree * 100 / total;

            txtPercentAgree.setText(""+percentAgree + "%");
            txtPercentDisagree.setText(""+percentDisagree + "%");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
