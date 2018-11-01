package com.tutorials.camera.custom;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tutorials.camera.models.Folder;

import java.util.List;

public class MySpinnerAdapter extends ArrayAdapter<Folder>
{
    public MySpinnerAdapter(Context context, List<Folder> items)
    {
        super(context, android.R.layout.simple_spinner_item, items);
    }

    @Override
    public boolean isEnabled(int position){
        return position != 0;
    }
    @Override
    public View getDropDownView(int position, View convertView,
                                @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view;
        if(position == 0){
            // Set the hint text color gray
            tv.setTextColor(Color.GRAY);
        }
        else {
            tv.setTextColor(Color.BLACK);
        }
        return view;
    }

    /*@Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        if (position == 0) {
            return initialSelection(true);
        }
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (position == 0) {
            return initialSelection(false);
        }
        return getCustomView(position, convertView, parent);
    }


    @Override
    public int getCount() {
        return super.getCount() + 1; // Adjust for initial selection item
    }

    @SuppressLint("ResourceAsColor")
    private View initialSelection(boolean dropdown) {
        // Just an example using a simple TextView. Create whatever default view
        // to suit your needs, inflating a separate layout if it's cleaner.
        TextView view = new TextView(getContext());
        //view.setText(R.string.select_one);
        CharSequence sequence = "Select Folder";
        view.setText(sequence);
        view.setTextColor(R.color.black);
        //int spacing = getContext().getResources().getDimensionPixelSize(R.dimen.spacing_smaller);
        //view.setPadding(0, spacing, 0, spacing);

        if (dropdown)
        { // Hidden when the dropdown is opened
            view.setHeight(0);
        }

        return view;
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        // Distinguish "real" spinner items (that can be reused) from initial selection item
        View row = convertView != null && !(convertView instanceof TextView)
                ? convertView :
                LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);

        position = position - 1; // Adjust for initial selection item
        Folder item = getItem(position);

        // ... Resolve views & populate with data ...

        return row;
    }*/
}
