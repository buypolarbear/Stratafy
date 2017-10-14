package com.stratafy.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.stratafy.R;
import com.stratafy.helper.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000;
    private SessionManager session;
    private ImageView imgSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        session = new SessionManager(this);
        imgSplash = (ImageView)findViewById(R.id.imgSplash);
        Glide.with(this).load(R.drawable.splash)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imgSplash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(session.isLoggedIn()){
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                   // overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }else {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                  //  overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }

            }
        }, SPLASH_TIME_OUT);
    }
}
