package com.tutorials.camera.interfaces;

import com.tutorials.camera.models.Token;
import com.tutorials.camera.models.User;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IUsers
{
    //@Headers("Content-Type: Application/json")
    @POST("login")
    @FormUrlEncoded
    Call<Token> login(@Field("UserName") String userName, @Field("UserPass") String userPass);

    //@Headers("Content-Type: Application/x-www-form-urlencoded")
    @POST("login")
    //@FormUrlEncoded
    Call<Token> login(@Body RequestBody body);

    @POST("login")
    //@FormUrlEncoded
    Call<Token> login(@Body User user);
}
