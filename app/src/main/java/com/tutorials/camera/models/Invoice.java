package com.tutorials.camera.models;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

@Entity(nameInDb = "TInvoices")
public class Invoice implements Serializable
{
    public static final long serialVersionUID = 90623229L;

    @Id()
    @Property(nameInDb = "InvoiceId")
    @SerializedName("InvoiceId")
    private Long invoiceId;

    @Property(nameInDb = "InvoiceCode")
    @SerializedName("InvoiceCode")
    private String invoiceCode;

    @Property(nameInDb = "InvoiceDesc")
    @SerializedName("InvoiceDesc")
    private String invoiceDesc;

    @Property(nameInDb = "InvoiceBarCode")
    @SerializedName("InvoiceBarCode")
    private String invoiceBarCode;

    @Property(nameInDb = "UserFId")
    @SerializedName("UserFId")
    private Long userId;

    @Property(nameInDb = "BranchFId")
    @SerializedName("BranchFId")
    private Long branchId;

    @Property(nameInDb = "Uploaded")
    private Boolean uploaded;

    @Property(nameInDb = "DirectoryId")
    @SerializedName("DirectoryFId")
    private Long folderId;

    @Transient
    private Boolean isChecked = false;

    @Transient
    private Boolean isInProgress = false;

    @Property(nameInDb = "SavingDate")
    private String savingDate;

    @Generated(hash = 90623229)
    public Invoice(Long invoiceId, String invoiceCode, String invoiceDesc,
            String invoiceBarCode, Long userId, Long branchId, Boolean uploaded,
            Long folderId, String savingDate) {
        this.invoiceId = invoiceId;
        this.invoiceCode = invoiceCode;
        this.invoiceDesc = invoiceDesc;
        this.invoiceBarCode = invoiceBarCode;
        this.userId = userId;
        this.branchId = branchId;
        this.uploaded = uploaded;
        this.folderId = folderId;
        this.savingDate = savingDate;
    }

    @Generated(hash = 1296330302)
    public Invoice() {
    }

    public Long getInvoiceId() {
        return this.invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceCode() {
        return this.invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public String getInvoiceDesc() {
        return this.invoiceDesc;
    }

    public void setInvoiceDesc(String invoiceDesc) {
        this.invoiceDesc = invoiceDesc;
    }

    public String getInvoiceBarCode() {
        return this.invoiceBarCode;
    }

    public void setInvoiceBarCode(String invoiceBarCode) {
        this.invoiceBarCode = invoiceBarCode;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBranchId() {
        return this.branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public Boolean getUploaded() {
        return this.uploaded;
    }

    public void setUploaded(Boolean uploaded) {
        this.uploaded = uploaded;
    }

    public Long getFolderId() {
        return this.folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getSavingDate() {
        return this.savingDate;
    }

    public void setSavingDate(String savingDate) {
        this.savingDate = savingDate;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public Boolean getInProgress() {
        return isInProgress;
    }

    public void setInProgress(Boolean inProgress) {
        isInProgress = inProgress;
    }
}
