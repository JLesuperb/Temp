package com.tutorials.camera.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.adapters.InvoiceAdapter;
import com.tutorials.camera.data.LocalData;
import com.tutorials.camera.decorations.CardDecoration;
import com.tutorials.camera.interfaces.IFolders;
import com.tutorials.camera.models.Folder;
import com.tutorials.camera.models.FolderDao;
import com.tutorials.camera.models.Invoice;
import com.tutorials.camera.models.User;
import com.tutorials.camera.tools.AppTools;
import com.tutorials.camera.tools.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        InvoiceAdapter.OnInvoiceClickListener,
        InvoiceAdapter.OnInvoiceLongClickListener,
        InvoiceAdapter.OnInvoiceCheckListener
{
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.navigationView)
    NavigationView navigationView;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private InvoiceAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        final User user = SCamera.getInstance().getCurrentUser();
        if(user==null)
        {
            startActivity(new Intent(getApplicationContext(),AuthenticationActivity.class));
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews()
    {
        toolbar.setTitle("S. Archive");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        recyclerView.addItemDecoration(new CardDecoration(5));

        adapter = new InvoiceAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(adapter);
        findViewById(R.id.captureBtn).setOnClickListener(this);
        adapter.setOnInvoiceClickListener(this);
        adapter.setOnInvoiceLongClickListener(this);
        adapter.setOnInvoiceCheckListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        adapter.update();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer!=null)
        {
            if (drawer.isDrawerOpen(GravityCompat.START))
            {
                drawer.closeDrawer(GravityCompat.START);
            }
        }

        switch (menuItem.getItemId())
        {
            case R.id.server_config:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        launchServerConfig();
                    }
                },500);
                break;

            case R.id.folders_sync:
                syncFolders();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                },500);
                break;

            case R.id.default_folder:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        defaultFolders();
                    }
                },500);
                break;

            /*case R.id.gallery:
                break;*/
        }
        return false;
    }

    private void launchServerConfig()
    {
        AppTools.loadConfigDialog(MainActivity.this);
    }

    private void syncFolders()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.folders_sync));
        progressDialog.show();

        User user = SCamera.getInstance().getCurrentUser();
        String token = String.format("Bearer %s", user.getToken());
        IFolders iFolders = RetrofitClient.getRetrofitInstance(MainActivity.this).create(IFolders.class);
        Call<List<Folder>> call = iFolders.get(token);
        call.enqueue(new Callback<List<Folder>>() {
            @Override
            public void onResponse(@NonNull Call<List<Folder>> call, @NonNull Response<List<Folder>> response)
            {
                progressDialog.dismiss();
                if(response.isSuccessful())
                {
                    List<Folder> folders = response.body();
                    if(folders!=null)
                    {
                        FolderDao folderDao = SCamera.getInstance().getDaoSession().getFolderDao();
                        folderDao.insertOrReplaceInTx(folders);
                        //loadList();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Connection Not found",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Folder>> call, @NonNull Throwable t)
            {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Connection Not found",Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),new Exception(t).getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void defaultFolders()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.parameters));

        FolderDao folderDao = SCamera.getInstance().getDaoSession().getFolderDao();

        List<Folder> folders = folderDao.loadAll();

        List<String> foldersNames = new ArrayList<>();

        String defaultFolder = new LocalData(this).getString("defaultFolder");

        Integer checkedItem = -1;
        for(Integer i=0;i<folders.size();i++)
        {
            foldersNames.add(folders.get(i).getFolderString());
            if(defaultFolder!=null && defaultFolder.equals(folders.get(i).getFolderString()))
            {
                checkedItem = i;
            }
        }

        String[] foldersString = new String[foldersNames.size()];
        foldersString = foldersNames.toArray(foldersString);

        final String[] finalFoldersString = foldersString;
        builder.setSingleChoiceItems(foldersString,checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                new LocalData(MainActivity.this).setString("defaultFolder", finalFoldersString[which]);
                //loadList();
            }
        });
        builder.show();
    }

    private Boolean hasFolders()
    {
        FolderDao folderDao = SCamera.getInstance().getDaoSession().getFolderDao();
        List<Folder> folders = folderDao.loadAll();
        return folders.size()>0;
    }

    private void setUpToolbar(Boolean visibility)
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.getMenu().findItem(R.id.action_delete).setVisible(visibility);
        toolbar.getMenu().findItem(R.id.action_sync).setVisible(visibility);
        toolbar.getMenu().findItem(R.id.action_check_uncheck).setVisible(visibility);
    }

    @Override
    public void onBackPressed()
    {
        if(adapter.getSelectable())
        {
            adapter.setSelectable(false);
            setUpToolbar(false);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.captureBtn:
                if(hasFolders())
                {
                    startActivity(new Intent(MainActivity.this,CaptureActivity.class));
                }
                else
                {
                    Toast.makeText(this,getString(R.string.please_sync_folder), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onInvoiceClicked(Invoice invoice)
    {
        Intent intent = new Intent(this,GalleryActivity.class);
        intent.putExtra("invoice",invoice);
        startActivity(intent);
    }

    @Override
    public void onInvoiceLongClicked(Invoice invoice)
    {
        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
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

    @Override
    public void onInvoiceChecked(Invoice invoice)
    {
        Drawable drawable = (adapter.isCheckAll())? ContextCompat.getDrawable(this,R.drawable.ic_check_box_outline_white):ContextCompat.getDrawable(this,R.drawable.ic_check_box_white);
        Toolbar toolbar = findViewById(R.id.toolbar);
        MenuItem menuItem = toolbar.getMenu().findItem(R.id.action_check_uncheck);
        menuItem.setIcon(drawable);
    }
}
