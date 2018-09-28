package com.tutorials.camera.ui.fragments.capture;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tutorials.camera.R;
import com.tutorials.camera.ui.fragments._BaseFragment;

public class FormFragment extends _BaseFragment
{
    private static FormFragment _instance;

    public static FormFragment getInstance()
    {
        if (_instance == null) {
            _instance = new FormFragment();
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
        return inflater.inflate(R.layout.fragment_capture_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        AppCompatEditText barCodeEdt = view.findViewById(R.id.barCodeEdt);
        barCodeEdt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean b)
            {
                if(b)
                {
                    if(getActivity()!=null)
                    {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        CodeBarFragment fragment = CodeBarFragment.getInstance();
                        fragmentManager
                                .beginTransaction()
                                .hide(FormFragment.this)
                                .add(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }
                view.clearFocus();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
