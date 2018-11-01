package com.tutorials.camera.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.tutorials.camera.R;
import com.tutorials.camera.models.Picture;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder>
{
    private Activity activity;
    private List<Picture> pictures;
    private OnPictureClickListener onPictureClickListener;
    private Boolean isSelectable = false;

    public PictureAdapter(Activity activity)
    {
        this.activity = activity;
        pictures = new ArrayList<>();
    }

    public void add(Collection<Picture> pictureCollection)
    {
        pictures.addAll(pictureCollection);
        notifyDataSetChanged();
    }

    public void setOnPictureClickListener(OnPictureClickListener onPictureClickListener)
    {
        this.onPictureClickListener = onPictureClickListener;
    }

    public void setSelectable(Boolean selectable)
    {
        isSelectable = selectable;
        if(!selectable)
        {
            for(Picture picture:pictures)
            {
                picture.setChecked(false);
            }
        }
        notifyDataSetChanged();
    }

    public Boolean getSelectable()
    {
        return isSelectable;
    }

    public void checkToggle()
    {
        List<Integer> listChecked = new ArrayList<>();
        List<Integer> listUnChecked = new ArrayList<>();
        for(Picture picture:pictures)
        {
            if(picture.getChecked())
                listChecked.add(1);
            else if(!picture.getChecked())
                listUnChecked.add(1);
        }

        if(listChecked.size()==pictures.size())
        {
            for(Picture picture:pictures)
            {
                picture.setChecked(false);
            }
            notifyDataSetChanged();
        }

        else if(listUnChecked.size()==pictures.size())
        {
            for(Picture picture:pictures)
            {
                picture.setChecked(true);
            }
            notifyDataSetChanged();
        }
        else
        {
            for(Picture picture:pictures)
            {
                picture.setChecked(true);
            }
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_picture,null);
        return new PictureAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i)
    {
        final Picture picture = pictures.get(i);
        File file = new File(picture.getPicturePath());

        int visibility = (isSelectable)?View.VISIBLE:View.GONE;
        viewHolder.checkBox.setVisibility(visibility);

        viewHolder.checkBox.setChecked(picture.getChecked());

        if(file.exists())
        {
            Glide.with(activity).load(file.getAbsolutePath()).into(viewHolder.itemImage);

            viewHolder.itemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onPictureClickListener!=null)
                        onPictureClickListener.onPictureClick(picture);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        AppCompatImageView itemImage;
        AppCompatCheckBox checkBox;

        ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    public interface OnPictureClickListener
    {
        void onPictureClick(Picture picture);
    }
}
