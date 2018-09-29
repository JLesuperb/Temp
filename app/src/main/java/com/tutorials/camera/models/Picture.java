package com.tutorials.camera.models;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "TPictures")
public class Picture
{
    @Id()
    @Property(nameInDb = "PictureId")
    @SerializedName("PictureId")
    private Long id;

    @Property(nameInDb = "PictureCode")
    @SerializedName("PictureCode")
    private String code;

    @Property(nameInDb = "PictureDesc")
    @SerializedName("PictureDesc")
    private String description;

    @Property(nameInDb = "BarCode")
    @SerializedName("PictureBarCode")
    private String barCode;

    @Property(nameInDb = "PicturePath")
    @SerializedName("PhonePath")
    private String filePath;

    @Property(nameInDb = "UserFId")
    @SerializedName("UserFId")
    private Long userId;

    @Property(nameInDb = "Folder")
    @SerializedName("Directory")
    private String folder;

    @Property(nameInDb = "Uploaded")
    private Boolean uploaded;

    @Property(nameInDb = "PictureName")
    @SerializedName("PictureName")
    private String name;

    @Generated(hash = 1715774908)
    public Picture(Long id, String code, String description, String barCode,
            String filePath, Long userId, String folder, Boolean uploaded,
            String name) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.barCode = barCode;
        this.filePath = filePath;
        this.userId = userId;
        this.folder = folder;
        this.uploaded = uploaded;
        this.name = name;
    }

    @Generated(hash = 1602548376)
    public Picture() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBarCode() {
        return this.barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFolder() {
        return this.folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public Boolean getUploaded() {
        return this.uploaded;
    }

    public void setUploaded(Boolean uploaded) {
        this.uploaded = uploaded;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
