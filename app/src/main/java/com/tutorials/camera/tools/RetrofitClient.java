package com.tutorials.camera.tools;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient
{
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://94a0feeb.ngrok.io/api/v1/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {

            Gson gson = new GsonBuilder()
                    //.registerTypeAdapter(Id.class, new IdTypeAdapter())
                    .enableComplexMapKeySerialization()
                    .serializeNulls()
                    //.setDateFormat(DateFormat.LONG)
                    .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                    .setPrettyPrinting()
                    .setVersion(1.0)
                    .create();

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    //.addConverterFactory(GsonConverterFactory.create(gson))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
