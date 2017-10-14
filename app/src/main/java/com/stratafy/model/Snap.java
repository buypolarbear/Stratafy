package com.stratafy.model;

import java.util.List;

public class Snap {

    private String mText;
    private List<Contact> mApps;

    public Snap(String text, List<Contact> apps) {
        mText = text;
        mApps = apps;
    }

    public String getText(){
        return mText;
    }

    public List<Contact> getApps(){
        return mApps;
    }

}