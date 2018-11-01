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
import com.tutorials.camera.adapters.FolderAdapter;
import com.tutorials.camera.decorations.CardDecoration;
import com.tutorials.camera.models.Folder;
import com.tutorials.camera.models.FolderDao;
import com.tutorials.camera.models.Invoice;
import com.tutorials.camera.models.InvoiceDao;
import com.tutorials.camera.ui.activities.GalleryActivity;
import com.tutorials.camera.ui.fragments._BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class FoldersFragment extends _BaseFragment implements FolderAdapter.OnFolderClickListener, FolderAdapter.OnFolderLongClickListener, FolderAdapter.OnFolderCheckListener {
    private FolderAdapter adapter;

    @NonNull
    public static FoldersFragment getInstance()
    {
        return new FoldersFragment();
    }

    public FoldersFragment()
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
        return inflater.inflate(R.layout.fragment_folders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Folders");
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

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new CardDecoration(5));
        FolderDao folderDao = SCamera.getInstance().getDaoSession().getFolderDao();
        List<Folder> folders = folderDao.loadAll();
        List<Folder> folderList = new ArrayList<>();
        for(Folder folder:folders)
        {
            InvoiceDao invoiceDao = SCamera.getInstance().getDaoSession().getInvoiceDao();
            List<Invoice> invoices = invoiceDao.queryBuilder().where(InvoiceDao.Properties.FolderId.eq(folder.getFolderId())).list();
            if(invoices.size()>0)
            {
                folderList.add(folder);
            }
        }
        adapter = new FolderAdapter(getActivity());
        adapter.add(folderList);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(adapter);
        adapter.setOnFolderClickListener(this);
        adapter.setOnFolderLongClickListener(this);
        adapter.setOnFolderCheckListener(this);
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
    public void onFolderClick(Folder folder)
    {
        if(getActivity()!=null)
        {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            DatesFragment fragment = DatesFragment.getInstance();
            Bundle bundle = new Bundle();
            bundle.putSerializable("folder",folder);
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .hide(this)
                    .add(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
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
    public void onFolderLongClick(Folder folder)
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
    public void onFolderCheck(Folder folder)
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
