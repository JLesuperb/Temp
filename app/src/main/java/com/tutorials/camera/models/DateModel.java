package com.tutorials.camera.models;

import java.io.Serializable;

public class DateModel implements Serializable
{
    private String savingDate;

    private Boolean isChecked = false;

    public String getSavingDate() {
        return savingDate;
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
}
