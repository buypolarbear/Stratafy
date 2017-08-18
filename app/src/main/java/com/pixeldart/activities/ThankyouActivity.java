package com.pixeldart.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pixeldart.R;

public class ThankyouActivity extends AppCompatActivity {

    private ImageView imgThank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankyou);

        imgThank = (ImageView)findViewById(R.id.imgThank);
        Glide.with(this).load(R.drawable.success_screen).into(imgThank);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
