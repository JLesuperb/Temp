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
import android.widget.Toast;

import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.interfaces.IPictures;
import com.tutorials.camera.models.Picture;
import com.tutorials.camera.models.PictureDao;
import com.tutorials.camera.models.User;
import com.tutorials.camera.tools.RetrofitClient;

import java.io.File;

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

        /*PictureDao pictureDao = SCamera.getInstance().getDaoSession().getPictureDao();
        List<Picture> pictures = pictureDao.queryBuilder().where(PictureDao.Properties.Uploaded.eq(false)).list();

        if(pictures.size()>0)
        {
            Integer i = 0;
            for (Picture picture : pictures)
            {
                uploadPicture(picture,i);
                i++;
            }

            *//*Iterator<Picture> iterator = pictures.iterator();
            while (iterator.hasNext())
            {
                Picture picture = iterator.next();
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
        PictureDao pictureDao = SCamera.getInstance().getDaoSession().getPictureDao();
        Picture picture = pictureDao.queryBuilder().where(PictureDao.Properties.Uploaded.eq(false)).limit(1).unique();
        if(picture!=null)
        {
            if(lastId == Long.MIN_VALUE || !lastId.equals(picture.getId()))
            {
                uploadPicture(picture,i);
                lastId = picture.getId();
            }
            else
            {
                picture = pictureDao.queryBuilder().where(PictureDao.Properties.Uploaded.eq(false),PictureDao.Properties.Id.notEq(lastId)).limit(1).unique();
                uploadPicture(picture,i);
                lastId = picture.getId();
            }
        }
        else
        {
            publishResults(0,Activity.RESULT_OK);
            Toast.makeText(getApplicationContext(),getText(R.string.server_updated),Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    private void uploadPicture(final Picture picture, Integer i)
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
            process(picture,mNotifyManager,i);
        }

    }

    private void process(final Picture picture, final NotificationManager mNotifyManager, final Integer i)
    {
        String root = Environment.getExternalStorageDirectory().toString();
        IPictures iPictures = RetrofitClient.getRetrofitInstance(UploadService.this).create(IPictures.class);
        String token = String.format("Bearer %s", SCamera.getInstance().getToken());

        File file = new File(picture.getFilePath());

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("Picture", file.getName(), reqFile);

        RequestBody code = RequestBody.create(okhttp3.MultipartBody.FORM, picture.getCode());
        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, picture.getDescription());
        RequestBody barCode = RequestBody.create(okhttp3.MultipartBody.FORM, picture.getBarCode());
        String phoneFolder = picture.getFilePath().replace(root,"");
        RequestBody filePath = RequestBody.create(okhttp3.MultipartBody.FORM, phoneFolder);
        RequestBody userId = RequestBody.create(okhttp3.MultipartBody.FORM, picture.getUserId().toString());
        RequestBody folderName = RequestBody.create(okhttp3.MultipartBody.FORM, file.getParentFile().getName());
        User user = SCamera.getInstance().getCurrentUser();
        RequestBody branchId = RequestBody.create(okhttp3.MultipartBody.FORM, user.getBranchId().toString());
        RequestBody folderId = RequestBody.create(okhttp3.MultipartBody.FORM, picture.getFolderId().toString());

        RequestBody savingTime = RequestBody.create(okhttp3.MultipartBody.FORM, picture.getSavingTime());

        RequestBody pictureNumber = RequestBody.create(okhttp3.MultipartBody.FORM, picture.getPictureNumber().toString());

        Call<ResponseBody> call = iPictures.upload(token,body,code,description,barCode,filePath,userId,folderName,branchId,folderId,savingTime, pictureNumber);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response)
            {
                mNotifyManager.cancel(i);
                //progressDialog.dismiss();
                if(response.isSuccessful())
                {

                    ///Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                    picture.setUploaded(true);
                    PictureDao pictureDao = SCamera.getInstance().getDaoSession().getPictureDao();
                    pictureDao.update(picture);
                    publishResults(1,Activity.RESULT_OK);
                    launch();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                    publishResults(0,Activity.RESULT_CANCELED);
                    stopSelf();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t)
            {
                mNotifyManager.cancel(i);
                //progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),new Exception(t).getMessage(),Toast.LENGTH_LONG).show();
                //launch();
                publishResults(0,Activity.RESULT_CANCELED);
                stopSelf();
            }
        });
    }

    private void publishResults(int state,int result)
    {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        intent.putExtra("state", state);
        sendBroadcast(intent);
    }
}
