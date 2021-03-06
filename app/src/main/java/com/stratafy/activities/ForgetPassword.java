package com.stratafy.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.stratafy.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ForgetPassword extends AppCompatActivity implements View.OnClickListener {

    private TextView txtSignup, txtEmail, txtSignin;
    private EditText edtEmail;
    private Button btnSubmit;

    private Intent intent;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        initialization(this);
    }

    private void initialization(ForgetPassword context) {
        txtSignup = (TextView)findViewById(R.id.txtSignup);
        txtEmail = (TextView)findViewById(R.id.txtEmail);
        txtSignin = (TextView)findViewById(R.id.txtSignin);

        edtEmail = (EditText)findViewById(R.id.edtEmail);

        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
        txtSignin.setOnClickListener(this);
        txtSignup.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.txtSignin:
                intent = new Intent(ForgetPassword.this, SigninActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.txtSignup:
                intent = new Intent(ForgetPassword.this, SignupActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.btnSubmit:
                intent = new Intent(ForgetPassword.this, SigninActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
