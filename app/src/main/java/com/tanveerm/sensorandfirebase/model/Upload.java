package com.tanveerm.sensorandfirebase.model;

public class Upload {

    private String mImageUrl;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Upload() {
    }

    public Upload(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
}
