package com.stratafy.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cn on 8/28/2017.
 */

public class Notifications implements Parcelable {
    private String message;

    public Notifications(){}

    protected Notifications(Parcel in) {
        message = in.readString();
    }

    public static final Creator<Notifications> CREATOR = new Creator<Notifications>() {
        @Override
        public Notifications createFromParcel(Parcel in) {
            return new Notifications(in);
        }

        @Override
        public Notifications[] newArray(int size) {
            return new Notifications[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
    }
}
