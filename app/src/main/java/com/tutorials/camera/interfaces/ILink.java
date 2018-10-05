package com.tutorials.camera.interfaces;

import com.tutorials.camera.models.Link;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface ILink
{
    @Headers({"Accept: application/json"})
    @GET("Links")
    Call<Link> get();
}
