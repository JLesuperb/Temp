package com.tutorials.camera.ui.fragments.capture;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;
import com.tutorials.camera.ui.fragments._BaseFragment;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ZXingFragment extends _BaseFragment implements ZXingScannerView.ResultHandler
{

    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(getContext());   // Programmatically initialize the scanner view
        //setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        /*if (savedInstanceState != null)
        {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }*/
        // Inflate the layout for this fragment
        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result result)
    {
        if(getContext()!=null)
        {
            Toast.makeText(getContext(),result.getText(),Toast.LENGTH_LONG).show();
        }
    }
}
