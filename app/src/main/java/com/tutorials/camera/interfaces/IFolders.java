package com.tutorials.camera.interfaces;

import com.tutorials.camera.models.Folder;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

public interface IFolders
{
    @Headers({"Accept: application/json","Content-Type: application/json"})
    @GET("directories")
    Call<Folder[]> get(@Header("Authorization") String authorization);
}