package com.tutorials.camera.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.FileProvider;
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

import id.zelory.compressor.Compressor;

public class CaptureActivity extends AppCompatActivity
        implements
        View.OnClickListener,
        View.OnLongClickListener
{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_BARCODE_CAPTURE = 2;

    private File currentFile = null;
    private File tempFile = null;
    private Boolean isQuery = false;

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
        findViewById(R.id.captureBtn).setOnClickListener(this);

        dispatchTakePictureIntent();
        TextInputEditText barCodeEdt = findViewById(R.id.barCodeEdt);
        barCodeEdt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean b)
            {
                if(b && !isQuery)
                {
                    dispatchTakeBarCodeIntent();
                    isQuery = true;
                    view.clearFocus();
                }
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
                    //tempFile = new File(path,"_temp.jpg");
                    Uri imageUri = FileProvider.getUriForFile(this,"com.tutorials.camera",tempFile);
                    //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                catch (Exception ex)
                {
                    tempFile = null;
                    Toast.makeText(getApplicationContext(), ex.getMessage(),Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), ex.getMessage(),Toast.LENGTH_LONG).show();
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
                    try
                    {
                        File path = new File(SCamera.getInstance().getFolderName(".tmp"),String.format("%s",AppTools.getUniqueString()));
                        currentFile = new Compressor(this)
                                .setDestinationDirectoryPath(path.getAbsolutePath())
                                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                .compressToFile(tempFile);
                        //currentFile = new Compressor(this).setDestinationDirectoryPath(path.getAbsolutePath()).compressToFile(tempFile);
                        //currentFile = tempFile;
                        AppCompatImageView imagePreview = findViewById(R.id.imagePreview);
                        Glide.with(this).load(currentFile.getAbsolutePath()).into(imagePreview);
                    }
                    catch (IOException e)
                    {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                if(currentFile==null)
                    finish();
            }

        }
        else if (requestCode == REQUEST_BARCODE_CAPTURE)
        {
            isQuery = false;
            if(resultCode == RESULT_OK)
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
            case R.id.captureBtn:
                dispatchTakePictureIntent();
                break;
        }
    }

    private void saveData()
    {
        SCamera app = SCamera.getInstance();
        TextInputEditText codeEdt = findViewById(R.id.codeEdt);
        codeEdt.setError(null);
        TextInputEditText descEdt = findViewById(R.id.descEdt);
        descEdt.setError(null);
        TextInputEditText barCodeEdt = findViewById(R.id.barCodeEdt);
        if(codeEdt.getText()!=null && descEdt.getText()!=null && barCodeEdt.getText()!=null)
        {
            String codeText = codeEdt.getText().toString();
            String descText = codeEdt.getText().toString();
            String barCodeText = codeEdt.getText().toString();
            if(!codeText.trim().isEmpty() && !descText.trim().isEmpty() || !barCodeText.trim().isEmpty())
            {
                File path = new File(app.getFolderName());
                {
                    try
                    {

                        if(path.mkdirs() || path.exists())
                        {
                            //currentFile.renameTo(new File(String.format("%s.jpg",AppTools.getUniqueString())));

                            /*if (currentFile.exists())
                                currentFile.delete();
                            currentFile.createNewFile();
                            FileOutputStream fos = new FileOutputStream(currentFile);
                            fos.write(fileData);
                            fos.flush();
                            fos.close();*/

                            File file = new File(path,currentFile.getName());
                            copyFile(currentFile.getAbsolutePath(),file.getAbsolutePath());




                            Picture picture = new Picture();
                            picture.setCode(codeEdt.getText().toString());
                            picture.setDescription(descEdt.getText().toString());

                            picture.setFilePath(file.getAbsolutePath());
                            picture.setFolderId(SCamera.getInstance().getFolder().getFolderId());
                            picture.setFolder(SCamera.getInstance().getFolderName());


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
            else
            {
                if(codeText.trim().isEmpty())
                {
                    codeEdt.setError("Please insert the code");
                }
                if(descText.trim().isEmpty())
                {
                    descEdt.setError("Please insert the description");
                }
            }
        }
        else
        {
            if(codeEdt.getText()==null)
            {
                codeEdt.setError("Please insert the code");
            }
            if(descEdt.getText()==null)
            {
                descEdt.setError("Please insert the description");
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
        finish();
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
