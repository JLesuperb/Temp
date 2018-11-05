package com.tutorials.camera.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.adapters.PhotoPagerAdapter;
import com.tutorials.camera.models.Invoice;
import com.tutorials.camera.models.Picture;
import com.tutorials.camera.models.PictureDao;
import com.tutorials.camera.ui.fragments.gallery.PhotoViewerFragment;

import java.util.List;

import butterknife.ButterKnife;

public class GalleryActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Invoice invoice = (Invoice)intent.getSerializableExtra("invoice");

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }*/

        if(invoice!=null)
        {

            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle(invoice.getInvoiceCode());

            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null)
            {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            else if(getActionBar()!=null)
            {
                getActionBar().setHomeButtonEnabled(true);
            }

            PictureDao pictureDao = SCamera.getInstance().getDaoSession().getPictureDao();
            List<Picture> pictures = pictureDao.queryBuilder().where(PictureDao.Properties.InvoiceId.eq(invoice.getInvoiceId())).list();

            ViewPager viewPager = findViewById(R.id.viewPager);
            PhotoPagerAdapter adapter = new PhotoPagerAdapter(getSupportFragmentManager());
            for(Picture picture:pictures)
            {
                PhotoViewerFragment fragment = new PhotoViewerFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("picture", picture);
                fragment.setArguments(bundle);
                adapter.addFragment(fragment);
            }
            viewPager.setAdapter(adapter);
        }
        else
        {
            finish();
        }
    }


}
