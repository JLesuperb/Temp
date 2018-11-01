package com.tutorials.camera.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.zxing.Result;
import com.tutorials.camera.R;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ZXingActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, View.OnClickListener {
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //mScannerView = new ZXingScannerView(this);
        //setContentView(mScannerView);
        setContentView(R.layout.activity_zxing);
        mScannerView = findViewById(R.id.scannerView);
        findViewById(R.id.flashButton).setOnClickListener(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();// Stop camera on pause
    }

    @Override
    public void handleResult(Result result)
    {
        Intent intent=new Intent();
        intent.putExtra("data",result.getText());
        setResult(RESULT_OK,intent);
        finish();
        // If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent=new Intent();
        setResult(RESULT_CANCELED,intent);
        finish();
        //super.onBackPressed();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.flashButton:
                mScannerView.setFlash(!mScannerView.getFlash());
                break;
        }
    }
}
