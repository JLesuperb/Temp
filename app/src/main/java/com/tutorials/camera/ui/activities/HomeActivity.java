package com.tutorials.camera.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.data.LocalData;
import com.tutorials.camera.data.LocalParams;
import com.tutorials.camera.interfaces.IPictures;
import com.tutorials.camera.interfaces.IUsers;
import com.tutorials.camera.models.Mode;
import com.tutorials.camera.models.Picture;
import com.tutorials.camera.models.PictureDao;
import com.tutorials.camera.models.User;
import com.tutorials.camera.models.UserDao;
import com.tutorials.camera.tools.RetrofitClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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

        FloatingActionButton syncBtn = findViewById(R.id.syncBtn);
        FloatingActionButton btnLogout = findViewById(R.id.btnLogout);
        syncBtn.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

        AppCompatButton captureBtn = findViewById(R.id.captureBtn);
        captureBtn.setOnClickListener(this);
        branchesSpr = findViewById(R.id.branchesSpr);

        List<String> list = new ArrayList<>();

        list.add("Kolwezi");
        list.add("Likasi");
        list.add("Kananga");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchesSpr.setAdapter(dataAdapter);

        if(user!=null && user.getBranchId()==null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();

            LocalData localData = new LocalData(getApplicationContext());
            IUsers iUsers = RetrofitClient.getRetrofitInstance(localData.getString("serverAddress")).create(IUsers.class);
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

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
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

    private void syncPictures()
    {
        final PictureDao pictureDao = SCamera.getInstance().getDaoSession().getPictureDao();
        final List<Picture> pictures = pictureDao.queryBuilder().where(PictureDao.Properties.Uploaded.eq(false)).list();
        if(pictures.size()>0)
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
        }

    }

    private void capturePicture()
    {
        SCamera.getInstance().setFolderName(branchesSpr.getSelectedItem().toString());
        startActivity(new Intent(HomeActivity.this,CaptureActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case LocalParams.CAMERA:
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
