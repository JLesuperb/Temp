package com.tutorials.camera.models;

import com.google.gson.annotations.SerializedName;


public class FolderModel
{
    public static class Pivot
    {
        @SerializedName("UserFId")
        private Long userId;

        @SerializedName("DrivePath")
        private String drivePath;

        public String getDrivePath()
        {
            return drivePath;
        }

        public Long getUserId() {
            return userId;
        }

        public void setDrivePath(String drivePath) {
            this.drivePath = drivePath;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }
    }

    @SerializedName("DirectoryId")
    private Long folderId;

    @SerializedName("DirectoryName")
    private String folderName;

    @SerializedName("ParentFId")
    private Long parentId;

    @SerializedName("pivot")
    private Pivot pivot;


    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Pivot getPivot() {
        return pivot;
    }

    public void setPivot(Pivot pivot) {
        this.pivot = pivot;
    }

}
