package com.tutorials.camera.ui.fragments.gallery;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
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
import com.tutorials.camera.adapters.PictureAdapter;
import com.tutorials.camera.decorations.CardDecoration;
import com.tutorials.camera.models.Invoice;
import com.tutorials.camera.models.Picture;
import com.tutorials.camera.models.PictureDao;
import com.tutorials.camera.ui.activities.GalleryActivity;
import com.tutorials.camera.ui.fragments._BaseFragment;

import java.util.List;

public class PictureFragment extends _BaseFragment implements PictureAdapter.OnPictureClickListener {
    private PictureAdapter adapter;

    @NonNull
    public static PictureFragment getInstance()
    {
        return new PictureFragment();
    }

    public PictureFragment()
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
        return inflater.inflate(R.layout.fragment_picture, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        /*AppCompatActivity homeActivity = ((GalleryActivity)getActivity());
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
        }*/

        if(getArguments()==null)
            return;
        Invoice invoice = (Invoice)getArguments().getSerializable("invoice");
        if(invoice==null)
            return;

        if(invoice.getInvoiceCode()!=null && !invoice.getInvoiceCode().trim().isEmpty())
            toolbar.setTitle(invoice.getInvoiceCode());
        else if(invoice.getInvoiceBarCode()!=null && !invoice.getInvoiceBarCode().trim().isEmpty())
            toolbar.setTitle(invoice.getInvoiceBarCode());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new CardDecoration(5));
        PictureDao pictureDao = SCamera.getInstance().getDaoSession().getPictureDao();
        List<Picture> pictures = pictureDao.queryBuilder().where(PictureDao.Properties.InvoiceId.eq(invoice.getInvoiceId())).list();
        adapter = new PictureAdapter(getActivity());
        adapter.add(pictures);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(adapter);
        adapter.setOnPictureClickListener(this);
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

    @Override
    public void onPictureClick(Picture picture)
    {
        if(getActivity()!=null)
        {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            DetailFragment fragment = DetailFragment.getInstance();
            Bundle bundle = new Bundle();
            bundle.putSerializable("picture",picture);
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .hide(this)
                    .add(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
