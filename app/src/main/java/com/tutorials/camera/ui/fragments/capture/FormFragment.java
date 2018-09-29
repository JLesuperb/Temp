package com.tutorials.camera.ui.fragments.capture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.models.Picture;
import com.tutorials.camera.models.PictureDao;
import com.tutorials.camera.tools.AppTools;
import com.tutorials.camera.ui.fragments._BaseFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FormFragment extends _BaseFragment implements View.OnClickListener {
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

        AppCompatImageView imagePreview = view.findViewById(R.id.imagePreview);
        byte[] bytes = SCamera.getInstance().getBytes();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imagePreview.setImageBitmap(bitmap);

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

        AppCompatButton saveBtn = view.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);
        AppCompatButton cancelBtn = view.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public boolean onBackPressed()
    {
        if(getView()!=null)
        {
            AppCompatButton cancelBtn = getView().findViewById(R.id.cancelBtn);
            cancelBtn.performClick();
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.saveBtn:
                SCamera app = SCamera.getInstance();
                byte[] bytes = app.getBytes();
                if(getView()!=null && bytes!=null)
                {

                    try
                    {
                        File path = new File(app.getFolderName());
                        if(path.mkdirs() || path.exists())
                        {
                            File file = new File(path,String.format("%s.jpg", AppTools.getUniqueString()));
                            FileOutputStream outStream = new FileOutputStream(file.getAbsolutePath());
                            outStream.write(bytes);
                            outStream.close();
                            AppCompatEditText codeEdt = getView().findViewById(R.id.codeEdt);
                            AppCompatEditText descEdt = getView().findViewById(R.id.descEdt);

                            Picture picture = new Picture();
                            if(codeEdt.getText()!=null)
                                picture.setCode(codeEdt.getText().toString());
                            if(descEdt.getText()!=null)
                                picture.setDescription(descEdt.getText().toString());
                            picture.setFilePath(file.getAbsolutePath());
                            picture.setFolder(SCamera.getInstance().getFolderName());

                            picture.setUploaded(false);
                            picture.setUserId(1L);

                            PictureDao pictureDao = app.getDaoSession().getPictureDao();
                            pictureDao.insert(picture);

                            Toast.makeText(getContext(),"Saved",Toast.LENGTH_LONG).show();
                            if(getActivity()!=null)
                                getActivity().finish();
                        }
                        else
                        {
                            Toast.makeText(getContext(),"Can't create folder",Toast.LENGTH_LONG).show();
                        }

                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }


                }
                break;

            case R.id.cancelBtn:
                if(getContext()!=null)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setTitle("Confirmation");

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                break;
        }
    }
}
