package com.tutorials.camera.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.models.Folder;
import com.tutorials.camera.models.Invoice;
import com.tutorials.camera.models.InvoiceDao;
import com.tutorials.camera.models.Picture;
import com.tutorials.camera.models.PictureDao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder>
{
    private final List<Folder> folders;
    private OnFolderClickListener onFolderClickListener;
    private OnFolderLongClickListener onFolderLongClickListener;
    private OnFolderCheckListener onFolderCheckListener;
    private Boolean isSelectable = false;

    public FolderAdapter(FragmentActivity activity)
    {
        folders = new ArrayList<>();
    }

    public void add(Folder folder)
    {
        folders.add(folder);
    }

    public void add(List<Folder> folders)
    {
        this.folders.addAll(folders);
        notifyDataSetChanged();
    }

    public void setOnFolderClickListener(OnFolderClickListener onFolderClickListener)
    {
        this.onFolderClickListener = onFolderClickListener;
    }

    public void setOnFolderLongClickListener(OnFolderLongClickListener onFolderLongClickListener)
    {
        this.onFolderLongClickListener = onFolderLongClickListener;
    }

    public void setOnFolderCheckListener(OnFolderCheckListener onFolderCheckListener) {
        this.onFolderCheckListener = onFolderCheckListener;
    }

    public void setSelectable(Boolean selectable)
    {
        isSelectable = selectable;
        if(!selectable)
        {
            for(Folder folder:folders)
            {
                folder.setChecked(false);
            }
        }
        notifyDataSetChanged();
    }

    public Boolean getSelectable()
    {
        return isSelectable;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_folder, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i)
    {
        View itemView = viewHolder.itemView;
        final Folder folder = folders.get(i);
        viewHolder.folderNameTV.setText(folder.getFolderString());

        InvoiceDao invoiceDao = SCamera.getInstance().getDaoSession().getInvoiceDao();
        Invoice invoice = invoiceDao.queryBuilder().orderDesc(InvoiceDao.Properties.InvoiceId).limit(1).unique();
        if(invoice!=null)
        {
            PictureDao pictureDao = SCamera.getInstance().getDaoSession().getPictureDao();
            Picture picture = pictureDao.queryBuilder().orderDesc(PictureDao.Properties.Id).limit(1).unique();
            if(picture!=null)
            {
                Glide.with(itemView.getContext())
                        .load(Uri.fromFile(new File(picture.getPicturePath())))
                        .asBitmap()
                        .into(viewHolder.folderIV);
            }
        }

        int visibility = (isSelectable)?View.VISIBLE:View.GONE;
        viewHolder.checkBox.setVisibility(visibility);

        viewHolder.checkBox.setChecked(folder.getChecked());

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                folder.setChecked(b);
            }
        });

        viewHolder.cardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!isSelectable)
                {
                    if(onFolderClickListener !=null)
                        onFolderClickListener.onFolderClick(folder);
                }
                else
                {
                    Boolean value = !folder.getChecked();
                    folder.setChecked(value);
                    viewHolder.checkBox.setChecked(value);
                    if(onFolderCheckListener !=null)
                        onFolderCheckListener.onFolderCheck(folder);
                }
            }
        });

        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view)
            {
                if(!isSelectable)
                {
                    Boolean value = !folder.getChecked();
                    folder.setChecked(value);
                    viewHolder.checkBox.setChecked(value);
                    if(onFolderLongClickListener!=null)
                        onFolderLongClickListener.onFolderLongClick(folder);
                    return true;
                }
                return false;
            }
        });
    }

    public Boolean isCheckAll()
    {
        for(Folder folder:folders)
        {
            if(!folder.getChecked())
                return false;
        }
        return true;
    }

    @Override
    public int getItemCount()
    {
        return folders.size();
    }

    public void checkToggle()
    {
        List<Integer> listChecked = new ArrayList<>();
        List<Integer> listUnChecked = new ArrayList<>();
        for(Folder folder:folders)
        {
            if(folder.getChecked())
                listChecked.add(1);
            else if(!folder.getChecked())
                listUnChecked.add(1);
        }

        if(listChecked.size()==folders.size())
        {
            for(Folder folder:folders)
            {
               folder.setChecked(false);
            }
            notifyDataSetChanged();
        }

        else if(listUnChecked.size()==folders.size())
        {
            for(Folder folder:folders)
            {
                folder.setChecked(true);
            }
            notifyDataSetChanged();
        }
        else
        {
            for(Folder folder:folders)
            {
                folder.setChecked(true);
            }
            notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        Toolbar folderToolbar;
        AppCompatTextView folderNameTV;
        AppCompatCheckBox checkBox;
        AppCompatImageView folderIV;

        ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            checkBox = itemView.findViewById(R.id.checkBox);
            folderToolbar = itemView.findViewById(R.id.folderToolbar);
            folderNameTV = itemView.findViewById(R.id.folderNameTV);
            folderIV = itemView.findViewById(R.id.folderIV);
        }
    }

    public interface OnFolderClickListener
    {
        void onFolderClick(Folder folder);
    }

    public interface OnFolderLongClickListener
    {
        void onFolderLongClick(Folder folder);
    }

    public interface OnFolderCheckListener
    {
        void onFolderCheck(Folder folder);
    }
}
