package com.pixeldart.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dpizarro.uipicker.library.picker.PickerUI;
import com.dpizarro.uipicker.library.picker.PickerUISettings;
import com.pixeldart.R;
import com.pixeldart.helper.Glob;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateLogActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtTitle, edtCategory, edtNotes;
    private TextView txtDetails;
    private Button btnSubmit;
    private Intent intent;
    private PickerUI mPickerUI;
    private FloatingActionButton btnCamera;


    private List<String> options;
    private int currentPosition = -1;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    int isCamera = 1;
    String mCurrentPhotoPath;
    Bitmap bitmap1;
    String imagePath;
    boolean isProfileUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_log);

        options = Arrays.asList(getResources().getStringArray(R.array.notes));

        initialization(this);
    }

    private void initialization(CreateLogActivity context) {
        mPickerUI = (PickerUI) findViewById(R.id.picker_ui_view);
        edtTitle = (EditText)findViewById(R.id.edtTitle);
        edtCategory = (EditText)findViewById(R.id.edtCategory);
        edtNotes = (EditText)findViewById(R.id.edtNotes);

        edtCategory.setOnClickListener(this);
        edtNotes.setOnClickListener(this);

        edtTitle.setTypeface(Glob.avenir(context));
        edtCategory.setTypeface(Glob.avenir(context));
        edtNotes.setTypeface(Glob.avenir(context));

        txtDetails = (TextView)findViewById(R.id.txtDetails);
        txtDetails.setTypeface(Glob.avenir(context));

        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnSubmit.setTypeface(Glob.avenir(context));

        btnCamera = (FloatingActionButton)findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(this);

        mPickerUI.setOnClickItemPickerUIListener(
                new PickerUI.PickerUIItemClickListener() {

                    @Override
                    public void onItemClickPickerUI(int which, int position, String valueResult) {
                        currentPosition = position;
                        edtNotes.setText(valueResult);
                        mPickerUI.slide(position);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnSubmit:

                break;
            case R.id.edtCategory:

                break;
            case R.id.edtNotes:
                openPicker();
                break;
            case R.id.btnCamera:
                selectImage();
                break;

        }
    }

    public void openPicker(){
        mPickerUI.setItems(this, options);
        PickerUISettings pickerUISettings =
                new PickerUISettings.Builder().withItems(options)
                        .withBackgroundColor(R.color.white)
                        .withAutoDismiss(false)
                        .withItemsClickables(false)
                        .withUseBlur(false)
                        .build();
        mPickerUI.setSettings(pickerUISettings);
        if(currentPosition==-1) {
            mPickerUI.slide();
        }
        else{
            mPickerUI.slide(currentPosition);
        }
    }

    private void selectImage() {
        final CharSequence[] options = {"Camera", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                            fileUri = FileProvider.getUriForFile(CreateLogActivity.this, getPackageName() + ".provider", getOutputMediaFile(MEDIA_TYPE_IMAGE));
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
                    } else {
                        bitmap1 = BitmapFactory.decodeFile(fileUri.getPath(), bitmapOptions);
                    }
                   // imgProfile.setImageBitmap(bitmap1);
                  //  isProfileUpdate = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                imagePath = c.getString(columnIndex);
                c.close();
                bitmap1 = (BitmapFactory.decodeFile(imagePath));
              //  imgProfile.setImageBitmap(bitmap1);
               // isProfileUpdate = true;
            }
        }
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
            ActivityCompat.requestPermissions(this, listpermissionNeeded.toArray(new String[listpermissionNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
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
                                fileUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", getOutputMediaFile(MEDIA_TYPE_IMAGE));
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
}
