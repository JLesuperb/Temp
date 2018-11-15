package com.tutorials.camera.tools;

import android.content.Context;

import com.tutorials.camera.data.LocalData;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient
{
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(Context context)
    {
        LocalData localData = new LocalData(context);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(localData.getString("serverAddress"))
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit;
    }

    static Retrofit getRetrofitInstance()
    {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl("http://173.212.228.130:8081/api/v1/")
                //.baseUrl("http://173.212.228.130:8081/api/v1/")
                //.baseUrl("http://bad6ec00.ngrok.io/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit;
    }
}
