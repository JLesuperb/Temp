package com.tutorials.camera.models;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

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

    @Transient
    private Boolean isChecked = false;


    @Generated(hash = 1855408798)
    public Folder(Long folderId, String folderString, String drivePath,
            Long parentId) {
        this.folderId = folderId;
        this.folderString = folderString;
        this.drivePath = drivePath;
        this.parentId = parentId;
    }

    @Generated(hash = 1947132626)
    public Folder() {
    }


    @Override
    public String toString() {
        return folderString;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
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
}
