package com.tutorials.camera.ui.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.adapters.BitmapAdapter;
import com.tutorials.camera.data.LocalData;
import com.tutorials.camera.models.Invoice;
import com.tutorials.camera.models.InvoiceDao;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import id.zelory.compressor.Compressor;

public class CaptureActivity extends AppCompatActivity
        implements
        View.OnClickListener,
        View.OnLongClickListener, BitmapAdapter.ImageViewClickListener, BitmapAdapter.ImageViewLongClickListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_CAPTURE2 = 5;
    static final int EXTERNAL_IMAGE_CAPTURE = 3;
    static final int REQUEST_BARCODE_CAPTURE = 2;

    private File currentFile = null;
    private File tempFile = null;
    private Boolean isQuery = false;
    private BitmapAdapter bitmapAdapter;

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

        bitmapAdapter = new BitmapAdapter();
        bitmapAdapter.setClickListener(this);
        bitmapAdapter.setLongClickListener(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.HORIZONTAL));
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setAdapter(bitmapAdapter);

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
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE2);
            /*File path = new File(SCamera.getInstance().getFolderName(".tmp"));
            AppTools.clean(path);
            if(path.exists() || path.mkdirs())
            {
                try
                {
                    tempFile = new File(path,String.format("%s.jpg",AppTools.getUniqueString()));
                    Uri imageUri = FileProvider.getUriForFile(this,"com.tutorials.camera",tempFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                catch (Exception ex)
                {
                    tempFile = null;
                    Toast.makeText(getApplicationContext(), ex.getMessage(),Toast.LENGTH_LONG).show();
                }
            }*/
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
                        AppCompatImageView imagePreview = findViewById(R.id.imagePreview);
                        /*Intent intent = new Intent(CaptureActivity.this,PhotoEditorActivity.class);
                        intent.putExtra("filePath",tempFile.getAbsolutePath());
                        startActivityForResult(intent, EXTERNAL_IMAGE_CAPTURE);*/
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
        else if(requestCode==EXTERNAL_IMAGE_CAPTURE)
        {
            if(resultCode == RESULT_OK)
            {
                AppCompatImageView imagePreview = findViewById(R.id.imagePreview);
                Glide.with(this).load(currentFile.getAbsolutePath()).into(imagePreview);
            }
            else
            {
                AppCompatImageView imagePreview = findViewById(R.id.imagePreview);
                Glide.with(this).load(currentFile.getAbsolutePath()).into(imagePreview);
            }
        }
        else if(requestCode==REQUEST_IMAGE_CAPTURE2)
        {
            if(resultCode == RESULT_OK)
            {
                Bundle extras = data.getExtras();
                if(extras!=null)
                {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    AppCompatImageView imagePreview = findViewById(R.id.imagePreview);
                    imagePreview.setImageBitmap(imageBitmap);
                    bitmapAdapter.addBitmap(imageBitmap);
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
        if(codeEdt.getText()!=null || barCodeEdt.getText()!=null && bitmapAdapter.getItemCount()>0)
        {
            String codeText = codeEdt.getText().toString();
            String barCodeText = codeEdt.getText().toString();
            if(!codeText.trim().isEmpty() || !barCodeText.trim().isEmpty())
            {
                File path = new File(app.getFolderName());
                {
                    try
                    {

                        if(path.mkdirs() || path.exists())
                        {
                            LocalData localData = new LocalData(app.getApplicationContext());

                            Date date = localData.getDate("savingDate");
                            Integer pNumber = 1;
                            if(date!=null)
                            {
                                if(AppTools.dateCompare(new Date(),date))
                                {
                                    pNumber = localData.getInteger("pictureNumber");
                                    pNumber = pNumber+1;
                                    localData.setInteger("pictureNumber",pNumber);
                                }
                                else
                                {
                                    date = new Date();
                                    localData.setDate("savingDate",date);
                                    localData.setInteger("pictureNumber",pNumber);
                                }

                            }
                            else
                            {
                                date = new Date();
                                localData.setDate("savingDate",date);
                                localData.setInteger("pictureNumber",pNumber);
                            }

                            Invoice invoice = new Invoice();
                            invoice.setInvoiceCode(codeEdt.getText().toString());
                            if(descEdt.getText()!=null)
                            invoice.setInvoiceDesc(descEdt.getText().toString());

                            invoice.setInvoiceBarCode(barCodeEdt.getText().toString());

                            invoice.setBranchId(app.getCurrentUser().getBranchId());

                            User user = app.getCurrentUser();

                            invoice.setUserId(user.getUserId());
                            invoice.setFolderId(app.getFolder().getFolderId());
                            invoice.setSavingDate(AppTools.getCurrentDate());
                            invoice.setUploaded(false);

                            InvoiceDao invoiceDao = app.getDaoSession().getInvoiceDao();
                            invoiceDao.insert(invoice);

                            @SuppressLint("SimpleDateFormat")
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                            List<Bitmap> bitmaps = bitmapAdapter.getBitmapList();
                            for(int i=0;i<bitmaps.size();i++)
                            {
                                String reportDate = df.format(date);
                                String fileName = String.format("%s-%s-%s-%s.jpg",reportDate,pNumber,i+1,user.getUserName());
                                File file = new File(path, fileName);
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                Bitmap bitmap = bitmaps.get(i);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fileOutputStream);
                                Picture picture = new Picture();
                                picture.setInvoiceId(invoice.getInvoiceId());
                                picture.setInvoice(invoice);
                                picture.setPictureName(fileName);
                                picture.setPicturePath(file.getAbsolutePath());
                                PictureDao pictureDao = app.getDaoSession().getPictureDao();
                                pictureDao.insert(picture);
                            }

                            Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                    catch (FileNotFoundException e)
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
            }
        }
        else
        {
            if(codeEdt.getText()==null)
            {
                codeEdt.setError("Please insert the code");
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

    @Override
    public void viewClicked(Bitmap bitmap)
    {
        AppCompatImageView imagePreview = findViewById(R.id.imagePreview);
        imagePreview.setImageBitmap(bitmap);
    }

    @Override
    public void viewLongClicked(final Bitmap bitmap)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Will you delete the selected picture?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                bitmapAdapter.removeBitmap(bitmap);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
