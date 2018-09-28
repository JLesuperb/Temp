package com.tutorials.camera.ui.fragments.capture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.ui.fragments._BaseFragment;

public class ConfirmationFragment extends _BaseFragment implements View.OnClickListener {
    private static ConfirmationFragment _instance;

    public static ConfirmationFragment getInstance()
    {
        if (_instance == null) {
            _instance = new ConfirmationFragment();
        }
        return _instance;
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
        return inflater.inflate(R.layout.fragment_capture_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        byte[] bytes = SCamera.getInstance().getBytes();
        if(bytes!=null)
        {
            AppCompatImageView imagePreview = view.findViewById(R.id.imagePreview);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imagePreview.setImageBitmap(bitmap);
            AppCompatButton okBtn = view.findViewById(R.id.okBtn);
            okBtn.setOnClickListener(this);
            AppCompatButton cancelBtn = view.findViewById(R.id.cancelBtn);
            cancelBtn.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.cancelBtn:
                if(getActivity()!=null)
                    getActivity().getSupportFragmentManager().popBackStackImmediate() ;
                break;
            case R.id.okBtn:
                if(getActivity()!=null)
                {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FormFragment fragment = FormFragment.getInstance();
                    fragmentManager
                            .beginTransaction()
                            .hide(this)
                            .add(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
                break;
        }
    }
}
