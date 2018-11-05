package com.tutorials.camera.interfaces;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IInvoices
{
    @Headers({"Accept: application/json"})
    @POST("invoices")
    Call<ResponseBody> upload(@Header("Authorization") String authorization,
                              @Body RequestBody file);
}
