package com.stratafy.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.stratafy.R;
import com.stratafy.adapter.CropOptionAdapter;
import com.stratafy.helper.AppHelper;
import com.stratafy.helper.ConnectionDetector;
import com.stratafy.helper.CropOption;
import com.stratafy.helper.Glob;
import com.stratafy.helper.MyApplication;
import com.stratafy.helper.SessionManager;
import com.stratafy.helper.VolleyMultipartRequest;
import com.stratafy.service.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VerifyActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtTake_time, txtVerify_you, txtTap, txtProfile_pic, txtFname,
            txtLName, txtEmail, txtPhone, txtM, txtF, txtPosition, txtDOB, txtGender, txtAddress,
            txtAptNo, txtPeoples, txtCount, txtCount2, txtCount3, txtCount4, txtFloor,
            txtBedrooms, txtBathrooms, txtCars, txtCarNo, txtPCName, txtPCNo, txtEName, txtENo, txtAgentName, txtAgentNo,
            txtCompanyName, txtCarReg, txtEmpNo, txtEmpType, txtkeyName, txtIndustry, txtShopName,
            txtShopLocation, txtShopNumber;

    private EditText edtFname, edtLName, edtEmail, edtPhone, edtAddress, edtAptNo, edtCarNo, edtPCName, edtPCNo, edtEName,
            edtENo, edtAgentName, edtAgentNo, edtCompanyName, edtCarReg, edtEmpNo, edtEmpType, edtKeyName,
            edtIndustry, edtPosition, edtFloor, edtShopName, edtShopLocation, edtShopNumber,
             edtDOB;

    private Button btnApprove;
    private LinearLayout llMale, llFemale, llMinus, llPlus, llMinus2, llPlus2, llMinus3, llPlus3, llMinus4, llPlus4,
            llPickImage, llPosition, llEmployee, llOccupier, llTenent, llContractor, llCTenant, llRetailer;
    private ImageView imgProfile;

    String fname = "", lname = "", email = "", phone = "", DOB = "", address = "", aptNo = "", carNo = "", pcname = "", pcno = "",
            ename = "", eno = "", agentname = "", agentno = "", companyName = "",  carReg = "", empNo = "",
            empType = "", keyName = "", industry = "", position = "", floor = "",
            shopname = "", shoplocation = "", shopnumber = "", peopleCount = "", bedCount = "",
            bathCount = "", carSpace = "";
    private String gender = "M", type, area;
    int count = 0, count2 = 0, count3 = 0, count4 = 0;

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CROP_FROM_CAMERA = 3;
    private Uri fileUri;
    int isCamera = 1;
    String mCurrentPhotoPath;

    String imagePath, property_id;
    int uid;
    Bitmap bitmap1;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private SessionManager session;
    private ConnectionDetector cd;
    private Boolean isInternetPresent = false;
    private ProgressBar mProgressBar;
    CharSequence[] items = {"Full-time", "Half-time"};
    LinearLayout view, view2;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        editor = pref.edit();
        session = new SessionManager(this);
        property_id = pref.getString("property_id", null);
        uid = pref.getInt("uid", 0);

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();

        initialization(this);
    }

    private void initialization(VerifyActivity context) {
        mProgressBar = (ProgressBar) findViewById(R.id.mProgressbar);

        txtTake_time = (TextView) findViewById(R.id.txtTake_time);
        txtVerify_you = (TextView) findViewById(R.id.txtVerify_you);
        txtTap = (TextView) findViewById(R.id.txtTap);
        txtProfile_pic = (TextView) findViewById(R.id.txtProfile_pic);
        txtFname = (TextView) findViewById(R.id.txtFname);
        txtLName = (TextView) findViewById(R.id.txtLName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtPhone = (TextView) findViewById(R.id.txtPhone);
        txtDOB = (TextView) findViewById(R.id.txtDOB);
        txtGender = (TextView) findViewById(R.id.txtGender);
        txtM = (TextView) findViewById(R.id.txtM);
        txtF = (TextView) findViewById(R.id.txtF);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtAptNo = (TextView) findViewById(R.id.txtAptNo);
        txtPeoples = (TextView) findViewById(R.id.txtPeoples);
        txtCount = (TextView) findViewById(R.id.txtCount);
        txtBedrooms = (TextView) findViewById(R.id.txtBedrooms);
        txtBathrooms = (TextView) findViewById(R.id.txtBathrooms);
        txtCars = (TextView) findViewById(R.id.txtCars);
        txtCount2 = (TextView) findViewById(R.id.txtCount2);
        txtCount3 = (TextView) findViewById(R.id.txtCount3);
        txtCount4 = (TextView) findViewById(R.id.txtCount4);
        txtCarNo = (TextView) findViewById(R.id.txtCarNo);
        txtPCName = (TextView) findViewById(R.id.txtPCName);
        txtPCNo = (TextView) findViewById(R.id.txtPCNo);
        txtEName = (TextView) findViewById(R.id.txtEName);
        txtENo = (TextView) findViewById(R.id.txtENo);
        txtAgentName = (TextView) findViewById(R.id.txtAgentName);
        txtAgentNo = (TextView) findViewById(R.id.txtAgentNo);
        txtEmpNo = (TextView) findViewById(R.id.txtEmpNo);
        txtEmpType = (TextView) findViewById(R.id.txtEmpType);
        txtCarReg = (TextView) findViewById(R.id.txtCarReg);
        txtkeyName = (TextView) findViewById(R.id.txtKeyName);

        txtPosition = (TextView) findViewById(R.id.txtPosition);
        txtFloor = (TextView) findViewById(R.id.txtFloor);

        txtShopName = (TextView) findViewById(R.id.txtShopName);
        txtShopLocation = (TextView) findViewById(R.id.txtShopLocation);
        txtShopNumber = (TextView) findViewById(R.id.txtShopNumber);



        edtFname = (EditText) findViewById(R.id.edtFname);
        edtLName = (EditText) findViewById(R.id.edtLName);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        edtAptNo = (EditText) findViewById(R.id.edtAptNo);
        edtCarNo = (EditText) findViewById(R.id.edtCarNo);
        edtPCName = (EditText) findViewById(R.id.edtPCName);
        edtPCNo = (EditText) findViewById(R.id.edtPCNo);
        edtEName = (EditText) findViewById(R.id.edtENAme);
        edtENo = (EditText) findViewById(R.id.edtENo);
        edtAgentName = (EditText) findViewById(R.id.edtAgentName);
        edtAgentNo = (EditText) findViewById(R.id.edtAgentNo);
        edtEmpNo = (EditText) findViewById(R.id.edtEmpNo);
        edtEmpType = (EditText) findViewById(R.id.edtEmpType);
        edtCarReg = (EditText) findViewById(R.id.edtCarReg);
        edtKeyName = (EditText) findViewById(R.id.edtKeyName);

        edtPosition = (EditText) findViewById(R.id.edtPosition);
        edtFloor = (EditText) findViewById(R.id.edtFloor);
        edtShopName = (EditText) findViewById(R.id.edtShopName);
        edtShopLocation = (EditText) findViewById(R.id.edtShopLocation);
        edtShopNumber = (EditText) findViewById(R.id.edtShopNumber);
        edtDOB = (EditText) findViewById(R.id.edtDOB);

        btnApprove = (Button) findViewById(R.id.btnApprove);

        llMale = (LinearLayout) findViewById(R.id.llMale);
        llFemale = (LinearLayout) findViewById(R.id.llFemale);
        llMinus = (LinearLayout) findViewById(R.id.llMinus);
        llPlus = (LinearLayout) findViewById(R.id.llPlus);
        llMinus2 = (LinearLayout) findViewById(R.id.llMinus2);
        llMinus3 = (LinearLayout) findViewById(R.id.llMinus3);
        llMinus4 = (LinearLayout) findViewById(R.id.llMinus4);
        llPlus2 = (LinearLayout) findViewById(R.id.llPlus2);
        llPlus3 = (LinearLayout) findViewById(R.id.llPlus3);
        llPlus4 = (LinearLayout) findViewById(R.id.llPlus4);
        llPickImage = (LinearLayout) findViewById(R.id.llPickImage);
        llPosition = (LinearLayout) findViewById(R.id.llPosition);
        llEmployee = (LinearLayout) findViewById(R.id.llEmployee);
        llOccupier = (LinearLayout) findViewById(R.id.llOccupier);
        llTenent = (LinearLayout) findViewById(R.id.llTenent);
        llContractor = (LinearLayout) findViewById(R.id.llContractor);
        llCTenant = (LinearLayout) findViewById(R.id.llCTenant);
        llRetailer = (LinearLayout) findViewById(R.id.llRetailer);

        btnApprove.setOnClickListener(this);
        llMale.setOnClickListener(this);
        llFemale.setOnClickListener(this);
        llMinus.setOnClickListener(this);
        llPlus.setOnClickListener(this);
        llMinus2.setOnClickListener(this);
        llMinus3.setOnClickListener(this);
        llMinus4.setOnClickListener(this);
        llPlus2.setOnClickListener(this);
        llPlus3.setOnClickListener(this);
        llPlus4.setOnClickListener(this);
        llPickImage.setOnClickListener(this);
        edtDOB.setOnClickListener(this);
        edtEmpType.setOnClickListener(this);
        imgProfile = (ImageView) findViewById(R.id.imgProfile);

        Bundle bundle = getIntent().getExtras();
        edtEmail.setText(bundle.getString("email"));
        area = bundle.getString("area");
        type = bundle.getString("type");
        if (bundle.getString("area").equals("Residential") && bundle.getString("type").equals("manager")) {
            llPosition.setVisibility(View.VISIBLE);
        } else if (bundle.getString("area").equals("Residential") && bundle.getString("type").equals("owner_occupier")) {
            llOccupier.setVisibility(View.VISIBLE);
        } else if (bundle.getString("area").equals("Residential") && bundle.getString("type").equals("owner_not_occupier")) {
            llOccupier.setVisibility(View.VISIBLE);
        } else if (bundle.getString("area").equals("Residential") && bundle.getString("type").equals("tenant")) {
            llOccupier.setVisibility(View.VISIBLE);
            llTenent.setVisibility(View.VISIBLE);
        } else if (bundle.getString("area").equals("Residential") && bundle.getString("type").equals("employee")) {
            llPosition.setVisibility(View.VISIBLE);
            llEmployee.setVisibility(View.VISIBLE);
            view = (LinearLayout) findViewById(R.id.llCompany3);
        } else if (bundle.getString("area").equals("Residential") && bundle.getString("type").equals("contractor")) {
            llPosition.setVisibility(View.VISIBLE);
            llContractor.setVisibility(View.VISIBLE);
            view = (LinearLayout) findViewById(R.id.llCompany2);
            view2 = (LinearLayout) findViewById(R.id.llIndustry3);
        } else if (bundle.getString("area").equals("Commercial") && bundle.getString("type").equals("building_management")) {
            llPosition.setVisibility(View.VISIBLE);
        } else if (bundle.getString("area").equals("Commercial") && bundle.getString("type").equals("concierge")) {
            llPosition.setVisibility(View.VISIBLE);
        } else if (bundle.getString("area").equals("Commercial") && bundle.getString("type").equals("tenant")) {
            llPosition.setVisibility(View.VISIBLE);
            llCTenant.setVisibility(View.VISIBLE);
            view = (LinearLayout) findViewById(R.id.llCompany1);
            view2 = (LinearLayout) findViewById(R.id.llIndustry2);
        } else if (bundle.getString("area").equals("Commercial") && bundle.getString("type").equals("contractor")) {
            llPosition.setVisibility(View.VISIBLE);
            llContractor.setVisibility(View.VISIBLE);
            view2 = (LinearLayout) findViewById(R.id.llIndustry3);
            view = (LinearLayout) findViewById(R.id.llCompany2);
        } else if (bundle.getString("area").equals("Commercial") && bundle.getString("type").equals("employee")) {
            llPosition.setVisibility(View.VISIBLE);
            llEmployee.setVisibility(View.VISIBLE);
            view = (LinearLayout) findViewById(R.id.llCompany3);
        } else if (bundle.getString("area").equals("Retail") && bundle.getString("type").equals("center_manager")) {
            llPosition.setVisibility(View.VISIBLE);
        } else if (bundle.getString("area").equals("Retail") && bundle.getString("type").equals("contractor")) {
            llPosition.setVisibility(View.VISIBLE);
            llContractor.setVisibility(View.VISIBLE);
            view2 = (LinearLayout) findViewById(R.id.llIndustry3);
            view = (LinearLayout) findViewById(R.id.llCompany2);
        } else if (bundle.getString("area").equals("Retail") && bundle.getString("type").equals("employee")) {
            llPosition.setVisibility(View.VISIBLE);
            llEmployee.setVisibility(View.VISIBLE);
            view = (LinearLayout) findViewById(R.id.llCompany3);
        } else if (bundle.getString("area").equals("Retail") && bundle.getString("type").equals("retailer")) {
            llPosition.setVisibility(View.VISIBLE);
            llRetailer.setVisibility(View.VISIBLE);
            view2 = (LinearLayout) findViewById(R.id.llIndustry1);
        } else if (bundle.getString("area").equals("Retail") && bundle.getString("type").equals("retail_head_office")) {
            llPosition.setVisibility(View.VISIBLE);
            llRetailer.setVisibility(View.VISIBLE);
            txtShopNumber.setVisibility(View.GONE);
            txtShopLocation.setVisibility(View.GONE);
            edtShopNumber.setVisibility(View.GONE);
            edtShopLocation.setVisibility(View.GONE);
        }

        if(view != null){
            txtCompanyName = (TextView)view.findViewById(R.id.txtCompanyName);
            edtCompanyName = (EditText) view.findViewById(R.id.edtCompanyName);
        }

        if(view2 != null){
            txtIndustry = (TextView) view2.findViewById(R.id.txtIndustry);
            edtIndustry = (EditText) view2.findViewById(R.id.edtIndustry);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llMale:
                llMale.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                llFemale.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                txtM.setTextColor(ContextCompat.getColor(this, R.color.white));
                txtF.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                gender = "M";
                break;
            case R.id.llFemale:
                llMale.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                llFemale.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                txtM.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                txtF.setTextColor(ContextCompat.getColor(this, R.color.white));
                gender = "F";
                break;
            case R.id.llPlus:
                count++;
                txtCount.setText(String.valueOf(count));
                break;
            case R.id.llMinus:
                if (count > 0) {
                    count--;
                    txtCount.setText(String.valueOf(count));
                }
                break;
            case R.id.llPlus2:
                count2++;
                txtCount2.setText(String.valueOf(count2));
                break;
            case R.id.llMinus2:
                if (count2 > 0) {
                    count2--;
                    txtCount2.setText(String.valueOf(count2));
                }
                break;
            case R.id.llPlus3:
                count3++;
                txtCount3.setText(String.valueOf(count3));
                break;
            case R.id.llMinus3:
                if (count3 > 0) {
                    count3--;
                    txtCount3.setText(String.valueOf(count3));
                }
                break;
            case R.id.llPlus4:
                count4++;
                txtCount4.setText(String.valueOf(count4));
                break;
            case R.id.llMinus4:
                if (count4 > 0) {
                    count4--;
                    txtCount4.setText(String.valueOf(count4));
                }
                break;
            case R.id.llPickImage:
                selectImage();
                break;
            case R.id.edtDOB:
                new DatePickerDialog(VerifyActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.edtEmpType:
               SelectEmpType(items);
                break;
            case R.id.btnApprove:
                email = edtEmail.getText().toString();
                fname = edtFname.getText().toString();
                lname = edtLName.getText().toString();
                phone = edtPhone.getText().toString();
                position = edtPosition.getText().toString();
                DOB = edtDOB.getText().toString();
                address = edtAddress.getText().toString();
                aptNo = edtAptNo.getText().toString();
                peopleCount = txtCount.getText().toString();
                bedCount = txtCount2.getText().toString();
                bathCount = txtCount3.getText().toString();
                carSpace = txtCount4.getText().toString();
                carReg = edtCarReg.getText().toString();
                ename = edtEName.getText().toString();
                eno = edtENo.getText().toString();
                pcname = edtPCName.getText().toString();
                pcno = edtPCNo.getText().toString();
                agentname = edtAgentName.getText().toString();
                agentno = edtAgentNo.getText().toString();
                if(edtIndustry != null){
                    industry = edtIndustry.getText().toString();
                }

                if(edtCompanyName != null){
                    companyName = edtCompanyName.getText().toString();
                }

                empNo = edtEmpNo.getText().toString();
                empType = edtEmpType.getText().toString();
                verifyAccount(VerifyActivity.this);
                break;
        }
    }

    public void SelectEmpType(final CharSequence[] items) {
        AlertDialog dialog =  new AlertDialog.Builder(this)

                .setSingleChoiceItems(items, 0, null)
                .setPositiveButton(R.string.ok_button_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        String ptype = ((AlertDialog) dialog).getListView().getItemAtPosition(selectedPosition).toString();
                        edtEmpType.setText(ptype);
                    }
                })
                .show();
    }

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edtDOB.setText(sdf.format(myCalendar.getTime()));
    }

    private boolean checkandRequestPermission() {
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int storageread = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int storagewrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listpermissionNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listpermissionNeeded.add(Manifest.permission.CAMERA);
        }
        if (storageread != PackageManager.PERMISSION_GRANTED) {
            listpermissionNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (storagewrite != PackageManager.PERMISSION_GRANTED) {
            listpermissionNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listpermissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(VerifyActivity.this, listpermissionNeeded.toArray(new String[listpermissionNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void selectImage() {
        final CharSequence[] options = {"Camera", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(VerifyActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Camera")) {
                    isCamera = 1;
                    if (checkandRequestPermission()) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            fileUri = FileProvider.getUriForFile(VerifyActivity.this, getPackageName() + ".provider", getOutputMediaFile(MEDIA_TYPE_IMAGE));
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        } else {
                            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        }
                        startActivityForResult(intent, 1);
                    }

                } else if (options[item].equals("Choose from Gallery")) {
                    isCamera = 0;
                    if (checkandRequestPermission()) {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2);
                    }
                }
            }
        });
        builder.show();
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "Stratafy");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("FileName::", "Oops! Failed create "
                        + "Stratafy" + " directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        mCurrentPhotoPath = mediaFile.getAbsolutePath();
        return mediaFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    if ((ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                            && (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                            && (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                        if (isCamera == 1) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                fileUri = FileProvider.getUriForFile(VerifyActivity.this, getPackageName() + ".provider", getOutputMediaFile(MEDIA_TYPE_IMAGE));
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                            } else {
                                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                            }
                            startActivityForResult(intent, 1);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 2);
                        }
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Log.d("myPath1:", mCurrentPhotoPath);
                Log.d("myPath2:", fileUri.getPath());
                try {
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        bitmap1 = BitmapFactory.decodeFile(mCurrentPhotoPath, bitmapOptions);
                        txtTap.setVisibility(View.GONE);
                        imgProfile.setVisibility(View.VISIBLE);
                        Glide.with(VerifyActivity.this).load(mCurrentPhotoPath).into(imgProfile);
                    } else {
                        txtTap.setVisibility(View.GONE);
                        imgProfile.setVisibility(View.VISIBLE);
                        bitmap1 = BitmapFactory.decodeFile(fileUri.getPath(), bitmapOptions);
                        Glide.with(VerifyActivity.this).load(fileUri.getPath()).into(imgProfile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                fileUri = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(fileUri, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                imagePath = c.getString(columnIndex);
                c.close();
                bitmap1 = (BitmapFactory.decodeFile(imagePath));

                txtTap.setVisibility(View.GONE);
                imgProfile.setVisibility(View.VISIBLE);
                Glide.with(VerifyActivity.this).load(fileUri).into(imgProfile);

            } else if (requestCode == CROP_FROM_CAMERA) {
                fileUri = data.getData();
                txtTap.setVisibility(View.GONE);
                imgProfile.setVisibility(View.VISIBLE);
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    Glide.with(VerifyActivity.this).load(photo).into(imgProfile);
                }
            }
        }
    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();

            return;
        } else {
            intent.setData(fileUri);
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);

                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    cropOptions.add(co);
                }
                CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Choose Crop App");

                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (fileUri != null) {
                            getContentResolver().delete(fileUri, null, null);
                            fileUri = null;
                        }
                    }
                });

                android.app.AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private void verifyAccount(final Context context) {
        String tag_string_req = "req_login";
        mProgressBar.setVisibility(View.VISIBLE);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                Glob.API_EDIT_PROFILE +  uid + "/" +property_id + ".json",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String resultResponse = new String(response.data);

                        Log.d("ResultResponce:", resultResponse);
                        try {
                            JSONObject jObj = new JSONObject(resultResponse);
                            int status = jObj.getInt("status");
                            final String errorMsg = jObj.getString("errorMsg");
                            if (status != 0) {
                                mProgressBar.setVisibility(View.GONE);
                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(context, MainActivity.class);
                                startActivity(intent);
                                finish();
                                session.setLogin(true);
                            } else {
                                mProgressBar.setVisibility(View.GONE);
                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (final JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                error.printStackTrace();
                Log.e("TAG", "Login Error: " + error.getMessage());
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(context, "Json error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                /*if(!description.equals("null") && !description.isEmpty()){
                    params.put("description", description);
                }*/

                params.put("data[User][email]", email);
                params.put("data[User][first_name]", fname);
                params.put("data[User][last_name]", lname);
                params.put("data[User][contact_num]", phone);
                params.put("data[User][position]", position);
                params.put("data[User][dob]", DOB);
                params.put("data[User][address]", address);
                params.put("data[User][apt_number]", aptNo);
                params.put("data[User][gender]", gender);
                params.put("data[User][no_of_people]", peopleCount);
                params.put("data[User][emg_contact_name]", ename);
                params.put("data[User][emg_contact_number]", eno);
                params.put("data[User][bedrooms]", bedCount);
                params.put("data[User][bathrooms]", bathCount);
                params.put("data[User][carspace]", carSpace);
                params.put("data[User][key_contact_name]", pcname);
                params.put("data[User][key_contact_number]", pcno);
                params.put("data[User][landlord_name]", agentname);
                params.put("data[User][landlord_number]", agentno);
                params.put("data[User][company_name]", companyName);
                params.put("data[User][industry]", industry);
                params.put("data[User][employee_number]", empNo);
                params.put("data[User][car_rego]", carReg);
                params.put("data[User][employement_type]", empType);

                Log.d("Param:", params.toString());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if(bitmap1 != null){
                    params.put("data[User][phpto]", new VolleyMultipartRequest.DataPart("something.jpg",
                            AppHelper.getFileDataFromDrawable(bitmap1), "image/jpeg"));
                }
                Log.d("ParamImage:", params.toString());
                return params;
            }
        };
        MyApplication.getInstance().addToRequestQueue(multipartRequest, tag_string_req);
    }
}
