package com.tutorials.camera.ui.activities;

import android.Manifest;
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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.Toast;

import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.custom.MySpinnerAdapter;
import com.tutorials.camera.data.LocalData;
import com.tutorials.camera.data.LocalParams;
import com.tutorials.camera.interfaces.IFolders;
import com.tutorials.camera.interfaces.IUsers;
import com.tutorials.camera.models.Folder;
import com.tutorials.camera.models.FolderDao;
import com.tutorials.camera.models.Mode;
import com.tutorials.camera.models.Picture;
import com.tutorials.camera.models.PictureDao;
import com.tutorials.camera.models.User;
import com.tutorials.camera.models.UserDao;
import com.tutorials.camera.services.UploadService;
import com.tutorials.camera.tools.AppTools;
import com.tutorials.camera.tools.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener
{
    private AppCompatSpinner branchesSpr;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null)
            {
                int resultCode = bundle.getInt(UploadService.RESULT);
                if (resultCode == RESULT_OK)
                {
                    setPending();
                }
            }
        }
    };

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
        setContentView(R.layout.activity_home);

        setPending();

        FloatingActionButton syncBtn = findViewById(R.id.syncBtn);
        FloatingActionButton btnLogout = findViewById(R.id.btnLogout);
        FloatingActionButton configBtn  = findViewById(R.id.configBtn);
        syncBtn.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        configBtn.setOnClickListener(this);

        AppCompatButton captureBtn = findViewById(R.id.captureBtn);
        captureBtn.setOnClickListener(this);
        branchesSpr = findViewById(R.id.branchesSpr);

        loadList();

        if(user!=null && user.getBranchId()==null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();

            IUsers iUsers = RetrofitClient.getRetrofitInstance(HomeActivity.this).create(IUsers.class);
            String token = String.format("Bearer %s", user.getToken());
            Call<User> call = iUsers.get(token);

            call.enqueue(new Callback<User>()
            {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response)
                {
                    progressDialog.dismiss();
                    if(response.isSuccessful())
                    {
                        User _user = response.body();
                        if(_user!=null)
                        {
                            SCamera.getInstance().getCurrentUser().setBranchId(_user.getBranchId());
                            if(SCamera.getInstance().getMode().getModeType()== Mode.ModeType.Auto)
                            {
                                UserDao userDao = SCamera.getInstance().getDaoSession().getUserDao();
                                userDao.insertOrReplace(user);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t)
                {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Connection Not found",Toast.LENGTH_LONG).show();
                }
            });
        }

        /*findViewById(R.id.logPending).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                load200();
            }
        });*/
    }

    private void load200()
    {
        Toast.makeText(getApplicationContext(),"Launch1",Toast.LENGTH_SHORT).show();
        FolderDao folderDao = SCamera.getInstance().getDaoSession().getFolderDao();
        List<Folder> folderList = folderDao.loadAll();
        Folder[] folders = folderList.toArray(new Folder[0]);

        PictureDao pictureDao = SCamera.getInstance().getDaoSession().getPictureDao();
        List<Picture> pictureList = pictureDao.queryBuilder().where(PictureDao.Properties.Uploaded.eq(false)).list();
        Picture[] pictures = pictureList.toArray(new Picture[0]);

        List<Picture> list = new ArrayList<>();

        Random rand = new Random();

        Long integer=10L;
        while (integer<200)
        {
            /*for(Picture picture:pictureList)
            {
                integer++;
                Picture pictureCopy = Picture.copy(picture);
                pictureCopy.setId(integer);
                list.add(pictureCopy);
            }*/
        }

        /*for(Long integer=1L;integer<201;integer++)
        {
            int pictureNum = rand.nextInt((2) + 1);

            for(Picture picture:pictureList)
            {
                integer++;
                Picture pictureCopy = Picture.copy(picture);
                pictureCopy.setId(integer);
                list.add(pictureCopy);
            }
            
            
            
            Picture picture = pictures[pictureNum];
            picture.setId(Long.parseLong(integer+""));
            list.add(picture);
            *//*int folderNum = rand.nextInt((4) + 1);
            int pictureNum = rand.nextInt((2) + 1);

            File path = new File(SCamera.getInstance().getFolderName(folders[folderNum].getFolderString()));

            if(path.exists() || path.mkdirs())
            {
                File file = new File(path,String.format("%s.jpg",AppTools.getUniqueString()));
                try
                {
                    AppTools.copyFile(pictures[pictureNum].getFilePath(),file.getAbsolutePath());

                    Picture picture = new Picture();
                    picture.setCode(pictures[pictureNum].getCode());
                    picture.setDescription(pictures[pictureNum].getDescription());

                    picture.setFilePath(file.getAbsolutePath());
                    picture.setFolderId(folders[folderNum].getFolderId());
                    picture.setFolder(folders[folderNum].getFolderString());

                    picture.setBarCode(pictures[pictureNum].getBarCode());

                    picture.setUploaded(false);
                    picture.setUserId(SCamera.getInstance().getCurrentUser().getUserId());
                    list.add(picture);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }*//*
        }*/

        if(list.size()>0)
        {
            pictureDao.insertOrReplaceInTx(list);
        }
        Toast.makeText(getApplicationContext(),"Launch2",Toast.LENGTH_SHORT).show();
    }


    private void loadList()
    {
        List<Folder> list = new ArrayList<>();

        FolderDao folderDao = SCamera.getInstance().getDaoSession().getFolderDao();
        List<Folder> folders = folderDao.loadAll();
        String defaultFolder = new LocalData(this).getString("defaultFolder");
        Integer selected = Integer.MIN_VALUE;
        if(folders.size()>0)
        {
            list.add(new Folder(Long.MIN_VALUE,getString(R.string.please_select_folder)));
            //list.addAll(folders);
            for(int i=0;i<folders.size();i++)
            {
                list.add(folders.get(i));
                if(defaultFolder!=null && defaultFolder.equals(folders.get(i).getFolderString()))
                {
                    selected = i;
                }
            }
        }

        MySpinnerAdapter mySpinnerAdapter = new MySpinnerAdapter(this,list);
        branchesSpr.setAdapter(mySpinnerAdapter);

        if(selected!=Integer.MIN_VALUE)
        {
            branchesSpr.setSelection((selected+1));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setPending();
        registerReceiver(receiver, new IntentFilter(UploadService.NOTIFICATION));
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.configBtn:
                //AppTools.loadConfigDialog(this);
                String[] colors = {getString(R.string.server_config),
                        getString(R.string.folders_sync),getString(R.string.default_folder),
                        getString(R.string.gallery)};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.parameters));
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        switch (which)
                        {
                            case 0:
                                AppTools.loadConfigDialog(HomeActivity.this);
                                break;
                            case 1:
                                syncFolders();
                                break;
                            case 2:
                                defaultFolders();
                                break;
                            case 3:
                                startActivity(new Intent(getApplicationContext(),GalleryActivity.class));
                                break;
                        }
                    }
                });
                builder.show();
                break;

            case R.id.captureBtn:
                capturePicture();
                break;

            case R.id.syncBtn:
                syncPictures();
                break;

            case R.id.btnLogout:
                SCamera.getInstance().setCurrentUser(null);
                startActivity(new Intent(getApplicationContext(),AuthenticationActivity.class));
                finish();
                break;
        }
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
                new LocalData(HomeActivity.this).setString("defaultFolder", finalFoldersString[which]);
                loadList();
            }
        });
        builder.show();
    }

    private void syncFolders()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.folders_sync));
        progressDialog.show();

        User user = SCamera.getInstance().getCurrentUser();
        String token = String.format("Bearer %s", user.getToken());
        IFolders iFolders = RetrofitClient.getRetrofitInstance(HomeActivity.this).create(IFolders.class);
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
                        loadList();
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
                Toast.makeText(getApplicationContext(),new Exception(t).getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void syncPictures()
    {
        Intent i= new Intent(this, UploadService.class);
        startService(i);
        //bindService(i, networkServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /*private ServiceConnection networkServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            networkService = new Messenger(service);
            try {
                Message msg = Message.obtain(null, NetworkService.MSG_REGISTER_CLIENT);
                msg.replyTo = messenger;
                networkService.send(msg);
                log.debug("Connected to service");

            } catch (RemoteException e) {
                // Here, the service has crashed even before we were able to connect
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };*/

        private void setPending()
    {
        PictureDao pictureDao = SCamera.getInstance().getDaoSession().getPictureDao();
        List<Picture> pictures = pictureDao.queryBuilder().where(PictureDao.Properties.Uploaded.eq(false)).list();
        int color = ContextCompat.getColor(getApplicationContext(), R.color.white);
        ((FloatingActionButton)findViewById(R.id.logPending)).setImageBitmap(textAsBitmap((pictures.size()+""),20,color));
        //((FloatingActionButton)findViewById(R.id.logPending)).setImageBitmap(textAsBitmap("25",20,color));
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

    private void capturePicture()
    {
        if(AppTools.checkPermission(this))
        {
            if(branchesSpr.getSelectedItem()!=null)
            {
                Folder folder = (Folder)branchesSpr.getSelectedItem();
                if(folder.getFolderId()!=Long.MIN_VALUE)
                {
                    SCamera.getInstance().setFolder(folder);
                    SCamera.getInstance().setFolderName(branchesSpr.getSelectedItem().toString());
                    startActivity(new Intent(HomeActivity.this,CaptureActivity.class));
                }
                else
                {
                    Toast.makeText(this,"Please select your folder", Toast.LENGTH_LONG).show();
                }

            }
            else
            {
                Toast.makeText(this,"Please select your folder", Toast.LENGTH_LONG).show();
            }

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
                    capturePicture();
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
}
