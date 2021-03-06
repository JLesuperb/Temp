package com.tutorials.camera.services;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.interfaces.IInvoices;
import com.tutorials.camera.models.Folder;
import com.tutorials.camera.models.FolderDao;
import com.tutorials.camera.models.Invoice;
import com.tutorials.camera.models.InvoiceDao;
import com.tutorials.camera.models.Picture;
import com.tutorials.camera.models.PictureDao;
import com.tutorials.camera.tools.AppTools;
import com.tutorials.camera.tools.RetrofitClient;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadService extends Service
{
    public static final String CHANNEL_1_ID = "channel1";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.tutorials.camera";
    //public static final String CHANNEL_2_ID = "channel2";

    private Long lastId = Long.MIN_VALUE;
    Integer i = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        launch();

        /*InvoiceDao invoiceDao = SCamera.getInstance().getDaoSession().getInvoiceDao();
        List<Invoice> invoices = invoiceDao.queryBuilder().where(InvoiceDao.Properties.Uploaded.eq(false)).list();

        if(invoices.size()>0)
        {
            Integer i = 0;
            for (Invoice invoice : invoices)
            {
                uploadPicture(invoice,i);
                i++;
            }

           *//* Iterator<Invoice> iterator = invoices.iterator();
            while (iterator.hasNext())
            {
                Invoice invoice = iterator.next();
                //Do stuff
                if (!iterator.hasNext())
                {
                    isLasted = true;
                }
            }*//*
        }
        else
        {
            Toast.makeText(getApplicationContext(),getText(R.string.server_updated),Toast.LENGTH_LONG).show();
        }*/

        //TODO do something useful
        return Service.START_NOT_STICKY;
    }

    private void launch()
    {
        i++;
        InvoiceDao invoiceDao = SCamera.getInstance().getDaoSession().getInvoiceDao();
        List<Invoice> invoices = SCamera.getInstance().getSyncInvoices();
        if(invoices!=null && invoices.size()>0)
        {
            Invoice invoice = invoices.get(0);
            if(invoice!=null)
            {
                if(lastId == Long.MIN_VALUE || !lastId.equals(invoice.getInvoiceId()))
                {
                    uploadPicture(invoice,i);
                    lastId = invoice.getInvoiceId();
                }
                else
                {
                    invoice = invoiceDao.queryBuilder().where(InvoiceDao.Properties.Uploaded.eq(false),InvoiceDao.Properties.InvoiceId.notEq(lastId)).limit(1).unique();
                    uploadPicture(invoice,i);
                    lastId = invoice.getInvoiceId();
                }
            }
        }
        else
        {
            publishResults(0,Activity.RESULT_OK);
            AppTools.toastError(getApplicationContext(),getString(R.string.server_updated));
            stopSelf();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    private void uploadPicture(final Invoice invoice, Integer i)
    {

        NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_1_ID);
        mBuilder.setContentTitle("Soficom Archive")
                .setContentText("Upload in progress")
                .setSmallIcon(R.drawable.round_sync_black_18);
        mBuilder.setAutoCancel(false);
        mBuilder.setProgress(0, 0, true);
        if(mNotifyManager!=null)
        {
            mNotifyManager.notify(i, mBuilder.build());
            process(invoice,mNotifyManager,i);
        }

    }

    private void process(final Invoice invoice, final NotificationManager mNotifyManager, final Integer i)
    {
        publishProgress(invoice);

        String root = Environment.getExternalStorageDirectory().toString();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        FolderDao folderDao = SCamera.getInstance().getDaoSession().getFolderDao();
        Folder folder = folderDao.queryBuilder().where(FolderDao.Properties.FolderId.eq(invoice.getFolderId())).limit(1).unique();

        String token = String.format("Bearer %s", SCamera.getInstance().getToken());

        builder.addFormDataPart("InvoiceCode",invoice.getInvoiceCode());
        builder.addFormDataPart("InvoiceDesc",invoice.getInvoiceDesc());
        builder.addFormDataPart("InvoiceBarCode",invoice.getInvoiceBarCode());
        builder.addFormDataPart("UserFId",invoice.getUserId().toString());
        builder.addFormDataPart("BranchFId",invoice.getBranchId().toString());
        builder.addFormDataPart("DirectoryFId",invoice.getFolderId().toString());
        builder.addFormDataPart("DrivePath",folder.getDrivePath());

        PictureDao pictureDao = SCamera.getInstance().getDaoSession().getPictureDao();
        List<Picture> pictures = pictureDao.queryBuilder()
            .where(PictureDao.Properties.InvoiceId.eq(invoice.getInvoiceId())).list();

        for (int j = 0; j < pictures.size(); j++)
        {
            Picture picture = pictures.get(j);
            File file = new File(picture.getPicturePath());
            builder.addFormDataPart("Pictures[]", picture.getPictureName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
            builder.addFormDataPart("PhonePaths[]", picture.getPicturePath().replace(root,""));
        }

        MultipartBody requestBody = builder.build();

        IInvoices iInvoices = RetrofitClient.getRetrofitInstance(UploadService.this).create(IInvoices.class);

        Call<ResponseBody> call = iInvoices.upload(token,requestBody);

        call.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response)
            {
                mNotifyManager.cancel(i);
                if(response.isSuccessful())
                {
                    invoice.setUploaded(true);
                    InvoiceDao invoiceDao = SCamera.getInstance().getDaoSession().getInvoiceDao();
                    invoiceDao.update(invoice);
                    List<Invoice> list = SCamera.getInstance().getSyncInvoices();
                    list.remove(invoice);
                    publishResults(1,Activity.RESULT_OK);
                    launch();
                }
                else
                {
                    AppTools.toastError(getApplicationContext(),getString(R.string.the_server_is_not_found_please_check_your_internet_connection));
                    publishResults(-1,Activity.RESULT_CANCELED);
                    stopSelf();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t)
            {
                mNotifyManager.cancel(i);
                AppTools.toastError(getApplicationContext(),t);
                publishResults(0,Activity.RESULT_CANCELED);
                stopSelf();
            }
        });
    }

    private void publishResults(int state,int result)
    {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        intent.putExtra("result","result");
        intent.putExtra("state", state);
        sendBroadcast(intent);
    }

    private void publishProgress(Invoice invoice)
    {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, Activity.RESULT_OK);
        intent.putExtra("progress","progress");
        intent.putExtra("invoice", invoice);
        sendBroadcast(intent);
    }
}
