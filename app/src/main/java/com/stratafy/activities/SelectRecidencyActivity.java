package com.stratafy.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.stratafy.R;
import com.stratafy.helper.Glob;

public class SelectRecidencyActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtResidetial, txtCommercial, txtRetail;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_recidency);

        initialization(this);

    }

    private void initialization(SelectRecidencyActivity context) {
        txtResidetial = (TextView)findViewById(R.id.txtResidential);
        txtCommercial = (TextView)findViewById(R.id.txtCommmercial);
        txtRetail = (TextView)findViewById(R.id.txtRetail);

        txtResidetial.setTypeface(Glob.avenir(context));
        txtCommercial.setTypeface(Glob.avenir(context));
        txtRetail.setTypeface(Glob.avenir(context));

        txtResidetial.setOnClickListener(this);
        txtCommercial.setOnClickListener(this);
        txtRetail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.txtResidential:
                intent = new Intent(SelectRecidencyActivity.this, SignupActivity.class);
                intent.putExtra("type", "Residential");
                startActivity(intent);
                finish();
                break;
            case R.id.txtCommmercial:
                intent = new Intent(SelectRecidencyActivity.this, SignupActivity.class);
                intent.putExtra("type", "Commercial");
                startActivity(intent);
                finish();
                break;
            case R.id.txtRetail:
                intent = new Intent(SelectRecidencyActivity.this, SignupActivity.class);
                intent.putExtra("type", "Retail");
                startActivity(intent);
                finish();
                break;
        }
    }
}
