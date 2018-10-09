package com.tutorials.camera.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LocalData
{
    //private static final String LocalConfig = "local_config";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public LocalData(Context context)
    {
        //preferences = context.getSharedPreferences(LocalConfig,Context.MODE_PRIVATE);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    public void setString(String key,String value)
    {
        if(value!=null)
        {
            editor.putString(key,value);
        }
        else
        {
            editor.remove(key);
        }
        editor.apply();
        editor.commit();
        editor.apply();
    }

    public String getString(String key)
    {
        if(preferences.contains(key))
            return preferences.getString(key,null);
        else
            return null;
    }

}
