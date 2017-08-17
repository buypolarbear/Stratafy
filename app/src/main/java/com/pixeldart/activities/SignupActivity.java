package com.pixeldart.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pixeldart.R;
import com.pixeldart.helper.Glob;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtSignup, txtPID, txtPType, txtName, txtEmail, txtPassword, txtSignin, txtTerm;
    private EditText edtPID, edtPType, edtName, edtEmail, edtPassword;
    private Button btnApprove;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initialization(this);
    }

    private void initialization(SignupActivity activity) {
        txtSignup = (TextView)findViewById(R.id.txtSignup);
        txtSignin = (TextView)findViewById(R.id.txtSignin);
        txtPID = (TextView)findViewById(R.id.txtPID);
        txtPType = (TextView)findViewById(R.id.txtPType);
        txtName = (TextView)findViewById(R.id.txtName);
        txtEmail = (TextView)findViewById(R.id.txtEmail);
        txtPassword = (TextView)findViewById(R.id.txtPassword);
        txtTerm = (TextView)findViewById(R.id.txtTerm);

        edtPID = (EditText)findViewById(R.id.edtPID);
        edtPType = (EditText)findViewById(R.id.edtPType);
        edtName = (EditText)findViewById(R.id.edtName);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtPassword = (EditText)findViewById(R.id.edtPassword);

        btnApprove = (Button)findViewById(R.id.btnApprove);
        btnApprove.setTypeface(Glob.avenir(activity));
        btnApprove.setOnClickListener(this);
        txtSignin.setOnClickListener(this);
        txtTerm.setOnClickListener(this);

        txtSignup.setTypeface(Glob.avenir(activity));
        txtSignin.setTypeface(Glob.avenir(activity));
        txtPID.setTypeface(Glob.avenir(activity));
        txtPType.setTypeface(Glob.avenir(activity));
        txtName.setTypeface(Glob.avenir(activity));
        txtEmail.setTypeface(Glob.avenir(activity));
        txtPassword.setTypeface(Glob.avenir(activity));
        txtTerm.setTypeface(Glob.avenir(activity));

        edtPID.setTypeface(Glob.avenir(activity));
        edtPType.setTypeface(Glob.avenir(activity));
        edtName.setTypeface(Glob.avenir(activity));
        txtEmail.setTypeface(Glob.avenir(activity));
        txtPassword.setTypeface(Glob.avenir(activity));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnApprove:
                intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.txtSignin:
                intent = new Intent(SignupActivity.this, SigninActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.txtTerm:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://stratafyconnect.com/terms-of-use/"));
                startActivity(browserIntent);
                break;
        }
    }
}
