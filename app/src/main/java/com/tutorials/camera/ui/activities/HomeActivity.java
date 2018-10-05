package com.tutorials.camera.ui.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.Toast;

import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.custom.MySpinnerAdapter;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener
{
    private AppCompatSpinner branchesSpr;

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

            IUsers iUsers = RetrofitClient.getRetrofitInstance(SCamera.getInstance().getServerString()).create(IUsers.class);
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
    }

    private void loadList()
    {
        List<Folder> list = new ArrayList<>();

        FolderDao folderDao = SCamera.getInstance().getDaoSession().getFolderDao();
        List<Folder> folders = folderDao.loadAll();
        if(folders.size()>0)
        {
            list.add(new Folder(Long.MIN_VALUE," "));
            list.addAll(folders);
        }

        /*ArrayAdapter<Folder> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchesSpr.setAdapter(dataAdapter);*/
        MySpinnerAdapter mySpinnerAdapter = new MySpinnerAdapter(this,list);
        branchesSpr.setAdapter(mySpinnerAdapter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setPending();
        branchesSpr.setSelection(0);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.configBtn:
                //AppTools.loadConfigDialog(this);
                String[] colors = {"Server config", "Folder config"};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Options");
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

    private void syncFolders()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sync Folders...");
        progressDialog.show();

        User user = SCamera.getInstance().getCurrentUser();
        String token = String.format("Bearer %s", user.getToken());
        IFolders iFolders = RetrofitClient.getRetrofitInstance(SCamera.getInstance().getServerString()).create(IFolders.class);
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
        // potentially add data to the intent
        //i.putExtra("KEY1", "Value to be used by the service");
        startService(i);

        /*final PictureDao pictureDao = SCamera.getInstance().getDaoSession().getPictureDao();
        final List<Picture> pictures = pictureDao.queryBuilder().where(PictureDao.Properties.Uploaded.eq(false)).list();
        if(pictures.size()>0)
        {
            for (Picture picture : pictures)
            {

                //uploadPicture(pictures.get(0));
                //uploadPicture(picture);
            }
        }*/

        //region comment
        /*if(pictures.size()>0)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
            progressDialog.setCancelable(false);
            progressDialog.show();

            String root = Environment.getExternalStorageDirectory().toString();
            for(final Picture picture : pictures)
            {
                IPictures iPictures = RetrofitClient.getRetrofitInstance(new LocalData(getApplicationContext()).getString("serverAddress")).create(IPictures.class);
                String token = SCamera.getInstance().getCurrentUser().getToken();
                File file = new File(picture.getFilePath());

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("Picture", file.getName(), reqFile);

                RequestBody code = RequestBody.create(okhttp3.MultipartBody.FORM, picture.getCode());
                RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, picture.getDescription());
                RequestBody barCode = RequestBody.create(okhttp3.MultipartBody.FORM, picture.getBarCode());
                String phoneFolder = picture.getFilePath().replace(root,"");
                RequestBody filePath = RequestBody.create(okhttp3.MultipartBody.FORM, phoneFolder);
                RequestBody userId = RequestBody.create(okhttp3.MultipartBody.FORM, picture.getUserId().toString());
                RequestBody folderName = RequestBody.create(okhttp3.MultipartBody.FORM, file.getParentFile().getName());

                Call<ResponseBody> call = iPictures.upload(token,body,code,description,barCode,filePath,userId,folderName);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response)
                    {
                        if(response.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                            picture.setUploaded(true);
                            pictureDao.update(picture);
                        }
                        else
                        {
                            ResponseBody responseBody = response.errorBody();
                            if(responseBody!=null)
                            {
                                try {
                                    Toast.makeText(getApplicationContext(),responseBody.string(),Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        pictures.remove(picture);
                        if(pictures.size()==0)
                            progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t)
                    {
                        Toast.makeText(getApplicationContext(),"Server not found",Toast.LENGTH_LONG).show();
                        pictures.remove(picture);
                        if(pictures.size()==0)
                            progressDialog.dismiss();
                    }
                });
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Already synchronized",Toast.LENGTH_LONG).show();
        }*/
        //endregion
    }

    private void setPending()
    {
        PictureDao pictureDao = SCamera.getInstance().getDaoSession().getPictureDao();
        List<Picture> pictures = pictureDao.queryBuilder().where(PictureDao.Properties.Uploaded.eq(false)).list();
        ((FloatingActionButton)findViewById(R.id.logPending)).setImageBitmap(textAsBitmap((pictures.size()+""),1,1));
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
