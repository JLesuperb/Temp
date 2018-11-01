package com.tutorials.camera.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.tutorials.camera.R;
import com.tutorials.camera.ui.fragments._BaseFragment;
import com.tutorials.camera.ui.fragments.gallery.FoldersFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity
{
    List<WeakReference<Fragment>> fragList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        if(savedInstanceState!=null)
        {
            return;
        }

        FoldersFragment foldersFragment = FoldersFragment.getInstance();
        foldersFragment.setArguments(getIntent().getExtras());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, foldersFragment).commit();
    }

    @Override
    public void onAttachFragment (Fragment fragment)
    {
        fragList.add(new WeakReference<>(fragment));
    }


    @Override
    public void onBackPressed()
    {
        boolean handled = false;
        for(WeakReference<Fragment> ref : fragList)
        {
            Fragment f = ref.get();
            if(f != null)
            {
                if(f.isVisible())
                {
                    handled = ((_BaseFragment)f).onBackPressed();
                    if(handled)
                    {
                        break;
                    }
                }
            }
        }

        if(!handled)
        {
            //this.moveTaskToBack(true);
            super.onBackPressed();
        }
    }

}
