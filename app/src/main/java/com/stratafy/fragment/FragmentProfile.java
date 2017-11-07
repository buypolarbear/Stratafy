package com.stratafy.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.stratafy.R;
import com.stratafy.activities.MainActivity;
import com.stratafy.helper.Glob;
import com.stratafy.helper.MyApplication;
import com.stratafy.model.AroundPlaces;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Created by cn on 8/14/2017.
 */

public class FragmentProfile extends Fragment implements View.OnClickListener {

    private TextView txtName,  txtGeneral, txtOther, txtLink, txtTake_time, txtVerify_you, txtTap, txtProfile_pic, txtFname,
            txtLName, txtEmail, txtPhone, txtM, txtF, txtPosition, txtDOB, txtGender, txtAddress, txtAddresses,
            txtAptNo, txtPeoples, txtCount, txtCount2, txtCount3, txtCount4, txtFloor,
            txtBedrooms, txtBathrooms, txtCars, txtCarNo, txtPCName, txtPCNo, txtEName, txtENo, txtAgentName, txtAgentNo,
            txtCompanyName, txtCarReg, txtEmpNo, txtEmpType, txtkeyName, txtIndustry, txtShopName,
            txtShopLocation, txtShopNumber;


    private ImageView imgProfile, imgBackground;
    private LinearLayout llFirst, llSecond, llThird;
    private LinearLayout llLine1, llLine2, llLine3;
    private ProgressBar mProgressBar;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    int isCamera = 1;
    String mCurrentPhotoPath;
    Bitmap bitmap1;
    String imagePath;

    private Button btnApprove;

    private EditText edtFname, edtLName, edtEmail, edtPhone, edtAddress, edtAptNo, edtCarNo, edtPCName, edtPCNo, edtEName,
            edtENo, edtAgentName, edtAgentNo, edtCompanyName, edtCarReg, edtEmpNo, edtEmpType, edtKeyName,
            edtIndustry, edtPosition, edtFloor, edtShopName, edtShopLocation, edtShopNumber,
            edtDOB;

    private static final String EXTRA_TEXT = "text";

    public static FragmentProfile instance(String text) {
        FragmentProfile fragment = new FragmentProfile();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initialization(view);
        return view;
    }

    private void initialization(View view) {
        MainActivity.txtToolbarTitle.setVisibility(View.VISIBLE);
        MainActivity.txtToolbarTitle.setText(getResources().getString(R.string.profile));
        MainActivity.imgStreaming.setVisibility(View.GONE);

        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressbar);

        txtName = (TextView)view.findViewById(R.id.txtName);
        txtAddresses = (TextView)view.findViewById(R.id.txtAddresses);
        txtGeneral = (TextView)view.findViewById(R.id.txtGenral);
        txtOther = (TextView)view.findViewById(R.id.txtOther);
        txtLink = (TextView)view.findViewById(R.id.txtLink);
        txtFname = (TextView) view.findViewById(R.id.txtFname);
        txtLName = (TextView) view.findViewById(R.id.txtLName);
        txtEmail = (TextView) view.findViewById(R.id.txtEmail);
        txtPhone = (TextView) view.findViewById(R.id.txtPhone);

        edtFname = (EditText) view.findViewById(R.id.edtFname);
        edtLName = (EditText) view.findViewById(R.id.edtLName);
        edtEmail = (EditText) view.findViewById(R.id.edtEmail);
        edtPhone = (EditText) view.findViewById(R.id.edtPhone);

        btnApprove = (Button) view.findViewById(R.id.btnApprove);

        imgProfile = (ImageView)view.findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(this);
        imgBackground = (ImageView)view.findViewById(R.id.imgBackground);

        llFirst = (LinearLayout)view.findViewById(R.id.llFirst);
        llSecond = (LinearLayout)view.findViewById(R.id.llSecond);
        llThird = (LinearLayout)view.findViewById(R.id.llThird);

