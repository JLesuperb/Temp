package com.tutorials.camera;

import android.app.Application;
import android.os.Environment;

import com.tutorials.camera.data.LocalData;
import com.tutorials.camera.helpers.DbHelper;
import com.tutorials.camera.models.DaoMaster;
import com.tutorials.camera.models.DaoSession;
import com.tutorials.camera.models.Folder;
import com.tutorials.camera.models.Invoice;
import com.tutorials.camera.models.Mode;
import com.tutorials.camera.models.User;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.util.List;

public class SCamera extends Application
{
    private static SCamera _Instance;
    private User currentUser;
    private Mode mode;
    private String token;
    private String serverString;
    private Folder folder;
    private List<Invoice> syncInvoices;

    @Contract(pure = true)
    public static SCamera getInstance() { return _Instance; }

    private DaoSession mDaoSession;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mDaoSession = new DaoMaster(new DbHelper(this, "SArchives.db").getWritableDb()).newSession();
        LocalData localData = new LocalData(getApplicationContext());
        if(localData.getString("serverAddress")!=null)
        {
            serverString = localData.getString("serverAddress");
        }
        _Instance = this;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public String getFolderName(String folder)
    {
        String root = Environment.getExternalStorageDirectory().toString();
        File parentFile = new File(root,"SCamera");
        File file = new File(parentFile,folder);
        return file.getAbsolutePath();
    }

    public void setFolderName(String folderName) {
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public String getServerString() {
        return serverString;
    }

    public void setServerString(String serverString) {
        this.serverString = serverString;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setSyncInvoices(List<Invoice> syncInvoices)
    {
        this.syncInvoices = syncInvoices;
    }

    public List<Invoice> getSyncInvoices()
    {
        return syncInvoices;
    }
}
