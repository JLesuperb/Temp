package com.tutorials.camera.ui.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.data.LocalParams;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener
{
    private AppCompatSpinner branchesSpr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        AppCompatButton captureBtn = findViewById(R.id.captureBtn);
        captureBtn.setOnClickListener(this);
        branchesSpr = findViewById(R.id.branchesSpr);
        List<String> list = new ArrayList<>();
        list.add("list 1");
        list.add("list 2");
        list.add("list 3");
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
        }
    }

    private void capturePicture()
    {
        startActivity(new Intent(HomeActivity.this,CameraActivity.class));
        SCamera.getInstance().setFolderName(branchesSpr.getSelectedItem().toString());
        /*if(Permissions.checkCameraPermission(getApplicationContext()))
        {
            startActivity(new Intent(HomeActivity.this,AndroidSurfaceView.class));
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, LocalParams.CAMERA);
            }
        }*/
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
