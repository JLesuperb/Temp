package com.tutorials.camera.ui.fragments.gallery;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.tutorials.camera.R;
import com.tutorials.camera.models.Picture;
import com.tutorials.camera.ui.fragments._BaseFragment;

import java.io.File;

public class DetailFragment extends _BaseFragment
{
    private static DetailFragment _instance;

    public static DetailFragment getInstance()
    {
        if(_instance==null)
        {
            _instance = new DetailFragment();
        }
        return _instance;
    }

    public DetailFragment()
    {
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
        return inflater.inflate(R.layout.fragment_gallery_detail, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if(getArguments()==null)
            return;
        Picture picture = (Picture) getArguments().getSerializable("picture");
        if(picture==null)
            return;

        PhotoView photoView = view.findViewById(R.id.photoView);
        Glide
                .with(getContext())
                .load(Uri.fromFile(new File(picture.getPicturePath())))
                .asBitmap()
                .into(photoView);
    }
}
