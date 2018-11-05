package com.tutorials.camera.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.models.Invoice;
import com.tutorials.camera.models.InvoiceDao;
import com.tutorials.camera.models.Picture;
import com.tutorials.camera.models.PictureDao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder>
{
    private List<Invoice> invoices;
    private OnInvoiceClickListener onInvoiceClickListener;
    private OnInvoiceLongClickListener onInvoiceLongClickListener;
    private OnInvoiceCheckListener onInvoiceCheckListener;
    private Boolean isSelectable = false;

    public InvoiceAdapter()
    {
        invoices = new ArrayList<>();
    }

    public void add(List<Invoice> list)
    {
        invoices.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnInvoiceClickListener(OnInvoiceClickListener onInvoiceClickListener)
    {
        this.onInvoiceClickListener = onInvoiceClickListener;
    }

    public void setOnInvoiceLongClickListener(OnInvoiceLongClickListener onInvoiceLongClickListener)
    {
        this.onInvoiceLongClickListener = onInvoiceLongClickListener;
    }

    public void setOnInvoiceCheckListener(OnInvoiceCheckListener onInvoiceCheckListener)
    {
        this.onInvoiceCheckListener = onInvoiceCheckListener;
    }

    public void update()
    {
        InvoiceDao invoiceDao = SCamera.getInstance().getDaoSession().getInvoiceDao();
        List<Invoice> invoiceList = invoiceDao.queryBuilder().where(InvoiceDao.Properties.Uploaded.eq(false)).orderDesc(InvoiceDao.Properties.InvoiceId).list();
        invoices.clear();
        invoices.addAll(invoiceList);
        notifyDataSetChanged();
    }

    public void setSelectable(Boolean selectable)
    {
        isSelectable = selectable;
        if(!selectable)
        {
            for(Invoice invoice:invoices)
            {
                invoice.setChecked(false);
            }
        }
        notifyDataSetChanged();
    }

    public void checkToggle()
    {
        List<Integer> listChecked = new ArrayList<>();
        List<Integer> listUnChecked = new ArrayList<>();
        for(Invoice invoice:invoices)
        {
            if(invoice.getChecked())
                listChecked.add(1);
            else if(!invoice.getChecked())
                listUnChecked.add(1);
        }

        if(listChecked.size()==invoices.size())
        {
            for(Invoice invoice:invoices)
            {
                invoice.setChecked(false);
            }
            notifyDataSetChanged();
        }

        else if(listUnChecked.size()==invoices.size())
        {
            for(Invoice invoice:invoices)
            {
                invoice.setChecked(true);
            }
            notifyDataSetChanged();
        }
        else
        {
            for(Invoice invoice:invoices)
            {
                invoice.setChecked(true);
            }
            notifyDataSetChanged();
        }
    }

    public Boolean getSelectable()
    {
        return isSelectable;
    }

    public List<Invoice> getSelected()
    {
        List<Invoice> list = new ArrayList<>();
        for(Invoice invoice:invoices)
        {
            if(invoice.getChecked())
                list.add(invoice);
        }
        return list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_invoice, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i)
    {
        View itemView = viewHolder.itemView;

        final Invoice invoice = invoices.get(i);
        if(invoice.getInvoiceCode()!=null && !invoice.getInvoiceCode().trim().isEmpty())
            viewHolder.invoiceNameTV.setText(invoice.getInvoiceCode());
        else if(invoice.getInvoiceBarCode()!=null && !invoice.getInvoiceBarCode().trim().isEmpty())
            viewHolder.invoiceNameTV.setText(invoice.getInvoiceBarCode());

        PictureDao pictureDao = SCamera.getInstance().getDaoSession().getPictureDao();

        Picture picture = pictureDao.queryBuilder()
                .where(PictureDao.Properties.InvoiceId.eq(invoice.getInvoiceId()))
                .orderDesc(PictureDao.Properties.Id).limit(1).unique();

        if(picture!=null)
        {
            Glide.with(itemView.getContext())
                    .load(Uri.fromFile(new File(picture.getPicturePath())))
                    .into(viewHolder.invoiceIV);
        }

        int visibility = (isSelectable)?View.VISIBLE:View.GONE;
        viewHolder.checkBox.setVisibility(visibility);

        viewHolder.checkBox.setChecked(invoice.getChecked());

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                invoice.setChecked(b);
                if(onInvoiceCheckListener !=null)
                    onInvoiceCheckListener.onInvoiceChecked(invoice);
            }
        });


        viewHolder.invoiceIV.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!isSelectable)
                {
                    if(onInvoiceClickListener !=null)
                        onInvoiceClickListener.onInvoiceClicked(invoice);
                }
                else
                {
                    Boolean value = !invoice.getChecked();
                    invoice.setChecked(value);
                    viewHolder.checkBox.setChecked(value);
                }
            }
        });

        viewHolder.invoiceIV.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                if(!isSelectable)
                {
                    //Boolean value = !invoice.getChecked();
                    invoice.setChecked(true);
                    viewHolder.checkBox.setChecked(true);
                    if(onInvoiceLongClickListener!=null)
                        onInvoiceLongClickListener.onInvoiceLongClicked(invoice);
                    return true;
                }
                return false;
            }
        });
    }

    public Boolean isCheckAll()
    {
        for(Invoice invoice:invoices)
        {
            if(!invoice.getChecked())
                return false;
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return invoices.size();
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        AppCompatTextView invoiceNameTV;
        AppCompatCheckBox checkBox;
        AppCompatImageView invoiceIV;

        ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            invoiceNameTV = itemView.findViewById(R.id.invoiceNameTV);
            checkBox = itemView.findViewById(R.id.checkBox);
            invoiceIV = itemView.findViewById(R.id.invoiceIV);
        }
    }

    public interface OnInvoiceClickListener
    {
        void onInvoiceClicked(Invoice invoice);
    }

    public interface OnInvoiceLongClickListener
    {
        void onInvoiceLongClicked(Invoice invoice);
    }

    public interface OnInvoiceCheckListener
    {
        void onInvoiceChecked(Invoice invoice);
    }
}
