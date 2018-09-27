package com.tutorials.camera.models;

import com.google.gson.annotations.SerializedName;

public class Token
{
    @SerializedName("AccessToken")
    private String tokenString;

    @SerializedName("message")
    private String message;

    @SerializedName("UserName")
    private String userName;

    @SerializedName("status")
    private String status;

    public String getTokenString() {
        return tokenString;
    }

    public void setTokenString(String tokenString) {
        this.tokenString = tokenString;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
