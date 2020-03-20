package com.example.blogapp.Models;

import com.google.firebase.database.ServerValue;

public class Post {

    private String sTittle, sDecription, sPicture, sUserId, sUserPhoto, sPostKey;
    private Object timeStamp;

    public Post(String sTittle, String sDecription, String sPicture, String sUserId, String sUserPhoto) {
        this.sTittle = sTittle;
        this.sDecription = sDecription;
        this.sPicture = sPicture;
        this.sUserId = sUserId;
        this.sUserPhoto = sUserPhoto;
        this.timeStamp = ServerValue.TIMESTAMP;//conexion con firebase
    }

    public Post() {
    }

    //getter
    public String getsTittle() {
        return sTittle;
    }

    public String getsPostKey() {
        return sPostKey;
    }

    public void setsPostKey(String sPostKey) {
        this.sPostKey = sPostKey;
    }

    public String getsDecription() {
        return sDecription;
    }

    public String getsPicture() {
        return sPicture;
    }

    public String getsUserId() {
        return sUserId;
    }

    public String getsUserPhoto() {
        return sUserPhoto;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    //setter

    public void setsTittle(String sTittle) {
        this.sTittle = sTittle;
    }

    public void setsDecription(String sDecription) {
        this.sDecription = sDecription;
    }

    public void setsPicture(String sPicture) {
        this.sPicture = sPicture;
    }

    public void setsUserId(String sUserId) {
        this.sUserId = sUserId;
    }

    public void setsUserPhoto(String sUserPhoto) {
        this.sUserPhoto = sUserPhoto;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
