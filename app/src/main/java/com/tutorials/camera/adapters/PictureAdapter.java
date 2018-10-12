package com.tutorials.camera.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    public PictureAdapter(Activity activity)
    {
        this.activity = activity;
        pictures = new ArrayList<>();
    }

    public void addAll(Collection<Picture> pictureCollection)
    {
        pictures.addAll(pictureCollection);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_picture,null);
        return new PictureAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i)
    {
        Picture picture = pictures.get(i);
        File file = new File(picture.getFilePath());
        if(file.exists())
        {
            Glide.with(activity).load(file.getAbsolutePath()).into(viewHolder.itemImage);
            if(!picture.getBarCode().isEmpty())
            {
                viewHolder.productNameTxt.setText(picture.getBarCode());
            }
            else
            {
                viewHolder.productNameTxt.setText(picture.getName());
            }
        }
        //Toast.makeText(activity,picture.getFilePath(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        AppCompatTextView productNameTxt;
        AppCompatImageView itemImage;
        ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            productNameTxt = itemView.findViewById(R.id.productNameTxt);
            itemImage = itemView.findViewById(R.id.itemImage);
        }
    }
}
