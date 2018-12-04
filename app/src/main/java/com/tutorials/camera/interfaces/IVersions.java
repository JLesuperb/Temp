package com.tutorials.camera.interfaces;

import com.tutorials.camera.models.VersionModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IVersions
{
    @Headers({"Accept: application/json"})
    @POST("Versions/GetVersion")
    Call<VersionModel> check(@Body VersionModel versionModel);
}
