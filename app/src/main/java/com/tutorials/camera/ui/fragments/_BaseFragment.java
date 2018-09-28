package com.tutorials.camera.ui.fragments;

import android.support.v4.app.Fragment;

public class _BaseFragment extends Fragment
{
    protected Fragment parent;

    /**
     * Could handle back press.
     * @return true if back press was handled
     */
    public boolean onBackPressed()
    {
        return false;
    }

    public void setParent(Fragment parent)
    {
        this.parent = parent;
    }

    protected Fragment getParent()
    {
        return this.parent;
    }
}
