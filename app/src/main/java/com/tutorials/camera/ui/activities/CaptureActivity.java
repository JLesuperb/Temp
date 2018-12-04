package com.tutorials.camera.ui.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.adapters.BitmapAdapter;
import com.tutorials.camera.custom.MySpinnerAdapter;
import com.tutorials.camera.data.LocalData;
import com.tutorials.camera.models.Folder;
import com.tutorials.camera.models.FolderDao;
import com.tutorials.camera.models.Invoice;
import com.tutorials.camera.models.InvoiceDao;
import com.tutorials.camera.models.Picture;
import com.tutorials.camera.models.PictureDao;
import com.tutorials.camera.models.User;
import com.tutorials.camera.tools.AppTools;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.zelory.compressor.Compressor;

public class CaptureActivity extends AppCompatActivity
        implements
        View.OnClickListener,
        View.OnLongClickListener, BitmapAdapter.ImageViewClickListener, BitmapAdapter.ImageViewLongClickListener, AdapterView.OnItemSelectedListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_CAPTURE2 = 5;
    static final int EXTERNAL_IMAGE_CAPTURE = 3;
    static final int REQUEST_BARCODE_CAPTURE = 2;

    private File currentFile = null;
    private File tempFile = null;
    private File newFile = null;
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

        loadList();

        dispatchTakePictureIntent();

        TextInputEditText barCodeEdt = findViewById(R.id.barCodeEdt);
        barCodeEdt.setOnClickListener(this);
        /*barCodeEdt.setOnFocusChangeListener(new View.OnFocusChangeListener()
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
        });*/

        barCodeEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                int res = (!editable.toString().trim().isEmpty()) ? R.drawable.ic_cancel:R.drawable.ic_barcode;
                ((AppCompatImageView)findViewById(R.id.barCodeImageView)).setImageResource(res);
            }
        });

        findViewById(R.id.barCodeImageView).setOnClickListener(this);
    }


    private void loadList()
    {
        AppCompatSpinner foldersSpinner = findViewById(R.id.foldersSpinner);

        List<Folder> list = new ArrayList<>();

        FolderDao folderDao = SCamera.getInstance().getDaoSession().getFolderDao();
        List<Folder> folders = folderDao.queryBuilder()
                .where(FolderDao.Properties.ParentId.isNull())
                .orderAsc(FolderDao.Properties.FolderString).list();

        Long intFolder = new LocalData(this).getLong("intFolder");
        Long intParentFolder = new LocalData(this).getLong("intParentFolder");
        Long selected = null;
        if(folders.size()>0)
        {
            list.add(new Folder(Long.MIN_VALUE,getString(R.string.please_select_folder),null,null,null));
            //list.addAll(folders);
            for(Long i=0L;i.intValue()<folders.size();i++)
            {
                list.add(folders.get(i.intValue()));
                if(intFolder!=null && intFolder.equals(folders.get(i.intValue()).getFolderId()))
                {
                    selected = i;
                }
                else if(intParentFolder!=null && intParentFolder.equals(folders.get(i.intValue()).getFolderId()))
                {
                    selected = i;
                }
            }
        }

        MySpinnerAdapter mySpinnerAdapter = new MySpinnerAdapter(this,list);
        foldersSpinner.setAdapter(mySpinnerAdapter);
        foldersSpinner.setOnItemSelectedListener(this);
        if(selected!=null)
        {
            foldersSpinner.setSelection((selected.intValue()+1));
        }
    }

    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE2);
            File path = new File(SCamera.getInstance().getFolderName(".tmp"));
            //AppTools.clean(path);
            if(path.exists() || path.mkdirs())
            {
                try
                {
                    tempFile = new File(path,String.format("%s.jpg",AppTools.getUniqueString()));
                    Uri imageUri = FileProvider.getUriForFile(this,"com.tutorials.camera",tempFile);
                    //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
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
                    try
                    {
                        File path = new File(SCamera.getInstance().getFolderName(".tmp"),String.format("%s",AppTools.getUniqueString()));
                        currentFile = new Compressor(this)
                                .setDestinationDirectoryPath(path.getAbsolutePath())
                                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                .compressToFile(tempFile);
                        AppCompatImageView imagePreview = findViewById(R.id.imagePreview);
                        tempFile.delete();
                        tempFile = null;
                        Glide.with(this).load(currentFile.getAbsolutePath()).into(imagePreview);
                        bitmapAdapter.addPath(currentFile.getAbsolutePath());
                    }
                    catch (IOException e)
                    {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();

                        AppCompatImageView imagePreview = findViewById(R.id.imagePreview);
                        Glide.with(this).load(tempFile.getAbsolutePath()).into(imagePreview);
                        bitmapAdapter.addPath(tempFile.getAbsolutePath());
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
                    //bitmapAdapter.addPath(imageBitmap);
                }
            }
        }
        else if(requestCode==UCrop.REQUEST_CROP)
        {
            if(resultCode == RESULT_OK)
            {
                try
                {
                    File path = new File(SCamera.getInstance().getFolderName(".tmp"),String.format("%s",AppTools.getUniqueString()));
                    currentFile = new Compressor(this)
                            .setDestinationDirectoryPath(path.getAbsolutePath())
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .compressToFile(newFile);

                    AppCompatImageView imagePreview = findViewById(R.id.imagePreview);
                    Glide.with(this).load(currentFile.getAbsolutePath()).into(imagePreview);
                    bitmapAdapter.addPath(currentFile.getAbsolutePath());

                    newFile.delete();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
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
            case R.id.barCodeImageView:
                TextInputEditText barCodeEdt = findViewById(R.id.barCodeEdt);
                if(barCodeEdt.getText()!=null)
                {
                    String barCode = barCodeEdt.getText().toString();
                    if(barCode.matches(""))
                    {
                        if(!isQuery)
                        {
                            dispatchTakeBarCodeIntent();
                            isQuery = true;
                        }
                    }
                    else
                    {
                        barCodeEdt.setText("");
                    }
                }
                else
                {
                    if(!isQuery)
                    {
                        dispatchTakeBarCodeIntent();
                        isQuery = true;
                    }

                }
                break;
            case R.id.barCodeEdt:
                if(!isQuery)
                {
                    dispatchTakeBarCodeIntent();
                    isQuery = true;
                }
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

        AppCompatSpinner foldersSpinner = findViewById(R.id.foldersSpinner);
        Folder folder = (Folder)foldersSpinner.getSelectedItem();

        AppCompatSpinner subFoldersSpinner = findViewById(R.id.subFoldersSpinner);
        Folder subFolder = (Folder)subFoldersSpinner.getSelectedItem();
        Boolean subFolderChecked = false;
        if(subFoldersSpinner.getAdapter()!=null)
        {

            if(subFoldersSpinner.getAdapter().getCount()>0)
            {
                if(subFolder!=null && subFolder.getFolderId()!=Long.MIN_VALUE)
                {
                    subFolderChecked = true;
                }
            }
            else
            {
                subFolderChecked = true;
            }
        }
        else
        {
            subFolderChecked = true;
        }

        if((codeEdt.getText()!=null || barCodeEdt.getText()!=null)
                && bitmapAdapter.getItemCount()>0 && folder!=null
                && folder.getFolderId()!=Long.MIN_VALUE && subFolderChecked )
        {
            String codeText = codeEdt.getText().toString();
            String barCodeText = codeEdt.getText().toString();
            if(!codeText.trim().isEmpty() || !barCodeText.trim().isEmpty())
            {
                String folderString = (subFolder!=null) ? String.format("%s/%s",folder.getFolderString(),subFolder.getFolderString()) : folder.getFolderString();
                File path = new File(app.getFolderName(folderString));
                {

                    if(path.mkdirs() || path.exists())
                    {
                        LocalData localData = new LocalData(app.getApplicationContext());
                        Date date = localData.getDate("savingDate");
                        Integer invoiceDayCounter = 1;
                        String pictureNumberByFolder = folder.getFolderString();
                        if(date!=null)
                        {
                            if(AppTools.dateCompare(new Date(),date))
                            {
                                invoiceDayCounter = localData.getInteger(pictureNumberByFolder);
                                if(invoiceDayCounter!=null)
                                {
                                    invoiceDayCounter = invoiceDayCounter +1;
                                    localData.setInteger(pictureNumberByFolder, invoiceDayCounter);
                                }
                                else
                                {
                                    invoiceDayCounter = 1;
                                    localData.setInteger(pictureNumberByFolder, invoiceDayCounter);
                                }
                            }
                            else
                            {
                                date = new Date();
                                localData.setDate("savingDate",date);
                                localData.setInteger(pictureNumberByFolder, invoiceDayCounter);
                            }

                        }
                        else
                        {
                            date = new Date();
                            localData.setDate("savingDate",date);
                            localData.setInteger(pictureNumberByFolder, invoiceDayCounter);
                        }

                        Invoice invoice = new Invoice();
                        invoice.setInvoiceCode(codeEdt.getText().toString());
                        if(descEdt.getText()!=null)
                        invoice.setInvoiceDesc(descEdt.getText().toString());

                        invoice.setInvoiceBarCode(barCodeEdt.getText().toString());

                        invoice.setBranchId(app.getCurrentUser().getBranchId());

                        User user = app.getCurrentUser();

                        invoice.setUserId(user.getUserId());
                        Long folderId = (subFolder!=null)? subFolder.getFolderId() : folder.getFolderId();
                        invoice.setFolderId(folderId);
                        invoice.setSavingDate(AppTools.getCurrentDate());
                        invoice.setUploaded(false);

                        InvoiceDao invoiceDao = app.getDaoSession().getInvoiceDao();
                        invoiceDao.insert(invoice);

                        @SuppressLint("SimpleDateFormat")
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                        List<String> paths = bitmapAdapter.getPaths();
                        int position = 0;
                        for(int i=0;i<paths.size();i++)
                        {
                            position = position+1;
                            String reportDate = df.format(date);
                            String fileName = String.format("%s-%s-%s-%s.jpg",reportDate, invoiceDayCounter,position,user.getUserName());
                            File file = new File(path, fileName);
                            try
                            {
                                copyFile(paths.get(i),file.getAbsolutePath());

                                Picture picture = new Picture();
                                picture.setInvoiceId(invoice.getInvoiceId());
                                picture.setInvoice(invoice);
                                picture.setPictureName(fileName);
                                picture.setPicturePath(file.getAbsolutePath());
                                PictureDao pictureDao = app.getDaoSession().getPictureDao();
                                pictureDao.insert(picture);

                                File source = new File(paths.get(i));
                                source.delete();
                            }
                            catch (IOException e)
                            {
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                position = position-1;
                                Log.e(CaptureActivity.class.getCanonicalName(), e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),path.getPath(),Toast.LENGTH_LONG).show();
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

            if(folder==null || folder.getFolderId()==Long.MIN_VALUE)
            {
                ((TextView) foldersSpinner.getSelectedView()).setError(getString(R.string.please_select_folder));
            }

            if(!subFolderChecked)
            {
                ((TextView) subFoldersSpinner.getSelectedView()).setError(getString(R.string.please_select_folder));
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
    public void viewClicked(String path)
    {
        /*AppCompatImageView imagePreview = findViewById(R.id.imagePreview);
        imagePreview.setImageBitmap(bitmap);*/
        AppCompatImageView imagePreview = findViewById(R.id.imagePreview);
        Glide.with(this).load(path).into(imagePreview);
    }

    @Override
    public void viewLongClicked(final String path)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Will you delete the selected picture?");
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.setPositiveButton("Ok", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            bitmapAdapter.removeBitmap(path);
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        switch (adapterView.getId())
        {
            case R.id.foldersSpinner:
                Folder folder = (Folder)adapterView.getSelectedItem();
                if(folder!=null && folder.getFolderId()!=Long.MIN_VALUE)
                {

                    FolderDao folderDao = SCamera.getInstance().getDaoSession().getFolderDao();
                    List<Folder> folders = folderDao.queryBuilder().where(FolderDao.Properties.ParentId.eq(folder.getFolderId())).list();

                    Long intFolder = new LocalData(CaptureActivity.this).getLong("intFolder");
                    Long selected = null;

                    if(folders.size()>0)
                    {
                        List<Folder> list = new ArrayList<>();
                        list.add(new Folder(Long.MIN_VALUE,getString(R.string.please_select_sub_folder),null,null,null));
                        //list.addAll(folders);

                        for(Long j = 0L; j< folders.size(); j++)
                        {
                            list.add(folders.get(j.intValue()));
                            if(intFolder!=null && intFolder.equals(folders.get(j.intValue()).getFolderId()))
                            {
                                selected = j;
                            }
                        }

                        AppCompatSpinner subFoldersSpinner = findViewById(R.id.subFoldersSpinner);

                        MySpinnerAdapter mySpinnerAdapter = new MySpinnerAdapter(this,list);
                        subFoldersSpinner.setAdapter(mySpinnerAdapter);
                        subFoldersSpinner.setVisibility(View.VISIBLE);

                        if(selected!=null)
                        {
                            subFoldersSpinner.setSelection((selected.intValue()+1));
                        }
                    }
                    else
                    {
                        AppCompatSpinner subFoldersSpinner = findViewById(R.id.subFoldersSpinner);
                        subFoldersSpinner.setAdapter(null);
                        subFoldersSpinner.setVisibility(View.GONE);
                    }

                }
                else
                {
                    AppCompatSpinner subFoldersSpinner = findViewById(R.id.subFoldersSpinner);
                    subFoldersSpinner.setAdapter(null);
                    subFoldersSpinner.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }
}
