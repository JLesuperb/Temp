package com.tutorials.camera.ui.fragments.gallery;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.adapters.DateAdapter;
import com.tutorials.camera.decorations.CardDecoration;
import com.tutorials.camera.models.DateModel;
import com.tutorials.camera.models.Folder;
import com.tutorials.camera.models.Invoice;
import com.tutorials.camera.models.InvoiceDao;
import com.tutorials.camera.ui.activities.GalleryActivity;
import com.tutorials.camera.ui.fragments._BaseFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DatesFragment extends _BaseFragment implements DateAdapter.OnDateClickListener, DateAdapter.OnDateLongClickListener, DateAdapter.OnDateCheckListener {
    private DateAdapter adapter;

    @NonNull
    public static DatesFragment getInstance()
    {
        return new DatesFragment();
    }

    public DatesFragment()
    {
        //Must be empty
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        /*if (savedInstanceState != null)
        {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }*/
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dates, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity homeActivity = ((GalleryActivity)getActivity());
        if(homeActivity!=null)
        {
            homeActivity.setSupportActionBar(toolbar);

            ActionBar actionBar = homeActivity.getSupportActionBar();
            if (actionBar != null)
            {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            else if(getActivity().getActionBar()!=null)
            {
                getActivity().getActionBar().setHomeButtonEnabled(true);
            }
        }

        if(getArguments()==null)
            return;
        Folder folder = (Folder) getArguments().getSerializable("folder");
        if(folder==null)
            return;

        toolbar.setTitle(folder.getFolderString());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new CardDecoration(5));
        InvoiceDao invoiceDao = SCamera.getInstance().getDaoSession().getInvoiceDao();
        List<Invoice> invoices = invoiceDao.queryBuilder().where(InvoiceDao.Properties.FolderId.eq(folder.getFolderId())).list();
        List<String> oldDates = new ArrayList<>();
        for(Invoice invoice:invoices)
        {
            oldDates.add(invoice.getSavingDate());
        }
        List<String> newDates = new ArrayList<>(new HashSet<>(oldDates));
        List<DateModel> dateModels = new ArrayList<>();
        for(String txt:newDates)
        {
            DateModel dateModel = new DateModel();
            dateModel.setSavingDate(txt);
            dateModels.add(dateModel);
        }
        adapter = new DateAdapter(getActivity());
        adapter.add(dateModels);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(adapter);
        adapter.setOnDateClickListener(this);
        adapter.setOnDateLongClickListener(this);
        adapter.setOnDateCheckListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_gallery, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_check_uncheck:
                if(adapter.getSelectable())
                {
                    adapter.checkToggle();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpToolbar(Boolean visibility)
    {
        if(getView()!=null)
        {
            Toolbar toolbar = getView().findViewById(R.id.toolbar);
            toolbar.getMenu().findItem(R.id.action_delete).setVisible(visibility);
            toolbar.getMenu().findItem(R.id.action_sync).setVisible(visibility);
            toolbar.getMenu().findItem(R.id.action_check_uncheck).setVisible(visibility);
        }
    }

    @Override
    public boolean onBackPressed()
    {
        if(adapter.getSelectable())
        {
            adapter.setSelectable(false);
            setUpToolbar(false);
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onDateClicked(DateModel dateModel)
    {
        if(getActivity()!=null)
        {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            InvoicesFragment fragment = InvoicesFragment.getInstance();
            Bundle bundle = new Bundle();
            bundle.putString("savingDate",dateModel.getSavingDate());
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .hide(this)
                    .add(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }


    @Override
    public void onDateLongClick(DateModel dateModel)
    {
        if(getActivity()!=null)
        {
            Vibrator vibrator = (Vibrator)getActivity(). getSystemService(Context.VIBRATOR_SERVICE);
            if(vibrator!=null)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                }
                else
                {
                    //deprecated in API 26
                    vibrator.vibrate(200);
                }
            }
            adapter.setSelectable(true);
            setUpToolbar(true);
        }
    }

    @Override
    public void onDaterCheck(DateModel dateModel)
    {
        if(getView()!=null && getContext()!=null)
        {
            Drawable drawable = (adapter.isCheckAll())? ContextCompat.getDrawable(getContext(),R.drawable.ic_check_box_outline_white):ContextCompat.getDrawable(getContext(),R.drawable.ic_check_box_white);
            Toolbar toolbar = getView().findViewById(R.id.toolbar);
            MenuItem menuItem = toolbar.getMenu().findItem(R.id.action_check_uncheck);
            menuItem.setIcon(drawable);
        }
    }
}
