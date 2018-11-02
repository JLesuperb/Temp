package com.tutorials.camera.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.data.LocalParams;
import com.tutorials.camera.interfaces.IUsers;
import com.tutorials.camera.models.Token;
import com.tutorials.camera.models.User;
import com.tutorials.camera.models.UserDao;
import com.tutorials.camera.tools.AppTools;
import com.tutorials.camera.tools.Permissions;
import com.tutorials.camera.tools.RetrofitClient;

import java.io.IOException;

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
        if (Permissions.checkCameraPermission(getApplicationContext()))
        {
            if (Permissions.checkReadingExternalPermission(getApplicationContext())) {
                if (Permissions.checkWritingExternalPermission(getApplicationContext())) {
                    initViews();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, LocalParams.WRITE_EXTERNAL_STORAGE);
                    } else {
                        finish();
                    }

                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, LocalParams.READ_EXTERNAL_QUERY);
                } else {
                    finish();
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, LocalParams.CAMERA);
            } else {
                finish();
            }
        }
    }

    private void initViews() {
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.cleanTxt).setOnClickListener(this);

        FloatingActionButton configBtn = findViewById(R.id.configBtn);
        configBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_login:
                if(SCamera.getInstance().getServerString()==null)
                {
                     AppTools.loadConfigDialog(this);
                    return;
                }
                final TextInputEditText userNameEdt = findViewById(R.id.userNameEdt);
                userNameEdt.setError(null);
                final TextInputEditText userPassEdt = findViewById(R.id.userPassEdt);
                userPassEdt.setError(null);
                if(userNameEdt.getText()!=null && !userNameEdt.getText().toString().trim().isEmpty()
                        && userPassEdt.getText()!=null && !userPassEdt.getText().toString().trim().isEmpty())
                {
                    String userName = userNameEdt.getText().toString();
                    String userPass = userPassEdt.getText().toString();
                    login(userName,userPass);
                }
                else
                {
                    if(userNameEdt.getText()==null && userNameEdt.getText().toString().trim().isEmpty())
                    {
                        userNameEdt.setError("Username is required");
                    }
                    if(userPassEdt.getText()==null || userPassEdt.getText().toString().trim().isEmpty())
                    {
                        userPassEdt.setError("Password is required");
                    }
                }
                break;
            case R.id.configBtn:
                //http://localhost:1900/api/links

                AppTools.loadConfigDialog(this);


                break;
            case R.id.cleanTxt:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirmation");
                builder.setMessage("Will you clear all offline login?");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        cleanLogin();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
        }
    }

    private void cleanLogin()
    {
        UserDao userDao = SCamera.getInstance().getDaoSession().getUserDao();
        userDao.deleteAll();
    }

    private void login(String userName, String userPass)
    {
        final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        AppCompatRadioButton autoRbn = findViewById(R.id.autoRbn);
        AppCompatRadioButton onlineRbn = findViewById(R.id.onlineRbn);
        AppCompatRadioButton offlineRbn = findViewById(R.id.offlineRbn);
        if(autoRbn.isChecked()||onlineRbn.isChecked()||offlineRbn.isChecked())
        {
            //region Auto
            if(autoRbn.isChecked())
            {
                UserDao userDao = SCamera.getInstance().getDaoSession().getUserDao();
                User dbUser = userDao.queryBuilder().where(UserDao.Properties.UserName.eq(userName)).unique();
                if(dbUser!=null)
                {
                    progressDialog.dismiss();
                    if(userPass.equals(dbUser.getUserPass()))
                    {
                        SCamera.getInstance().setToken(dbUser.getToken());
                        SCamera.getInstance().setCurrentUser(dbUser);
                        Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        ((EditText)findViewById(R.id.userPassEdt)).setError("Incorrect Password");
                    }
                }
                else
                {
                    IUsers iUsers = RetrofitClient.getRetrofitInstance(AuthenticationActivity.this).create(IUsers.class);
                    final User user = new User();
                    user.setUserName(userName);
                    user.setUserPass(userPass);
                    Call<Token> call = iUsers.login(user);
                    call.enqueue(new Callback<Token>()
                    {
                        @Override
                        public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response)
                        {
                            progressDialog.dismiss();
                            if (response.isSuccessful())
                            {
                                Token token = response.body();
                                if(token!=null)
                                {
                                    user.setToken(token.getTokenString());
                                    user.setUserId(token.getUserId());
                                    user.setBranchId(token.getBranchId());
                                    SCamera.getInstance().setToken(token.getTokenString());
                                    SCamera.getInstance().setCurrentUser(user);
                                    UserDao userDao = SCamera.getInstance().getDaoSession().getUserDao();
                                    userDao.insertOrReplace(user);
                                    Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            else
                            {
                                ResponseBody responseBody = response.errorBody();
                                if(responseBody!=null)
                                {
                                    try
                                    {
                                        Toast.makeText(AuthenticationActivity.this,responseBody.string(),Toast.LENGTH_LONG).show();
                                    }
                                    catch (IOException e)
                                    {
                                        Toast.makeText(AuthenticationActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Connection Not found",Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(),new Exception(t).getMessage(),Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(),new Exception(t).getMessage(),Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(),new Exception(t).getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            //endregion

            //region Online
            else if(onlineRbn.isChecked())
            {
                IUsers iUsers = RetrofitClient.getRetrofitInstance(AuthenticationActivity.this).create(IUsers.class);

                final User user = new User();
                user.setUserName(userName);
                user.setUserPass(userPass);
                Call<Token> call = iUsers.login(user);

                call.enqueue(new Callback<Token>()
                {
                    @Override
                    public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response)
                    {
                        progressDialog.dismiss();
                        if (response.isSuccessful())
                        {
                            Token token = response.body();
                            if(token!=null)
                            {
                                user.setToken(token.getTokenString());
                                user.setUserId(token.getUserId());
                                user.setBranchId(token.getBranchId());
                                SCamera.getInstance().setToken(token.getTokenString());
                                SCamera.getInstance().setCurrentUser(user);
                                Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t)
                    {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Connection Not found",Toast.LENGTH_LONG).show();
                    }
                });
            }
            //endregion

            //region Offline
            else if(offlineRbn.isChecked())
            {
                progressDialog.dismiss();
                UserDao userDao = SCamera.getInstance().getDaoSession().getUserDao();
                User dbUser = userDao.queryBuilder().where(UserDao.Properties.UserName.eq(userName)).unique();
                if(dbUser!=null)
                {
                    if(userPass.equals(dbUser.getUserPass()))
                    {
                        SCamera.getInstance().setToken(dbUser.getToken());
                        SCamera.getInstance().setCurrentUser(dbUser);
                        Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        ((EditText)findViewById(R.id.userPassEdt)).setError("Incorrect Password");
                    }
                }
                else
                {
                    ((EditText)findViewById(R.id.userNameEdt)).setError("Incorrect Username");
                }
            }
            //endregion
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
