package com.tutorials.camera.services;

import android.app.NotificationManager;
import android.app.ProgressDialog;
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
import java.io.IOException;
import java.util.Iterator;
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
    //public static final String CHANNEL_2_ID = "channel2";

    private NotificationManager mNotifyManager;
    private Boolean isLasted;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {


        final PictureDao pictureDao = SCamera.getInstance().getDaoSession().getPictureDao();
        final List<Picture> pictures = pictureDao.queryBuilder().where(PictureDao.Properties.Uploaded.eq(false)).list();

        if(pictures.size()>0)
        {
            Integer i = 0;
            for (Picture picture : pictures)
            {
                uploadPicture(picture,i);
                i++;
            }

            /*Iterator<Picture> iterator = pictures.iterator();
            while (iterator.hasNext())
            {
                Picture picture = iterator.next();
                //Do stuff
                if (!iterator.hasNext())
                {
                    isLasted = true;
                }
            }*/
        }
        else
        {
            Toast.makeText(getApplicationContext(),getText(R.string.server_updated),Toast.LENGTH_LONG).show();
        }

        //TODO do something useful
        return Service.START_NOT_STICKY;
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
        mBuilder.setContentTitle("SCamera")
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

    private void process(final Picture picture, final NotificationManager mNotifyManager, final Integer i) {
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

        Call<ResponseBody> call = iPictures.upload(token,body,code,description,barCode,filePath,userId,folderName,branchId,folderId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response)
            {
                mNotifyManager.cancel(i);
                //progressDialog.dismiss();
                if(response.isSuccessful())
                {

                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                    picture.setUploaded(true);
                    PictureDao pictureDao = SCamera.getInstance().getDaoSession().getPictureDao();
                    pictureDao.update(picture);
                }
                else
                {
                    ResponseBody responseBody = response.errorBody();
                    if(responseBody!=null)
                    {
                        try {
                            Toast.makeText(getApplicationContext(),responseBody.string(),Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t)
            {
                mNotifyManager.cancel(i);
                //progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Server not found",Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),new Exception(t).getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
