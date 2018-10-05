package com.tutorials.camera.models;

import com.google.gson.annotations.SerializedName;

public class Token
{
    @SerializedName("UserId")
    private Long userId;

    @SerializedName("AccessToken")
    private String tokenString;

    @SerializedName("message")
    private String message;

    @SerializedName("UserName")
    private String userName;

    @SerializedName("BranchFId")
    private Long branchId;

    @SerializedName("status")
    private String status;

    public String getTokenString() {
        return tokenString;
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

    public Long getUserId() {
        return userId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }
}
