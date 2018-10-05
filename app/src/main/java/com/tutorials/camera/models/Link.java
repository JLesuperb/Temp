package com.tutorials.camera.models;

import com.google.gson.annotations.SerializedName;

public class Link
{
    @SerializedName("LinkText")
    private String linkText;

    public String getLinkText() {
        return linkText;
    }
}
