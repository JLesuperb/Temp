package com.tutorials.camera.tools;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient
{
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://387cfd27.ngrok.io/api/v1/";

    /*public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }*/

    public static Retrofit getRetrofitInstance(String serverAddress)
    {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        if (retrofit == null)
        {

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(serverAddress)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    //.setClient(new Ok3Client(new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS)))
                    .build();
        }
        return retrofit;
    }
}
