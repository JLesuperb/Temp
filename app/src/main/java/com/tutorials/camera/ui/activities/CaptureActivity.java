package com.tutorials.camera.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.models.Picture;
import com.tutorials.camera.models.PictureDao;
import com.tutorials.camera.models.User;
import com.tutorials.camera.tools.AppTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CaptureActivity extends AppCompatActivity
        implements
        View.OnClickListener,
        View.OnLongClickListener
{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_BARCODE_CAPTURE = 2;

    private File currentFile = null;
    private File tempFile = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        User user = SCamera.getInstance().getCurrentUser();
        if(user==null)
        {
            startActivity(new Intent(getApplicationContext(),AuthenticationActivity.class));
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_activity);

        findViewById(R.id.saveBtn).setOnClickListener(this);
        findViewById(R.id.cancelBtn).setOnClickListener(this);
        findViewById(R.id.imagePreview).setOnLongClickListener(this);

        dispatchTakePictureIntent();
        TextInputEditText barCodeEdt = findViewById(R.id.barCodeEdt);
        barCodeEdt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean b)
            {
                if(b)
                {
                    dispatchTakeBarCodeIntent();
                }
                view.clearFocus();
            }
        });
    }

    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File path = new File(SCamera.getInstance().getFolderName(".tmp"));
            AppTools.clean(path);
            if(path.exists() || path.mkdirs())
            {
                try
                {
                    tempFile = new File(path,String.format("%s.jpg",AppTools.getUniqueString()));
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                catch (Exception ex)
                {
                    tempFile = null;
                    Toast.makeText(getApplicationContext(), ex.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void dispatchTakeBarCodeIntent()
    {
        Intent takePictureIntent = new Intent(this,ZXingActivity.class);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(takePictureIntent, REQUEST_BARCODE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE)
        {
            if(resultCode == RESULT_OK)
            {
                if(tempFile !=null)
                {
                    currentFile = tempFile;
                    AppCompatImageView imagePreview = findViewById(R.id.imagePreview);
                    Glide.with(this).load(currentFile.getAbsolutePath()).into(imagePreview);
                }
            }
            else
            {
                if(currentFile==null)
                    finish();
            }

        }
        else if (requestCode == REQUEST_BARCODE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            if(extras!=null)
            {
                String text = extras.getString("data");
                TextInputEditText barCodeEdt = findViewById(R.id.barCodeEdt);
                barCodeEdt.setText(text);
            }
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.saveBtn:
                saveData();
                break;
            case R.id.cancelBtn:
                cancelData();
                break;
        }
    }

    private void saveData()
    {
        SCamera app = SCamera.getInstance();
        File path = new File(app.getFolderName());
        {
            try
            {

                if(path.mkdirs() || path.exists())
                {
                    File file = new File(path,currentFile.getName());
                    copyFile(currentFile.getAbsolutePath(),file.getAbsolutePath());

                    TextInputEditText codeEdt = findViewById(R.id.codeEdt);
                    TextInputEditText descEdt = findViewById(R.id.descEdt);
                    TextInputEditText barCodeEdt = findViewById(R.id.barCodeEdt);

                    Picture picture = new Picture();
                    if(codeEdt.getText()!=null)
                        picture.setCode(codeEdt.getText().toString());
                    if(descEdt.getText()!=null)
                        picture.setDescription(descEdt.getText().toString());
                    picture.setFilePath(file.getAbsolutePath());
                    picture.setFolder(SCamera.getInstance().getFolderName());

                    if(barCodeEdt.getText()!=null)
                        picture.setBarCode(barCodeEdt.getText().toString());

                    picture.setUploaded(false);
                    picture.setUserId(app.getCurrentUser().getUserId());

                    PictureDao pictureDao = app.getDaoSession().getPictureDao();
                    pictureDao.insert(picture);

                    Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void copyFile(String from, String to) throws IOException
    {
        File fromFile =new File(from);
        File toFile =new File(to);
        InputStream inStream = new FileInputStream(fromFile);
        OutputStream outStream = new FileOutputStream(toFile);
        byte[] buffer = new byte[1024];

        int length;
        //copy the file content in bytes
        while ((length = inStream.read(buffer)) > 0)
        {
            outStream.write(buffer, 0, length);
        }

        inStream.close();
        outStream.close();
        fromFile.delete();
    }

    private void cancelData()
    {

    }

    @Override
    public boolean onLongClick(View view)
    {
        switch (view.getId())
        {
            case R.id.imagePreview:
                dispatchTakePictureIntent();
                return true;
        }
        return false;
    }
}
