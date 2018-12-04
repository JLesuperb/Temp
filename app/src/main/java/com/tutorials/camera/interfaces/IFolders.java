package com.tutorials.camera.interfaces;

import com.tutorials.camera.models.FolderModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

public interface IFolders
{
    @Headers({"Accept: application/json","Content-Type: application/json"})
    @GET("directories")
    Call<FolderModel[]> get(@Header("Authorization") String authorization);
}