package com.stratafy.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.stratafy.R;
import com.stratafy.helper.ConnectionDetector;
import com.stratafy.helper.Glob;
import com.stratafy.helper.MyApplication;
import com.stratafy.helper.SessionManager;
import com.stratafy.service.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtSignup, txtPID, txtPType, txtName, txtEmail, txtPassword, txtSignin, txtTerm;
    private EditText edtPID, edtPType, edtName, edtEmail, edtPassword;
    private Button btnApprove;

    private Intent intent;
    String property_type, profile_type;
    CharSequence[] items;

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
        setContentView(R.layout.activity_signup);

        pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        editor = pref.edit();
        session = new SessionManager(this);

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();

        Bundle bundle = getIntent().getExtras();
        property_type = bundle.getString("type");

        switch (property_type) {
            case "Residential":
                items = new CharSequence[]{"Building manager", "owner Occupier", "Owner but not occupier",
                        "Tenant in this building", "Contractor", "Employee"};
                break;
            case "Commercial":
                items = new CharSequence[]{"Building management", "Concierge",
                        "Tenant in this building", "Contractor", "Employee"};
                break;
            case "Retail":
                items = new CharSequence[]{"Centre  management", "Retailer",
                         "Contractor", "Employee", "Retail head office"};
                break;
        }

        initialization(this);
    }

    private void initialization(SignupActivity activity) {
        mProgressBar = (ProgressBar)findViewById(R.id.mProgressbar);

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
        edtPType.setOnClickListener(this);

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
        edtEmail.setTypeface(Glob.avenir(activity));
        txtEmail.setTypeface(Glob.avenir(activity));
        txtPassword.setTypeface(Glob.avenir(activity));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnApprove:

                if(validation()){
                    String PID = edtPID.getText().toString();
                    String name = edtName.getText().toString();
                    String email = edtEmail.getText().toString();
                    String password = edtPassword.getText().toString();
                    String token = pref.getString("regId", null);
                    mProgressBar.setVisibility(View.VISIBLE);
                    checkRegister(SignupActivity.this, property_type, PID, profile_type, name, token, email, password);
                }
                break;

            case R.id.txtSignin:
                intent = new Intent(SignupActivity.this, SigninActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.edtPType:
                SelectProfileType(items);
                break;

            case R.id.txtTerm:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://stratafyconnect.com/terms-of-use/"));
                startActivity(browserIntent);
                break;
        }
    }

    private void checkRegister(final Context context, final String property, final String PID, final String pType, final String name, final String token, final String email, final String password) {
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Glob.API_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Signup Response: " + response.toString());

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
                        intent = new Intent(SignupActivity.this, VerifyActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("area", property_type);
                        intent.putExtra("type", profile_type);
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
                params.put("data[User][profile_type]", pType);
                params.put("data[User][first_name]", name);
                params.put("data[User][property_id]", PID);
                params.put("data[User][property_type]", property);
                params.put("data[User][device_type]", "A");
                params.put("data[User][email]", email);
                params.put("data[User][password]", password);
                Log.d("PARAM_REGISTER:", params.toString());
                return params;
            }
        };
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public boolean validation() {
        if (edtPID.getText().toString().trim().isEmpty()) {
            edtPID.setError("Property ID is empty");
            return false;
        }
        else if (edtPType.getText().toString().trim().isEmpty()) {
            edtPType.setError("Profile Type is empty");
            return false;
        }
        else if (edtName.getText().toString().trim().isEmpty()) {
            edtName.setError("Name is empty");
            return false;
        }
        else if (edtEmail.getText().toString().trim().isEmpty()) {
            edtEmail.setError("add email address");
            return false;
        } else if (edtPassword.getText().toString().trim().isEmpty()) {
            edtPassword.setError("password is empty");
            return false;
        } else if (edtPassword.getText().length() < 6) {
            edtPassword.setError("password must be greater than 6 character");
            return false;
        }
        else {
            return true;
        }
    }

    public void SelectProfileType(final CharSequence[] items) {
       AlertDialog dialog =  new AlertDialog.Builder(this)

                .setSingleChoiceItems(items, 0, null)
                .setPositiveButton(R.string.ok_button_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        String ptype = ((AlertDialog)dialog).getListView().getItemAtPosition(selectedPosition).toString();
                        edtPType.setText(ptype);
                        switch (property_type) {
                            case "Residential":
                                if (selectedPosition == 0) {
                                    profile_type = "manager";
                                } else if (selectedPosition == 1) {
                                    profile_type = "owner_occupier";
                                } else if (selectedPosition == 2) {
                                    profile_type = "owner_not_occupier";
                                } else if (selectedPosition == 3) {
                                    profile_type = "tenant";
                                } else if (selectedPosition == 4) {
                                    profile_type = "contractor";
                                } else if (selectedPosition == 5) {
                                    profile_type = "employee";
                                }
                                break;
                            case "Commercial":
                                if (selectedPosition == 0) {
                                    profile_type = "building_management";
                                } else if (selectedPosition == 1) {
                                    profile_type = "concierge";
                                } else if (selectedPosition == 2) {
                                    profile_type = "tenant";
                                } else if (selectedPosition == 3) {
                                    profile_type = "contractor";
                                } else if (selectedPosition == 4) {
                                    profile_type = "employee";
                                }
                                break;
                            case "Retail":
                                if (selectedPosition == 0) {
                                    profile_type = "center_manager";
                                } else if (selectedPosition == 1) {
                                    profile_type = "retailer";
                                } else if (selectedPosition == 2) {
                                    profile_type = "contractor";
                                } else if (selectedPosition == 3) {
                                    profile_type = "employee";
                                } else if (selectedPosition == 4) {
                                    profile_type = "retail_head_office";
                                }
                                break;
                        }
                    }
                })
                .show();

    }
}
