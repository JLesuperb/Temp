package com.tutorials.camera.models;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "TFolders")
public class Folder
{
    @Id()
    @Property(nameInDb = "FolderId")
    @SerializedName("DirectoryId")
    private Long folderId;

    @Property(nameInDb = "FolderName")
    @SerializedName("DirectoryName")
    private String folderString;

    @Generated(hash = 937473288)
    public Folder(Long folderId, String folderString) {
        this.folderId = folderId;
        this.folderString = folderString;
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

    @Override
    public String toString() {
        return folderString;
    }
}
