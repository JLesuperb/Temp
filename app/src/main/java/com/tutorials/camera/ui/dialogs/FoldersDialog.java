package com.tutorials.camera.ui.dialogs;

import android.support.v4.app.DialogFragment;

public class FoldersDialog extends DialogFragment
{
    private static FoldersDialog _instance;

    public static FoldersDialog getInstance()
    {
        if(_instance==null)
        {
            _instance = new FoldersDialog();
        }
        return _instance;
    }
}
