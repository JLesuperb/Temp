package com.tutorials.camera.custom;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tutorials.camera.R;
import com.tutorials.camera.models.Folder;

import java.util.List;

public class MySpinnerAdapter extends ArrayAdapter<Folder>
{
    public MySpinnerAdapter(Context context, List<Folder> items)
    {
        super(context, R.layout.item_folder, items);
    }

    @Override
    public boolean isEnabled(int position){
        return position != 0;
    }

    @Override
    public View getDropDownView(int position, View convertView,@NonNull ViewGroup parent)
    {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view;
        if(position == 0)
            // Set the hint text color gray
            tv.setTextColor(Color.GRAY);
        else
            tv.setTextColor(Color.BLACK);
        return view;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view;
        if(position == 0)
            // Set the hint text color gray
            tv.setTextColor(Color.GRAY);
        else
            tv.setTextColor(Color.BLACK);
        return view;
        //return super.getView(position, convertView, parent);
    }
}
