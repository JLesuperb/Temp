package com.tutorials.camera.ui.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
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
import com.tutorials.camera.data.LocalParams;
import com.tutorials.camera.decorations.CardDecoration;
import com.tutorials.camera.interfaces.IFolders;
import com.tutorials.camera.models.Folder;
import com.tutorials.camera.models.FolderDao;
import com.tutorials.camera.models.Invoice;
import com.tutorials.camera.models.InvoiceDao;
import com.tutorials.camera.models.User;
import com.tutorials.camera.services.UploadService;
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

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null)
            {
                int resultCode = bundle.getInt(UploadService.RESULT);
                if (resultCode == RESULT_OK)
                {
                    if(adapter!=null)
                    {
                        adapter.update();
                        setPending();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final User user = SCamera.getInstance().getCurrentUser();
        if(user==null)
        {
            startActivity(new Intent(getApplicationContext(),AuthenticationActivity.class));
            finish();
            return;
        }
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

        View header = navigationView.getHeaderView(0);
        //String fullName = localData.getString(Params.USER_FULL_NAME);
        User user = SCamera.getInstance().getCurrentUser();
        AppCompatTextView fullNameTxt = header.findViewById(R.id.fullNameTxt);
        fullNameTxt.setText(user.getUserName());

        AppCompatTextView accountNameTxt = header.findViewById(R.id.accountNameTxt);
        fullNameTxt.setText(user.getUserName());

        recyclerView.addItemDecoration(new CardDecoration(5));

        adapter = new InvoiceAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(adapter);
        findViewById(R.id.captureBtn).setOnClickListener(this);
        adapter.setOnInvoiceClickListener(this);
        adapter.setOnInvoiceLongClickListener(this);
        adapter.setOnInvoiceCheckListener(this);

        setPending();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        setPending();
        return true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        adapter.update();
        registerReceiver(receiver, new IntentFilter(UploadService.NOTIFICATION));
        setPending();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_sync:
                if(!isMyServiceRunning())
                {
                    if(adapter.getSelectable())
                    {
                        List<Invoice> invoices = adapter.getSelected();
                        if(invoices.size()>0)
                        {
                            SCamera.getInstance().setSyncInvoices(invoices);
                            syncPictures();
                        }
                    }
                    else
                    {
                        List<Invoice> invoices = adapter.getInvoices();
                        if(invoices.size()>0)
                        {
                            SCamera.getInstance().setSyncInvoices(invoices);
                            syncPictures();
                        }

                    }
                }
                break;
            case R.id.action_check_uncheck:
                if(adapter!=null)
                    adapter.checkToggle();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setPending()
    {
        int size;
        if(adapter!=null && adapter.getSelectable())
        {
            size = adapter.getSelected().size();
        }
        else
        {
            InvoiceDao invoiceDao = SCamera.getInstance().getDaoSession().getInvoiceDao();
            List<Invoice> invoices = invoiceDao.queryBuilder().where(InvoiceDao.Properties.Uploaded.eq(false)).list();
            size = invoices.size();
        }
        int color = ContextCompat.getColor(getApplicationContext(), R.color.white);
        Bitmap bitmap = textAsBitmap((size+""),30,color);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
        MenuItem item = toolbar.getMenu().findItem(R.id.action_counter);
        if(item!=null)
        {
            item.setIcon(drawable);
        }
        checkRemain();
    }

    private void checkRemain()
    {
        InvoiceDao invoiceDao = SCamera.getInstance().getDaoSession().getInvoiceDao();
        List<Invoice> invoices = invoiceDao.queryBuilder().where(InvoiceDao.Properties.Uploaded.eq(false)).list();
        int size = invoices.size();
        AppCompatTextView emptyTextView = findViewById(R.id.emptyTextView);
        if(size==0)
        {
            emptyTextView.setVisibility(View.VISIBLE);
        }
        else
        {
            emptyTextView.setVisibility(View.GONE);
        }
    }

    public Bitmap textAsBitmap(String text, float textSize, int textColor)
    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private boolean isMyServiceRunning()
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(manager!=null)
        {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (UploadService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void syncPictures()
    {
        Intent i= new Intent(this, UploadService.class);
        startService(i);
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        syncFolders();
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
            case R.id.logout:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SCamera.getInstance().setCurrentUser(null);
                        startActivity(new Intent(getApplicationContext(),AuthenticationActivity.class));
                        finish();
                    }
                },500);
                break;
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
        Call<Folder[]> call = iFolders.get(token);
        call.enqueue(new Callback<Folder[]>() {
            @Override
            public void onResponse(@NonNull Call<Folder[]> call, @NonNull Response<Folder[]> response)
            {
                progressDialog.dismiss();
                if(response.isSuccessful())
                {
                    Folder[] folders = response.body();
                    if(folders!=null)
                    {
                        FolderDao folderDao = SCamera.getInstance().getDaoSession().getFolderDao();
                        folderDao.insertOrReplaceInTx(folders);
                        Toast.makeText(MainActivity.this,"Folders updated",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Connection Not found",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Folder[]> call, @NonNull Throwable t)
            {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Connection Not found",Toast.LENGTH_LONG).show();
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
        toolbar.getMenu().findItem(R.id.action_check_uncheck).setVisible(visibility);
    }

    @Override
    public void onBackPressed()
    {
        if(adapter.getSelectable())
        {
            adapter.setSelectable(false);
            setUpToolbar(false);
            setPending();
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
                    if(AppTools.checkPermission(this))
                    {
                        startActivity(new Intent(MainActivity.this,CaptureActivity.class));
                    }
                    else
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.CAMERA}, LocalParams.PERMISSION_QUERY);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"We haven't permission",Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(this,getString(R.string.please_sync_folder), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case LocalParams.PERMISSION_QUERY:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.captureBtn).performClick();
                }
                else
                {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
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
        setPending();
    }

    @Override
    public void onInvoiceChecked(Invoice invoice)
    {
        setPending();
        Drawable drawable = (adapter.isCheckAll())? ContextCompat.getDrawable(this,R.drawable.ic_check_box_outline_white):ContextCompat.getDrawable(this,R.drawable.ic_check_box_white);
        Toolbar toolbar = findViewById(R.id.toolbar);
        MenuItem menuItem = toolbar.getMenu().findItem(R.id.action_check_uncheck);
        menuItem.setIcon(drawable);
    }
}
