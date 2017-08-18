package com.pixeldart.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pixeldart.R;
import com.pixeldart.helper.ConnectionDetector;
import com.pixeldart.helper.Glob;
import com.pixeldart.helper.MyApplication;
import com.pixeldart.helper.SessionManager;
import com.pixeldart.service.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtSignup, txtEmail, txtPassword, txtForgot;
    private EditText edtEmail, edtPassword;
    private Button btnSignin;

    private Intent intent;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private SessionManager session;
    private ConnectionDetector cd;
    private Boolean isInternetPresent = false;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        editor = pref.edit();
        session = new SessionManager(this);

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();

        initialization(this);
    }

    private void initialization(SigninActivity activity) {
        mProgressBar = (ProgressBar)findViewById(R.id.mProgressbar);

        txtSignup = (TextView) findViewById(R.id.txtSignup);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtPassword = (TextView) findViewById(R.id.txtPassword);
        txtForgot = (TextView) findViewById(R.id.txtforgot);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        btnSignin = (Button) findViewById(R.id.btnSignin);
        btnSignin.setTypeface(Glob.avenir(activity));
        btnSignin.setOnClickListener(this);
        txtSignup.setOnClickListener(this);
        txtForgot.setOnClickListener(this);

        txtSignup.setTypeface(Glob.avenir(activity));
        txtEmail.setTypeface(Glob.avenir(activity));
        txtPassword.setTypeface(Glob.avenir(activity));
        txtForgot.setTypeface(Glob.avenir(activity));

        edtEmail.setTypeface(Glob.avenir(activity));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnSignin:
                if(isInternetPresent){
                    if(!edtEmail.getText().toString().isEmpty() && !edtPassword.getText().toString().isEmpty()){
                        String token = pref.getString("regId", null);
                        String email = edtEmail.getText().toString();
                        String password = edtPassword.getText().toString();
                        mProgressBar.setVisibility(View.VISIBLE);
                        if(!token.equals("null")){
                            checkLogin(SigninActivity.this, token, email, password);
                        }
                    }else {
                        Toast.makeText(SigninActivity.this, getResources().getString(R.string.empty), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(SigninActivity.this, getResources().getString(R.string.noconnection), Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.txtSignup:
                intent = new Intent(SigninActivity.this, SelectRecidencyActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;

            case R.id.txtforgot:
                intent = new Intent(SigninActivity.this, ForgetPassword.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void checkLogin(final Context context, final String token, final String email, final String password) {
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Glob.API_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    int status = jObj.getInt("status");
                    final String errorMsg = jObj.getString("errorMsg");
                    if (status != 0) {
                        mProgressBar.setVisibility(View.GONE);
                        JSONObject user = jObj.getJSONObject("data");
                        editor.putInt("uid", user.getInt("id"));
                        editor.putString("username", user.getString("first_name"));
                        editor.putString("email", user.getString("email"));
                        editor.putString("property_id", user.getString("property_id"));
                        editor.putString("profile_type", user.getString("profile_type"));
                        session.setLogin(true);
                        editor.commit();
                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                Log.e("TAG", "Login Error: " + error.getMessage());
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("data[User][device_token]", token);
                params.put("data[User][device_type]", "A");
                params.put("data[User][email]", email);
                params.put("data[User][password]", password);
                return params;
            }
        };
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
