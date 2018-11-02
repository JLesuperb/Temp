package com.tutorials.camera.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.tutorials.camera.models.Picture;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ImagePagerAdapter extends PagerAdapter
{
    private List<Picture> pictures;

    public ImagePagerAdapter()
    {
        pictures = new ArrayList<>();
    }

    public void add(Collection<Picture> pictureCollection)
    {
        pictures.addAll(pictureCollection);
    }

    @Override
    public int getCount()
    {
        return pictures.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o)
    {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
        Picture picture = pictures.get(position);
        PhotoView photoView = new PhotoView(container.getContext());
        Glide
                .with(container.getContext())
                .load(Uri.fromFile(new File(picture.getPicturePath())))
                .thumbnail(0.2f)
                .into(photoView);

        photoView.setMaximumScale(5.0F);
        photoView.setMediumScale(3.0F);
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;
        //return super.instantiateItem(container, position);
    }
}
