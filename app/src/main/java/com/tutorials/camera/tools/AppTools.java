package com.tutorials.camera.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.data.LocalData;
import com.tutorials.camera.interfaces.ILink;
import com.tutorials.camera.models.Link;
import com.tutorials.camera.ui.activities.AuthenticationActivity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppTools
{
    public static boolean checkPermission(Context context)
    {
        String permission1 = "android.permission.READ_EXTERNAL_STORAGE";
        String permission2 = "android.permission.WRITE_EXTERNAL_STORAGE";
        String permission3 = "android.permission.CAMERA";
        int res1 = context.checkCallingOrSelfPermission(permission1);
        int res2 = context.checkCallingOrSelfPermission(permission2);
        int res3 = context.checkCallingOrSelfPermission(permission3);
        return (res1 == PackageManager.PERMISSION_GRANTED && res2 == PackageManager.PERMISSION_GRANTED&& res3 == PackageManager.PERMISSION_GRANTED);
    }

    @NonNull
    public static String getUniqueString()
    {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 12;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++)
        {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    public static void clean(File path)
    {
        if (path.isDirectory())
        {
            File[] entries = path.listFiles();
            if (entries != null)
            {
                for (File entry : entries)
                {
                    clean(entry);
                }
            }
        }
        if(!path.delete())
        {
            try
            {
                throw new IOException("Failed to delete " + path);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadConfigDialog(final Activity activity)
    {
        final ProgressDialog progressDialog = new ProgressDialog(activity,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Synchronize...");
        progressDialog.show();

        //AppTools.loadConfigDialog(this);
        ILink iLink = RetrofitClient.getRetrofitInstance("http://192.168.8.101:1900/api/").create(ILink.class);
        Call<Link> call = iLink.get();
        call.enqueue(new Callback<Link>()
        {
            @Override
            public void onResponse(@NonNull Call<Link> call, @NonNull Response<Link> response)
            {
                progressDialog.dismiss();
                if(response.isSuccessful())
                {
                    Link link = response.body();
                    if(link!=null)
                    {
                        SCamera.getInstance().setServerString(link.getLinkText());
                        new LocalData(activity).setString("serverAddress",link.getLinkText());
                        Toast.makeText(activity,link.getLinkText(),Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Link> call, @NonNull Throwable t)
            {
                progressDialog.dismiss();
                Toast.makeText(activity,new Exception(t).getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        /*LayoutInflater layoutInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        *//* final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.prompt_server_address, null);View view = layoutInflater.inflate(R.layout.mylayout, item ); *//*
        if(layoutInflater!=null)
        {
            @SuppressLint("InflateParams")
            final View view = layoutInflater.inflate(R.layout.prompt_server_address, null );
            AppCompatEditText addressEdt = view.findViewById(R.id.addressEdt);
            addressEdt.setText(new LocalData(activity).getString("serverAddress"));
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Server Config");
            builder.setCancelable(false);
            builder.setView(view);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    AppCompatEditText addressEdt = view.findViewById(R.id.addressEdt);
                    if(addressEdt.getText()!=null && !addressEdt.getText().toString().trim().isEmpty())
                    {
                        SCamera.getInstance().setServerString(addressEdt.getText().toString().trim());
                        new LocalData(activity).setString("serverAddress",addressEdt.getText().toString().trim());
                        dialogInterface.dismiss();
                    }
                    else
                    {
                        dialogInterface.cancel();
                    }

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else
        {
            Toast.makeText(activity,"Can't open view right now",Toast.LENGTH_LONG).show();
        }*/

    }
}
