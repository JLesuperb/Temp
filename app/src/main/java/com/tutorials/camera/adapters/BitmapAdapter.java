package com.tutorials.camera.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.tutorials.camera.R;

import java.util.ArrayList;
import java.util.List;

public class BitmapAdapter extends RecyclerView.Adapter<BitmapAdapter.ViewHolder>
{
    private List<String> paths;
    private ImageViewClickListener clickListener;
    private ImageViewLongClickListener longClickListener;

    public BitmapAdapter()
    {
        paths = new ArrayList<>();
    }

    public void addPath(String path)
    {
        paths.add(path);
        notifyDataSetChanged();
    }

    public void editBitmap(int index,String path)
    {
        paths.set(index,path);
        notifyDataSetChanged();
    }

    public List<String> getPaths()
    {
        return paths;
    }

    public void removeBitmap(String path)
    {
        int index = path.indexOf(path);
        paths.remove(path);
        notifyDataSetChanged();
        if(paths.size()>0)
        {
            if(index<paths.size())
            {
                if(clickListener!=null)
                {
                    clickListener.viewClicked(paths.get(index));
                }
            }
            else if(index - 1 < paths.size())
            {
                if(clickListener!=null)
                {
                    clickListener.viewClicked(paths.get(index-1));
                }
            }
            else
            {
                if(clickListener!=null)
                {
                    clickListener.viewClicked(null);
                }
            }
        }
        else
        {
            if(clickListener!=null)
            {
                clickListener.viewClicked(null);
            }
        }
    }

    public void setClickListener(ImageViewClickListener clickListener)
    {
        this.clickListener = clickListener;
    }

    public void setLongClickListener(ImageViewLongClickListener longClickListener)
    {
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_picture,null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i)
    {
        final String path = paths.get(i);
        View itemView = viewHolder.itemView;
        //final Bitmap bitmap = bitmapList.get(i);
        //viewHolder.pictureIV.setImageBitmap(bitmap);
        Glide.with(itemView.getContext()).load(path).into(viewHolder.pictureIV);
        viewHolder.pictureIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(clickListener!=null)
                    clickListener.viewClicked(path);
            }
        });

        viewHolder.pictureIV.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                if(longClickListener!=null)
                    longClickListener.viewLongClicked(path);
                return false;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return paths.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        AppCompatImageView pictureIV;
        ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            pictureIV = itemView.findViewById(R.id.pictureIV);
        }
    }

    public interface ImageViewClickListener
    {
        void viewClicked(String path);
    }

    public interface ImageViewLongClickListener
    {
        void viewLongClicked(String path);
    }
}
