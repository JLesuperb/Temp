package com.tutorials.camera.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.tutorials.camera.R;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;

public class TestActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener
{
    private BarcodeReader barcodeReader;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_fragment);
        if(barcodeReader!=null)
            barcodeReader.setListener(this);

    }

    @Override
    public void onScanned(final Barcode barcode)
    {
        if(barcodeReader!=null)
            barcodeReader.playBeep();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Barcode: " + barcode.displayValue, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onScannedMultiple(List<Barcode> barCodes)
    {
        StringBuilder codes = new StringBuilder();
        for (Barcode barcode : barCodes) {
            codes.append(barcode.displayValue).append(", ");
        }

        final String finalCodes = codes.toString();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "BarCodes: " + finalCodes, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Data", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onScanError(String errorMessage)
    {
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCameraPermissionDenied()
    {
        Toast.makeText(getApplicationContext(), "Camera permission denied!", Toast.LENGTH_LONG).show();
    }
}
