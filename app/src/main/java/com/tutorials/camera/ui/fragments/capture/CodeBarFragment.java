package com.tutorials.camera.ui.fragments.capture;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.tutorials.camera.R;
import com.tutorials.camera.ui.fragments._BaseFragment;

import java.io.IOException;

public class CodeBarFragment extends _BaseFragment implements SurfaceHolder.Callback, Detector.Processor<Barcode> {
    private static CodeBarFragment _instance;
    private CameraSource cameraSource;

    public static CodeBarFragment getInstance() {
        if (_instance == null) {
            _instance = new CodeBarFragment();
        }
        return _instance;
    }

    public CodeBarFragment() {
        //Must be empty
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        /*if (savedInstanceState != null)
        {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }*/
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_capture_bar_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SurfaceView surfaceView = view.findViewById(R.id.surfaceView);
        /*new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run() {
                if(getActivity()!=null)
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        },3000);*/

        if (getContext() != null)
        {
            final BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getContext())
                    .setBarcodeFormats(Barcode.QR_CODE)
                    .build();

            cameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
                    .setAutoFocusEnabled(true) //you should add this feature
                    .build();
            surfaceView.getHolder().addCallback(this);

            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> barCodes = detections.getDetectedItems();
                    if (barCodes.size() != 0)
                    {
                        String text = barCodes.get(0).displayValue;
                        if(getContext()!=null)
                            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                    }
                }
            });


            Detector<Barcode> detector = new Detector<Barcode>() {
                @Override
                public SparseArray<Barcode> detect(Frame frame) {
                    return barcodeDetector.detect(frame);
                }
            };

            detector.setProcessor(this);
        }


    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        if(getContext()!=null)
        {
            try
            {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                {
                    if (surfaceHolder.getSurface() == null)
                    {
                        // preview surface does not exist
                        return;
                    }

                    try
                    {
                        cameraSource.stop();
                        //cameraSource.release();
                    }
                    catch (Exception e)
                    {
                        // ignore: tried to stop a non-existent preview
                    }

                    cameraSource.start(surfaceHolder);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
        cameraSource.stop();
    }

    @Override
    public void release()
    {
        if(getContext()!=null)
            Toast.makeText(getContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections)
    {
        SparseArray<Barcode> barCodes = detections.getDetectedItems();
        String text = barCodes.get(0).displayValue;

        if(getContext()!=null)
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }
}
