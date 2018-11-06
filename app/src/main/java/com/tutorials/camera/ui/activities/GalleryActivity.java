package com.tutorials.camera.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.adapters.PhotoPagerAdapter;
import com.tutorials.camera.models.Folder;
import com.tutorials.camera.models.FolderDao;
import com.tutorials.camera.models.Invoice;
import com.tutorials.camera.models.Picture;
import com.tutorials.camera.models.PictureDao;
import com.tutorials.camera.ui.fragments.gallery.PhotoViewerFragment;

import java.util.List;

import butterknife.ButterKnife;

public class GalleryActivity extends AppCompatActivity
{
    private Invoice invoice;

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
            this.invoice = invoice;
        }
        else
        {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_detail:
                showDetail();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDetail()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_invoice_detail, null);
        dialogBuilder.setView(dialogView);

        TextInputEditText codeEditText = dialogView.findViewById(R.id.codeEditText);
        codeEditText.setText(invoice.getInvoiceCode());
        TextInputEditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        descriptionEditText.setText(invoice.getInvoiceDesc());
        TextInputEditText folderEditText = dialogView.findViewById(R.id.folderEditText);
        FolderDao folderDao = SCamera.getInstance().getDaoSession().getFolderDao();
        Folder folder = folderDao.queryBuilder().where(FolderDao.Properties.FolderId.eq(invoice.getFolderId())).limit(1).unique();
        if(folder!=null)
            folderEditText.setText(folder.getFolderString());
        TextInputEditText barCodeEditText = dialogView.findViewById(R.id.barCodeEditText);
        barCodeEditText.setText(invoice.getInvoiceBarCode());

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}
