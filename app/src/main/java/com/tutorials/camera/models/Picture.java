package com.tutorials.camera.models;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

@Entity(nameInDb = "TPictures")
public class Picture implements Serializable
{
    public static final long serialVersionUID = 2040040024L;

    @Id()
    @Property(nameInDb = "PictureId")
    @SerializedName("PictureId")
    private Long id;

    @Property(nameInDb = "PicturePath")
    @SerializedName("PicturePath")
    private String picturePath;

    @ToOne(joinProperty = "invoiceId")
    private Invoice invoice;

    @Property(nameInDb = "PictureName")
    @SerializedName("PictureName")
    private String pictureName;

    @Property(nameInDb = "InvoiceFId")
    @SerializedName("InvoiceFId")
    private Long invoiceId;

    @Transient
    private Boolean isChecked = false;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 220989104)
    private transient PictureDao myDao;

    @Generated(hash = 402704253)
    public Picture(Long id, String picturePath, String pictureName,
            Long invoiceId) {
        this.id = id;
        this.picturePath = picturePath;
        this.pictureName = pictureName;
        this.invoiceId = invoiceId;
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

    public String getPicturePath() {
        return this.picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getPictureName() {
        return this.pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public Long getInvoiceId() {
        return this.invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    @Generated(hash = 694408149)
    private transient Long invoice__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 710097085)
    public Invoice getInvoice() {
        Long __key = this.invoiceId;
        if (invoice__resolvedKey == null || !invoice__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            InvoiceDao targetDao = daoSession.getInvoiceDao();
            Invoice invoiceNew = targetDao.load(__key);
            synchronized (this) {
                invoice = invoiceNew;
                invoice__resolvedKey = __key;
            }
        }
        return invoice;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2131282099)
    public void setInvoice(Invoice invoice) {
        synchronized (this) {
            this.invoice = invoice;
            invoiceId = invoice == null ? null : invoice.getInvoiceId();
            invoice__resolvedKey = invoiceId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1412175658)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPictureDao() : null;
    }
}