        llLine1 = (LinearLayout)view.findViewById(R.id.llLine1);
        llLine2 = (LinearLayout)view.findViewById(R.id.llLine2);
        llLine3 = (LinearLayout)view.findViewById(R.id.llLine3);

        llFirst.setOnClickListener(this);
        llSecond.setOnClickListener(this);
        llThird.setOnClickListener(this);

        getProfile();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llFirst:
                llLine1.setVisibility(View.VISIBLE);
                llLine2.setVisibility(View.GONE);
                llLine3.setVisibility(View.GONE);
                break;
            case R.id.llSecond:
                llLine1.setVisibility(View.GONE);
                llLine2.setVisibility(View.VISIBLE);
                llLine3.setVisibility(View.GONE);
                break;
            case R.id.llThird:
                llLine1.setVisibility(View.GONE);
                llLine2.setVisibility(View.GONE);
                llLine3.setVisibility(View.VISIBLE);
                break;
            case R.id.imgProfile:
                selectImage();
                break;
        }
    }

    public static void longLog(String str, String log) {
        if (str.length() > 4000) {
            Log.d(log, str.substring(0, 4000));
            longLog(str.substring(4000), log);
        } else
            Log.d(log, str);
    }

    private void getProfile() {
        mProgressBar.setVisibility(View.VISIBLE);
        final String url = Glob.API_GET_USER + MainActivity.uid + ".json";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                longLog(response, "Places_response");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int status = jObj.getInt("status");
                    final String errorMsg = jObj.getString("errorMsg");
                    Glide.with(getActivity()).load(jObj.getString("image")).into(imgBackground);
                    if (status != 0) {
                        mProgressBar.setVisibility(View.GONE);
                        JSONObject user = jObj.getJSONObject("data").getJSONObject("userDetail");
                        if(user.getString("last_name") != null && !user.getString("last_name").isEmpty()){
                            txtName.setText(user.getString("first_name") + " " + user.getString("last_name"));
                            edtFname.setText(user.getString("first_name"));
                            edtLName.setText(user.getString("last_name"));
                        }else {
                            edtFname.setText(user.getString("first_name"));
                            txtName.setText(user.getString("first_name"));
                        }

                        edtEmail.setText(user.getString("email"));
                        edtPhone.setText(user.getString("contact_num"));

                        if(user.getString("address") != null && !user.getString("address").isEmpty()){
                            txtAddresses.setText(user.getString("address"));
                        }

                        if(user.getString("avatar") != null && !user.getString("avatar").isEmpty()){
                            Glide.with(getActivity()).load(jObj.getString("avatar")).into(imgProfile);
                        }
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                Log.e("TAG", "Law_error: " + error.getMessage());
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private void selectImage() {
        final CharSequence[] options = {"Camera", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                            fileUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", getOutputMediaFile(MEDIA_TYPE_IMAGE));
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Log.d("myPath1:", mCurrentPhotoPath);
                Log.d("myPath2:", fileUri.getPath());
                try {
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        bitmap1 = BitmapFactory.decodeFile(mCurrentPhotoPath, bitmapOptions);
                    } else {
                        bitmap1 = BitmapFactory.decodeFile(fileUri.getPath(), bitmapOptions);
                    }
                    imgProfile.setImageBitmap(bitmap1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                imagePath = c.getString(columnIndex);
                c.close();
                bitmap1 = (BitmapFactory.decodeFile(imagePath));
                imgProfile.setImageBitmap(bitmap1);
            }
        }
    }

    private boolean checkandRequestPermission() {
        int camera = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        int storageread = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int storagewrite = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

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
            ActivityCompat.requestPermissions(getActivity(), listpermissionNeeded.toArray(new String[listpermissionNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "Stratafy");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("FileName::", "Oops! Failed create "
                        + "InstructiveRide" + " directory");
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
                    if ((ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                            && (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                            && (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                        if (isCamera == 1) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                fileUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", getOutputMediaFile(MEDIA_TYPE_IMAGE));
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
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
