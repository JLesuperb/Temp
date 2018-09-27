package com.tutorials.camera.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class LocalData
{
    private static final String LocalConfig = "local_config";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public LocalData(Context context)
    {
        preferences = context.getSharedPreferences(LocalConfig,Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setString(String key,String value)
    {
        editor.putString(key,value);
        editor.commit();
    }

    public String getString(String key)
    {
        if(preferences.contains(key))
            return preferences.getString(key,"");
        else
            return null;
    }

    public void setBoolean(String key,Boolean value)
    {
        editor.putBoolean(key,value);
        editor.commit();
    }

    public Boolean getBoolean(String key)
    {
        if(preferences.contains(key))
            return preferences.getBoolean(key,false);
        else
            return null;
    }
}
