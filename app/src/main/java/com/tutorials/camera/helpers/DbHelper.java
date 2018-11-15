package com.tutorials.camera.helpers;

import android.content.Context;
import android.util.Log;

import com.tutorials.camera.models.DaoMaster;

import org.greenrobot.greendao.database.Database;

public class DbHelper extends DaoMaster.OpenHelper
{
    public DbHelper(Context context, String name)
    {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion)
    {
        super.onUpgrade(db, oldVersion, newVersion);
        Log.d("DEBUG", "DB_OLD_VERSION : " + oldVersion + ", DB_NEW_VERSION : " + newVersion);
        switch (oldVersion) {
            case 1:
            case 2:
                //db.execSQL("ALTER TABLE " + UserDao.TABLENAME + " ADD COLUMN " + UserDao.Properties.Name.columnName + " TEXT DEFAULT 'DEFAULT_VAL'");
        }
    }
}
