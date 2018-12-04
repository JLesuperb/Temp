package com.tutorials.camera.models;

import com.google.gson.annotations.SerializedName;

public class VersionModel
{
    @SerializedName("VersionId")
    private Long versionId;

    @SerializedName("Version")
    private String version;

    @SerializedName("Note")
    private String note;

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
