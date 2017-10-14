package com.stratafy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stratafy.R;
import com.stratafy.helper.Glob;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView txtSignin, txtSignup;
    private LinearLayout llSignup, llSignin;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialization(this);

    }

    private void initialization(LoginActivity activity) {
        txtSignin = (TextView)findViewById(R.id.txtSignin);
        txtSignup = (TextView)findViewById(R.id.txtSignup);

        txtSignin.setTypeface(Glob.avenir(activity));
        txtSignup.setTypeface(Glob.avenir(activity));

        llSignin = (LinearLayout)findViewById(R.id.llSignin);
        llSignup = (LinearLayout)findViewById(R.id.llSignup);

        llSignin.setOnClickListener(activity);
        llSignup.setOnClickListener(activity);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.llSignin:
                intent = new Intent(LoginActivity.this, SigninActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
            case R.id.llSignup:
                intent = new Intent(LoginActivity.this, SelectRecidencyActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
        }
    }
}
