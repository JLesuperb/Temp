package com.tutorials.camera.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public void setInteger(String key,Integer value)
    {
        if(value!=null) {
            editor.putInt(key,value);
        }
        else {
        editor.remove(key);
        }
        editor.apply();
        editor.commit();
        editor.apply();
    }

    public Integer getInteger(String key)
    {
        if(preferences.contains(key))
            return preferences.getInt(key,Integer.MIN_VALUE);
        else
            return null;
    }

    public void setLong(String key,Long value)
    {
        if(value!=null) {
            editor.putLong(key,value);
        }
        else {
            editor.remove(key);
        }
        editor.apply();
        editor.commit();
        editor.apply();
    }

    public Long getLong(String key)
    {
        if(preferences.contains(key))
            return preferences.getLong(key,Long.MIN_VALUE);
        else
            return null;
    }

    public Date getDate(String key)
    {
        if(preferences.contains(key))
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            try
            {
                return sdf.parse(getString(key));
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setDate(String key,Date value)
    {
        if(value!=null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            editor.putString(key,sdf.format(value));
        }
        else
        {
            editor.remove(key);
        }
        editor.apply();
        editor.commit();
        editor.apply();
    }
}
