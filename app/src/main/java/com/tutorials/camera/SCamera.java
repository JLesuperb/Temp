package com.tutorials.camera;

import android.app.Application;

import com.tutorials.camera.helpers.DbHelper;
import com.tutorials.camera.models.DaoMaster;
import com.tutorials.camera.models.DaoSession;

import org.jetbrains.annotations.Contract;

public class SCamera extends Application
{
    private static SCamera _Instance;
    @Contract(pure = true)
    public static SCamera getInstance() { return _Instance; }

    private byte[] bytes;
    private DaoSession mDaoSession;
    private String folderName;

    @Override
    public void onCreate() {
        super.onCreate();
        mDaoSession = new DaoMaster(
                new DbHelper(this, "SCamera.db").getWritableDb()).newSession();

        _Instance = this;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public byte[] getBytes()
    {
        return bytes;
    }

    public void setBytes(byte[] bytes)
    {
        this.bytes = bytes;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
