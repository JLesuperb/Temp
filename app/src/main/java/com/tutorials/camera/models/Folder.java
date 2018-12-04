package com.tutorials.camera.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;

@Entity(nameInDb = "TFolders")
public class Folder implements Serializable
{
    public static final long serialVersionUID = 194716L;

    @Id()
    @Property(nameInDb = "FolderId")
    @SerializedName("DirectoryId")
    private Long folderId;

    @Property(nameInDb = "FolderName")
    @SerializedName("DirectoryName")
    private String folderString;

    @Property(nameInDb = "DrivePath")
    @SerializedName("DrivePath")
    private String drivePath;

    @Property(nameInDb = "ParentFId")
    @SerializedName("ParentFId")
    private Long parentId;

    @Property(nameInDb = "UserFId")
    @SerializedName("UserFId")
    private Long userId;

    @Generated(hash = 558511380)
    public Folder(Long folderId, String folderString, String drivePath,
            Long parentId, Long userId) {
        this.folderId = folderId;
        this.folderString = folderString;
        this.drivePath = drivePath;
        this.parentId = parentId;
        this.userId = userId;
    }

    @Generated(hash = 1947132626)
    public Folder() {
    }

    public Long getFolderId() {
        return this.folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getFolderString() {
        return this.folderString;
    }

    public void setFolderString(String folderString) {
        this.folderString = folderString;
    }

    public String getDrivePath() {
        return this.drivePath;
    }

    public void setDrivePath(String drivePath) {
        this.drivePath = drivePath;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @NonNull
    @Override
    public String toString() {
        return folderString;
    }
}
