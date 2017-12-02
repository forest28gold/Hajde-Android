package com.azizinetwork.hajde.model.parse;

import android.net.Uri;

public class InviteFriendData {

    private String userName;
    private String phoneNumber;
    private Uri photoUri;
    private boolean selectIsOn;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isSelectIsOn() {
        return selectIsOn;
    }

    public void setSelectIsOn(boolean selectIsOn) {
        this.selectIsOn = selectIsOn;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }
}
