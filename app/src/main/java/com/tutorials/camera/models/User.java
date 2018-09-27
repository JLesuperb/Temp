package com.tutorials.camera.models;

import com.google.gson.annotations.SerializedName;

public class User
{
    @SerializedName("UserId")
    private Long userId;

    @SerializedName("UserName")
    private String userName;

    @SerializedName("UserPass")
    private  String userPass;

    public User(String userName, String userPass)
    {
        this.userName = userName;
        this.userPass = userPass;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPass() {
        return userPass;
    }
}
