package com.tutorials.camera.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.tutorials.camera.R;
import com.tutorials.camera.models.DateModel;

import java.util.ArrayList;
import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder>
{

    private List<DateModel> dateModels;
    private OnDateClickListener onDateClickListener;
    private OnDateLongClickListener onDateLongClickListener;
    private OnDateCheckListener onDateCheckListener;
    private Boolean isSelectable = false;

    public DateAdapter(FragmentActivity activity)
    {
        dateModels = new ArrayList<>();
    }

    public void add(List<DateModel> dates)
    {
        this.dateModels.addAll(dates);
        notifyDataSetChanged();
    }

    public void setOnDateClickListener(OnDateClickListener onDateClickListener)
    {
        this.onDateClickListener = onDateClickListener;
    }

    public void setOnDateLongClickListener(OnDateLongClickListener onDateLongClickListener)
    {
        this.onDateLongClickListener = onDateLongClickListener;
    }

    public void setOnDateCheckListener(OnDateCheckListener onDateCheckListener) {
        this.onDateCheckListener = onDateCheckListener;
    }

    public void setSelectable(Boolean selectable)
    {
        isSelectable = selectable;
        if(!selectable)
        {
            for(DateModel dateModel:dateModels)
            {
                dateModel.setChecked(false);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_date, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i)
    {
        final DateModel dateModel = dateModels.get(i);
        viewHolder.dateNameTV.setText(dateModel.getSavingDate());

        int visibility = (isSelectable)?View.VISIBLE:View.GONE;
        viewHolder.checkBox.setVisibility(visibility);

        viewHolder.checkBox.setChecked(dateModel.getChecked());

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                dateModel.setChecked(b);
            }
        });

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(!isSelectable)
                {
                    if(onDateClickListener !=null)
                        onDateClickListener.onDateClicked(dateModel);
                }
                else
                {
                    Boolean value = !dateModel.getChecked();
                    dateModel.setChecked(value);
                    viewHolder.checkBox.setChecked(value);
                    if(onDateCheckListener !=null)
                        onDateCheckListener.onDaterCheck(dateModel);
                }
            }
        });

        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view)
            {
                if(!isSelectable)
                {
                    Boolean value = !dateModel.getChecked();
                    dateModel.setChecked(value);
                    viewHolder.checkBox.setChecked(value);
                    if(onDateLongClickListener!=null)
                        onDateLongClickListener.onDateLongClick(dateModel);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dateModels.size();
    }

    public Boolean getSelectable() {
        return isSelectable;
    }

    public void checkToggle()
    {
        List<Integer> listChecked = new ArrayList<>();
        List<Integer> listUnChecked = new ArrayList<>();
        for(DateModel dateModel:dateModels)
        {
            if(dateModel.getChecked())
                listChecked.add(1);
            else if(!dateModel.getChecked())
                listUnChecked.add(1);
        }

        if(listChecked.size()==dateModels.size())
        {
            for(DateModel dateModel:dateModels)
            {
                dateModel.setChecked(false);
            }
            notifyDataSetChanged();
        }

        else if(listUnChecked.size()==dateModels.size())
        {
            for(DateModel dateModel:dateModels)
            {
                dateModel.setChecked(true);
            }
            notifyDataSetChanged();
        }
        else
        {
            for(DateModel dateModel:dateModels)
            {
                dateModel.setChecked(true);
            }
            notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        Toolbar dateToolbar;
        AppCompatTextView dateNameTV;
        AppCompatCheckBox checkBox;

        ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            dateToolbar = itemView.findViewById(R.id.dateToolbar);
            dateNameTV = itemView.findViewById(R.id.dateNameTV);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    public Boolean isCheckAll()
    {
        for(DateModel dateModel:dateModels)
        {
            if(!dateModel.getChecked())
                return false;
        }
        return true;
    }

    public interface OnDateClickListener
    {
        void onDateClicked(DateModel dateModel);
    }

    public interface OnDateLongClickListener
    {
        void onDateLongClick(DateModel dateModel);
    }

    public interface OnDateCheckListener
    {
        void onDaterCheck(DateModel dateModel);
    }
}
