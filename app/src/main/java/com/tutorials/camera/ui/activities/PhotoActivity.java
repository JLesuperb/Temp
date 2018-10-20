package com.tutorials.camera.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.tutorials.camera.R;
import com.tutorials.camera.tools.AppTools;
import com.tutorials.camera.tools.CameraPreview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private static String TAG = PhotoActivity.class.getCanonicalName();
    private Camera mCamera;
    private CameraPreview mPreview;
    private int focusState = 1;
    private FloatingActionButton focusBtn;
    private File file;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        if(AppTools.checkPermission(this))
        {
            initViews();
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA}, 1);
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
            case 1:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
                    initViews();
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

    private void initViews()
    {
        // Create an instance of Camera
        mCamera = getCameraInstance();

        if(mCamera!=null)
        {
            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = findViewById(R.id.camera_preview);
            preview.addView(mPreview);
            preview.setOnLongClickListener(this);

            // Add a listener to the Capture button
            FloatingActionButton captureButton = findViewById(R.id.button_capture);
            captureButton.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            /*try
                            {
                                String uriPath = "file:///android_asset/effects/camera_effect.mp3";
                                Uri uri = Uri.parse(uriPath);
                                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), uri);
                                r.play();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*/
                            // get an image from the camera
                            mCamera.takePicture(null, null, mPicture);
                        }
                    }
            );

            String picturePath = getIntent().getStringExtra("picturePath");
            file = new File(picturePath);

            focusBtn = findViewById(R.id.focusBtn);
            focusBtn.setOnClickListener(this);

            /*Button button_draw = findViewById(R.id.button_draw);
            button_draw.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // get an image from the camera
                            Intent intent = new Intent(PhotoActivity.this,DrawOnBitmapActivity.class);
                            startActivity(intent);
                        }
                    }
            );*/
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = AppTools.getOutputMediaFile(AppTools.MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    /** A safe way to get an instance of the Camera object. */
    public Camera getCameraInstance()
    {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e)
        {
            Log.e("MYAPP", "exception", e);
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.focusBtn:
                switch (focusState)
                {
                    case 1:
                        focusState = 2;
                        setFlash(Camera.Parameters.FLASH_MODE_ON);
                        focusBtn.setImageResource(R.drawable.ic_outline_flash_on_white);
                        break;
                    case 2:
                        focusState = 3;
                        setFlash(Camera.Parameters.FLASH_MODE_OFF);
                        focusBtn.setImageResource(R.drawable.ic_outline_flash_off_white);
                        break;
                    default:
                        focusState = 1;
                        setFlash(Camera.Parameters.FLASH_MODE_AUTO);
                        focusBtn.setImageResource(R.drawable.ic_outline_flash_auto_white);
                            break;
                }
                break;
        }
    }

    private void setFlash(String flashMode)
    {
        if(mCamera!=null)
        {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(flashMode);
            mCamera.setParameters(parameters);
        }
    }

    @Override
    public boolean onLongClick(View view)
    {
        if(mCamera!=null)
        {
            setFlash(Camera.Parameters.FLASH_MODE_TORCH);
        }
        return false;
    }

    @Override
    public void onBackPressed()
    {
        Intent intent=new Intent();
        setResult(RESULT_OK,intent);
        finish();
        //super.onBackPressed();
    }
}
