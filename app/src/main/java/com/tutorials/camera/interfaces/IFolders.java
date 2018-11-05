package com.tutorials.camera.interfaces;

import com.tutorials.camera.models.Folder;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface IFolders
{
    @GET("directories")
    Call<List<Folder>> get(@Header("Authorization") String authorization);
}