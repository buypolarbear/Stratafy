package com.stratafy.helper;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.stratafy.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDialogEvent extends Dialog implements
    android.view.View.OnClickListener {

  public Activity c;
  public Dialog d;
  public TextView txtName, txtEventName, txtEventDetail, txtEventDate, txtDate, txtEventTime, txtTime, txtCancel, txtAttending;
  public LinearLayout llCancel, llAttending;
  private ImageView imgAvatar, imgClose;
  
  public String avatar, fname, lname, eventname, detail, date, sTime, eTime, id;
  SimpleDateFormat sdf_set = new SimpleDateFormat("dd MMM yyyy");
  SimpleDateFormat sdf_get = new SimpleDateFormat("yyyy-MM-dd");

  public CustomDialogEvent(Activity a, String id, String avatar, String fname, String lname, String eventname, String detail, String date,
                           String sTime, String eTime) {
    super(a);
    // TODO Auto-generated constructor stub
    this.c = a;
    this.avatar = avatar;
    this.fname = fname;
    this.lname = lname;
    this.eventname = eventname;
    this.detail = detail;
    this.date = date;
    this.sTime = sTime;
    this.eTime = eTime;
    this.id = id;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.dialog_event);
    
    llCancel = (LinearLayout)findViewById(R.id.llCancel);
    llAttending = (LinearLayout)findViewById(R.id.llAttending);
    imgClose = (ImageView)findViewById(R.id.imgClose);
    
    imgAvatar = (ImageView)findViewById(R.id.imgAvatar);

    Log.d("avatar:", String.valueOf(avatar));
    if(avatar != null && !avatar.isEmpty()){
      Glide.with(c).load(avatar).into(imgAvatar);
    }

    llCancel.setOnClickListener(this);
    llAttending.setOnClickListener(this);
    imgClose.setOnClickListener(this);

    txtName = (TextView)findViewById(R.id.txtName);
    if(lname != null && !lname.isEmpty()){
      txtName.setText(fname + " " + lname);
    }else {
      txtName.setText(fname);
    }


    txtEventName = (TextView)findViewById(R.id.txtEventName);
    txtEventName.setText(eventname);

    txtEventDetail = (TextView)findViewById(R.id.txtEventDetail);

    if(detail != null && !detail.isEmpty()){
      txtEventDetail.setText(detail);
    }else {
      txtEventDetail.setVisibility(View.GONE);
    }

    txtEventDate = (TextView)findViewById(R.id.txtEventDate);
    txtDate = (TextView)findViewById(R.id.txtDate);

    try {
      Date dates = sdf_get.parse(date);
      String output = sdf_set.format(dates);
      txtDate.setText(output);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    txtEventTime = (TextView)findViewById(R.id.txtEventTime);
    txtTime = (TextView)findViewById(R.id.txtTime);
    txtTime.setText(sTime + " - " + eTime);
    txtCancel = (TextView)findViewById(R.id.txtCancel);
    txtAttending = (TextView)findViewById(R.id.txtAttending);


  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.llCancel:
      dismiss();
      break;
      case R.id.imgClose:
        dismiss();
        break;
    case R.id.llAttending:
      
      break;
    default:
      break;
    }
    dismiss();
  }
}