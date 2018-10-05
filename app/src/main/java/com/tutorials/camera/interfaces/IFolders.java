package com.tutorials.camera.interfaces;

import com.tutorials.camera.models.Folder;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface IFolders
{
    @GET("directories")
    Call<Folder[]> get(@Header("Authorization") String authorization);
}
