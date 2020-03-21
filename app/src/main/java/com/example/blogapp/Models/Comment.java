package com.example.blogapp.Models;

import com.google.firebase.database.ServerValue;

public class Comment {
    private String Content, uId, uImg, uName;
    private Object timeStamp;//fecha

    public Comment() {
    }

    public Comment(String content, String uId, String uImg, String uName) {
        Content = content;
        this.uId = uId;
        this.uImg = uImg;
        this.uName = uName;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public Comment(String content, String uId, String uImg, String uName, Object timeStamp) {
        Content = content;
        this.uId = uId;
        this.uImg = uImg;
        this.uName = uName;
        this.timeStamp = timeStamp;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getuImg() {
        return uImg;
    }

    public void setuImg(String uImg) {
        this.uImg = uImg;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
