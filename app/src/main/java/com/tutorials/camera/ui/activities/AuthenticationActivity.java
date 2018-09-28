package com.tutorials.camera.ui.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tutorials.camera.R;
import com.tutorials.camera.data.LocalData;
import com.tutorials.camera.data.LocalParams;
import com.tutorials.camera.interfaces.IUsers;
import com.tutorials.camera.models.Token;
import com.tutorials.camera.models.User;
import com.tutorials.camera.tools.Permissions;
import com.tutorials.camera.tools.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationActivity extends AppCompatActivity implements View.OnClickListener
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        initViews();
        //launch();

    }

    private void launch()
    {
        if(Permissions.checkCameraPermission(getApplicationContext()))
        {
            if(Permissions.checkReadingExternalPermission(getApplicationContext()))
            {
                if(Permissions.checkWritingExternalPermission(getApplicationContext()))
                {
                    initViews();
                }
                else
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, LocalParams.WRITE_EXTERNAL_STORAGE);
                    }
                    else
                    {
                        finish();
                    }

                }
            }
            else
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, LocalParams.READ_EXTERNAL_QUERY);
                }
                else
                {
                    finish();
                }
            }
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, LocalParams.CAMERA);
            }
            else
            {
                finish();
            }
        }
    }

    private void initViews()
    {
        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_login:
                final EditText userNameEdt = findViewById(R.id.userNameEdt);
                userNameEdt.setError(null);
                final EditText userPassEdt = findViewById(R.id.userPassEdt);
                userPassEdt.setError(null);
                final String userName = userNameEdt.getText().toString();
                final String userPass = userPassEdt.getText().toString();
                if(!userName.trim().isEmpty() && !userPass.trim().isEmpty())
                {
                    final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Authenticating...");
                    progressDialog.show();

                    final LocalData localData = new LocalData(getApplicationContext());
                    if(localData.getString("tokenKey")!=null)
                    {
                        progressDialog.dismiss();

                        if(userName.equals(localData.getString("userName")))
                        {
                            if(userPass.equals(localData.getString("userPass")))
                            {
                                Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                userPassEdt.setError("Incorrect Password");
                            }
                        }
                        else
                        {
                            userNameEdt.setError("Incorrect Username");
                        }
                    }
                    else
                    {
                        IUsers iUsers = RetrofitClient.getRetrofitInstance().create(IUsers.class);
                        Call<Token> call = iUsers.login(new User(userName,userPass));
                        call.enqueue(new Callback<Token>() {
                            @Override
                            public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response)
                            {
                                progressDialog.dismiss();
                                if (response.isSuccessful())
                                {
                                    Token token = response.body();
                                    if(token!=null)
                                    {
                                        String message = token.getTokenString();
                                        localData.setString("tokenKey",message);
                                        localData.setString("userName",userName);
                                        localData.setString("userPass",userPass);
                                        Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                                else
                                {
                                    ResponseBody responseBody = response.errorBody();
                                    if(responseBody!=null)
                                    {
                                        Gson gson = new Gson();
                                        Token token = gson.fromJson(responseBody.charStream(),Token.class);
                                        if(token!=null)
                                        {
                                            String message = token.getMessage();
                                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                                            if(message.equals("User not found"))
                                            {
                                                userNameEdt.setError("User not found");
                                                return;
                                            }
                                            else if(message.equals("Invalid Password"))
                                            {
                                                userPassEdt.setError("Invalid Password");
                                                return;
                                            }
                                        }
                                    }
                                    Toast.makeText(getApplicationContext(),"Error parsing",Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t)
                            {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Connection Error",Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }
                else
                {
                    if(userName.trim().isEmpty())
                    {
                        userNameEdt.setError("Username is required");
                    }
                    if(userPass.trim().isEmpty())
                    {
                        userPassEdt.setError("Password is required");
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case LocalParams.CAMERA:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
                    launch();
                }
                else
                {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            case LocalParams.READ_EXTERNAL_QUERY:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Reading storage permission granted", Toast.LENGTH_SHORT).show();
                    launch();
                }
                else
                {
                    Toast.makeText(this, "Reading storage permission denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            case LocalParams.WRITE_EXTERNAL_STORAGE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Writing storage permission granted", Toast.LENGTH_SHORT).show();
                    launch();
                }
                else
                {
                    Toast.makeText(this, "Writing storage permission denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

}
