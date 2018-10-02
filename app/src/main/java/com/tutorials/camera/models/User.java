package com.tutorials.camera.models;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "TUsers")
public class User
{
    @Id()
    @Property(nameInDb = "UserId")
    @SerializedName("UserId")
    private Long userId;

    @Property(nameInDb = "UserName")
    @SerializedName("UserName")
    private String userName;

    @Property(nameInDb = "UserPass")
    @SerializedName("UserPass")
    private  String userPass;

    @Property(nameInDb = "BranchFId")
    @SerializedName("BranchFId")
    private Long branchId;

    @Property(nameInDb = "AccessToken")
    @SerializedName("AccessToken")
    private String token;

    @Generated(hash = 1410854837)
    public User(Long userId, String userName, String userPass, Long branchId,
            String token) {
        this.userId = userId;
        this.userName = userName;
        this.userPass = userPass;
        this.branchId = branchId;
        this.token = token;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return this.userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public Long getBranchId() {
        return this.branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
