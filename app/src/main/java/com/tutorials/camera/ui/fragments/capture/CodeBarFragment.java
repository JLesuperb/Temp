package com.tutorials.camera.ui.fragments.capture;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tutorials.camera.R;
import com.tutorials.camera.ui.fragments._BaseFragment;

public class CodeBarFragment extends _BaseFragment
{
    private static CodeBarFragment _instance;

    public static CodeBarFragment getInstance()
    {
        if (_instance == null) {
            _instance = new CodeBarFragment();
        }
        return _instance;
    }

    public CodeBarFragment() {
        //Must be empty
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
        return inflater.inflate(R.layout.fragment_capture_bar_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run() {
                if(getActivity()!=null)
                    getActivity().getSupportFragmentManager().popBackStackImmediate() ;
            }
        },3000);

    }
}
