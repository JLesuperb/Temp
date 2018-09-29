package com.tutorials.camera.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import com.tutorials.camera.models.Picture;
import com.tutorials.camera.models.PictureDao;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FloatingActionButton syncBtn = findViewById(R.id.syncBtn);
        syncBtn.setOnClickListener(this);

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
                ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
                progressDialog.setCancelable(false);
                //progressDialog.show();
                PictureDao pictureDao = SCamera.getInstance().getDaoSession().getPictureDao();
                List<Picture> pictures = pictureDao.loadAll();
                LocalData localData = new LocalData(getApplicationContext());
                for(Picture picture : pictures)
                {
                    IPictures iPictures = RetrofitClient.getRetrofitInstance().create(IPictures.class);
                    String token = String.format("Bearer %s", localData.getString("tokenKey"));
                    File file = new File(picture.getFilePath());

                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("Picture", file.getName(), reqFile);
                    //RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");
                    Call<ResponseBody> call = iPictures.upload(token,body,picture);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response)
                        {
                            if(response.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
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
                        }

                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t)
                        {
                            Toast.makeText(getApplicationContext(),new Exception(t).getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
                break;
        }
    }

    private void capturePicture()
    {
        startActivity(new Intent(HomeActivity.this,CameraActivity.class));
        SCamera.getInstance().setFolderName(branchesSpr.getSelectedItem().toString());
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
