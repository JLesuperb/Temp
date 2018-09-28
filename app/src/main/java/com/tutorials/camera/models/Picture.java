package com.tutorials.camera.models;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "TPictures")
public class Picture
{
    @Id()
    @Property(nameInDb = "PictureId")
    private Long id;

    @Property(nameInDb = "PictureCode")
    private String code;

    @Property(nameInDb = "PictureDesc")
    private String description;

    @Property(nameInDb = "BarCode")
    private String barCode;

    @Property(nameInDb = "PicturePath")
    private String filePath;

    @Generated(hash = 1584322312)
    public Picture(Long id, String code, String description, String barCode,
            String filePath) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.barCode = barCode;
        this.filePath = filePath;
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
}
