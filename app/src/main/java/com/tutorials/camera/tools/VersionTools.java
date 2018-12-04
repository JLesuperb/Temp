package com.tutorials.camera.tools;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tutorials.camera.data.LocalParams;
import com.tutorials.camera.interfaces.IVersions;
import com.tutorials.camera.models.VersionModel;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VersionTools
{
    private VersionCallBackListener versionCallBackListener;
    private Retrofit retrofit;

    @NonNull
    public static VersionTools getInstance()
    {
        return new VersionTools();
    }

    private VersionTools()
    {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://173.212.228.130:1814/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        process();
    }

    private void process()
    {
        IVersions iVersions = retrofit.create(IVersions.class);
        VersionModel model = new VersionModel();
        model.setVersion(LocalParams.CURRENT_VERSION);
        Call<VersionModel> call = iVersions.check(model);
        call.enqueue(new Callback<VersionModel>()
        {
            @Override
            public void onResponse(@NonNull Call<VersionModel> call, @NonNull Response<VersionModel> response)
            {
                if(response.isSuccessful())
                {
                    VersionModel versionModel = response.body();
                    if(versionModel!=null)
                    {
                        //No Message
                        if(versionModel.getNote()==null||versionModel.getNote().trim().isEmpty())
                        {
                            if(versionCallBackListener!=null)
                            {
                                versionCallBackListener.onSuccess();
                            }
                        }
                        //Message
                        else
                        {
                            if(versionCallBackListener!=null)
                            {
                                versionCallBackListener.onMessage(versionModel.getNote());
                            }
                        }
                    }

                }
                else if(response.code()==401)
                {
                    if(response.errorBody()!=null)
                    {
                        Gson gson = new Gson();
                        Type type = new TypeToken<VersionModel>() {}.getType();
                        VersionModel versionModel = gson.fromJson(response.errorBody().charStream(),type);
                        if(versionCallBackListener!=null)
                        {
                            versionCallBackListener.onError(versionModel.getNote());
                        }
                    }
                }
                else if(response.code()==400)
                {
                    if(versionCallBackListener!=null)
                    {
                        versionCallBackListener.onHacking();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<VersionModel> call, @NonNull Throwable t)
            {
                if(versionCallBackListener!=null)
                {
                    versionCallBackListener.onFailure(t);
                }
            }
        });
    }

    public void setVersionCallBackListener(VersionCallBackListener versionCallBackListener)
    {
        this.versionCallBackListener = versionCallBackListener;
    }

    public void setForUpdated()
    {
    }

    public interface VersionCallBackListener
    {
        void onSuccess();

        void onMessage(String message);

        void onError(String message);

        void onHacking();

        void onFailure(Throwable t);
    }
}
