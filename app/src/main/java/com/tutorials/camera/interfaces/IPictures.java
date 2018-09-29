package com.tutorials.camera.interfaces;

import com.tutorials.camera.models.Picture;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IPictures
{
    @Multipart
    @POST("pictures")
    Call<ResponseBody> upload(@Header("Authorization") String authorization,
                              @Part MultipartBody.Part file,@Body Picture picture);
}
