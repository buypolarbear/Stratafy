package com.stratafy.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cn on 8/21/2017.
 */

public class Laws implements Parcelable {
    String law_name, law_text;

    public Laws(){}

    protected Laws(Parcel in) {
        law_name = in.readString();
        law_text = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(law_name);
        dest.writeString(law_text);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Laws> CREATOR = new Creator<Laws>() {
        @Override
        public Laws createFromParcel(Parcel in) {
            return new Laws(in);
        }

        @Override
        public Laws[] newArray(int size) {
            return new Laws[size];
        }
    };

    public String getLaw_name() {
        return law_name;
    }

    public void setLaw_name(String law_name) {
        this.law_name = law_name;
    }

    public String getLaw_text() {
        return law_text;
    }

    public void setLaw_text(String law_text) {
        this.law_text = law_text;
    }
}
