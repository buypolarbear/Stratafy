package com.pixeldart.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dpizarro.uipicker.library.picker.PickerUI;
import com.dpizarro.uipicker.library.picker.PickerUISettings;
import com.pixeldart.R;
import com.pixeldart.activities.MainActivity;
import com.pixeldart.helper.AppHelper;
import com.pixeldart.helper.Glob;
import com.pixeldart.helper.MyApplication;
import com.pixeldart.helper.VolleyMultipartRequest;
import com.pixeldart.service.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class FragmentCreateLog extends Fragment implements View.OnClickListener {

    private EditText edtTitle, edtCategory, edtNotes, edtDetail;
    private TextView txtDetails;
    private Button btnSubmit;
    private Intent intent;
    private PickerUI mPickerUI;
    private FloatingActionButton btnCamera;


    private List<String> options = new ArrayList<>();
    private List<String> option2 = new ArrayList<>();
    List<String> id = new ArrayList<>();
    private int currentPosition = -1;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    int isCamera = 1;
    String mCurrentPhotoPath;
    Bitmap bitmap1;
    String imagePath;
    boolean isProfileUpdate = false , isCategory = false;
    RelativeLayout llRoot, llRoot2;

    private FloatingActionButton btnClose;
    ImageView img;

    private ProgressBar mProgressBar;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String property_id, cat_id;
    private int uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_create_log, container, false);
        MainActivity.txtToolbarTitle.setVisibility(View.VISIBLE);
        MainActivity.txtToolbarTitle.setText("Create New");
        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressbar);

        pref = getActivity().getSharedPreferences(Config.SHARED_PREF, 0);
        editor = pref.edit();
        property_id = pref.getString("property_id", null);
        uid = pref.getInt("uid", 0);

        options = Arrays.asList(getResources().getStringArray(R.array.notes));

        initialization(getActivity(), view);
        getLogs();

        return view;
    }

    private void initialization(Context context, View view) {
        llRoot = (RelativeLayout) view.findViewById(R.id.llRoot);
        llRoot2 = (RelativeLayout) view.findViewById(R.id.llRoot2);

        btnClose = (FloatingActionButton) view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);
        img = (ImageView) view.findViewById(R.id.img);

        mPickerUI = (PickerUI) view.findViewById(R.id.picker_ui_view);
        edtTitle = (EditText) view.findViewById(R.id.edtTitle);
        edtCategory = (EditText) view.findViewById(R.id.edtCategory);
        edtNotes = (EditText) view.findViewById(R.id.edtNotes);
        edtDetail = (EditText) view.findViewById(R.id.edtDetail);

        edtCategory.setOnClickListener(this);
        edtNotes.setOnClickListener(this);

        edtDetail.setTypeface(Glob.avenir(context));
        edtTitle.setTypeface(Glob.avenir(context));
        edtCategory.setTypeface(Glob.avenir(context));
        edtNotes.setTypeface(Glob.avenir(context));

        txtDetails = (TextView) view.findViewById(R.id.txtDetails);
        txtDetails.setTypeface(Glob.avenir(context));

        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnSubmit.setTypeface(Glob.avenir(context));
        btnSubmit.setOnClickListener(this);

        btnCamera = (FloatingActionButton) view.findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(this);

        mPickerUI.setOnClickItemPickerUIListener(
                new PickerUI.PickerUIItemClickListener() {

                    @Override
                    public void onItemClickPickerUI(int which, int position, String valueResult) {
                        currentPosition = position;
                        mPickerUI.slide(position);
                        Log.d("Cat_id", id.get(position));
                        cat_id = id.get(position);
                        if(isCategory){
                            edtCategory.setText(valueResult);
                        }else {
                            edtNotes.setText(valueResult);
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnSubmit:
                if(validation()){
                    String title = edtTitle.getText().toString();
                    String notes = edtNotes.getText().toString();
                    String description = edtDetail.getText().toString();
                    postLog(getActivity(), cat_id, title, description, notes);
                }
                break;
            case R.id.edtCategory:
                isCategory = true;
                openPicker2();
                break;
            case R.id.edtNotes:
                isCategory = false;
                openPicker();
                break;
            case R.id.btnCamera:
                if (isProfileUpdate) {
                    llRoot.setVisibility(View.GONE);
                    llRoot2.setVisibility(View.VISIBLE);
                } else {
                    selectImage();
                }

                break;
            case R.id.btnClose:
                llRoot2.setVisibility(View.GONE);
                llRoot.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void openPicker2( ) {
        mPickerUI.setItems(getActivity(), option2);
        PickerUISettings pickerUISettings =
                new PickerUISettings.Builder().withItems(option2)
                        .withBackgroundColor(R.color.white)
                        .withAutoDismiss(false)
                        .withItemsClickables(false)
                        .withUseBlur(false)
                        .build();
        mPickerUI.setSettings(pickerUISettings);
        if (currentPosition == -1) {
            mPickerUI.slide();
        } else {
            mPickerUI.slide(currentPosition);
        }
    }

    public void openPicker( ) {
        mPickerUI.setItems(getActivity(), options);
        PickerUISettings pickerUISettings =
                new PickerUISettings.Builder().withItems(options)
                        .withBackgroundColor(R.color.white)
                        .withAutoDismiss(false)
                        .withItemsClickables(false)
                        .withUseBlur(false)
                        .build();
        mPickerUI.setSettings(pickerUISettings);
        if (currentPosition == -1) {
            mPickerUI.slide();
        } else {
            mPickerUI.slide(currentPosition);
        }
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
        //  super.onActivityResult(requestCode, resultCode, data);
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
                    img.setImageBitmap(bitmap1);
                    isProfileUpdate = true;
                    btnCamera.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo));
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
                img.setImageBitmap(bitmap1);
                isProfileUpdate = true;
                btnCamera.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo));
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

    public static void longLog(String str, String log) {
        if (str.length() > 4000) {
            Log.d(log, str.substring(0, 4000));
            longLog(str.substring(4000), log);
        } else
            Log.d(log, str);
    }

    private void getLogs() {
        mProgressBar.setVisibility(View.VISIBLE);
        String tag_string_req = "req_login";
        String url = Glob.API_GET_LOG + property_id + ".json?user_id="+uid;
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                longLog(response, "Low_response");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int status = jObj.getInt("status");
                    final String errorMsg = jObj.getString("errorMsg");
                    if (status != 0) {
                        mProgressBar.setVisibility(View.GONE);
                        JSONArray category = jObj.getJSONArray("categories");
                        for (int i = 0; i < category.length(); i++) {
                            JSONObject r = category.getJSONObject(i);
                            option2.add(i, r.getString("name"));
                            id.add(i, r.getString("id"));
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
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void postLog(final Context context, final String cat_id,
                         final String title, final String description, final String note) {
        String tag_string_req = "req_login";
        mProgressBar.setVisibility(View.VISIBLE);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                Glob.API_POST_LOG + property_id + ".json",
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
                        getActivity().onBackPressed();
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
                params.put("user_id", String.valueOf(uid));
                params.put("log_category_id", cat_id);
                params.put("title", title);
                if(!description.equals("null") && !description.isEmpty()){
                    params.put("description", description);
                }
                params.put("private", note);

                Log.d("Param:", params.toString());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if(bitmap1 != null){
                    params.put("photo", new VolleyMultipartRequest.DataPart("something.jpg",
                            AppHelper.getFileDataFromDrawable(bitmap1), "image/jpeg"));
                }
                Log.d("ParamImage:", params.toString());
                return params;
            }
        };
        MyApplication.getInstance().addToRequestQueue(multipartRequest, tag_string_req);
    }

    public boolean validation() {
        if (edtTitle.getText().toString().trim().isEmpty()) {
            edtTitle.setError("Title is empty");
            return false;
        }
        else if (edtCategory.getText().toString().trim().isEmpty()) {
            edtCategory.setError("Add Category");
            return false;
        }
        else if (edtNotes.getText().toString().trim().isEmpty()) {
            edtNotes.setError("Add Notes");
            return false;
        }
        else {
            return true;
        }
    }
}
