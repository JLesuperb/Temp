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

import com.tutorials.camera.R;

import java.util.ArrayList;
import java.util.List;

public class BitmapAdapter extends RecyclerView.Adapter<BitmapAdapter.ViewHolder>
{
    private List<Bitmap> bitmapList;
    private ImageViewClickListener clickListener;
    private ImageViewLongClickListener longClickListener;

    public BitmapAdapter()
    {
        bitmapList = new ArrayList<>();
    }

    public void addBitmap(Bitmap bitmap)
    {
        bitmapList.add(bitmap);
        notifyDataSetChanged();
    }

    public void editBitmap(int index,Bitmap bitmap)
    {
        bitmapList.set(index,bitmap);
        notifyDataSetChanged();
    }

    public List<Bitmap> getBitmapList()
    {
        return bitmapList;
    }

    public void removeBitmap(Bitmap bitmap)
    {
        int index = bitmapList.indexOf(bitmap);
        bitmapList.remove(bitmap);
        notifyDataSetChanged();
        if(bitmapList.size()>0)
        {
            if(index<bitmapList.size())
            {
                if(clickListener!=null)
                {
                    clickListener.viewClicked(bitmapList.get(index));
                }
            }
            else if(index - 1 < bitmapList.size())
            {
                if(clickListener!=null)
                {
                    clickListener.viewClicked(bitmapList.get(index-1));
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
        final Bitmap bitmap = bitmapList.get(i);
        viewHolder.pictureIV.setImageBitmap(bitmap);
        viewHolder.pictureIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(clickListener!=null)
                    clickListener.viewClicked(bitmap);
            }
        });

        viewHolder.pictureIV.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                if(longClickListener!=null)
                    longClickListener.viewLongClicked(bitmap);
                return false;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return bitmapList.size();
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
        void viewClicked(Bitmap bitmap);
    }

    public interface ImageViewLongClickListener
    {
        void viewLongClicked(Bitmap bitmap);
    }
}
