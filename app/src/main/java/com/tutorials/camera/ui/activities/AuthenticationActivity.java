package com.tutorials.camera.ui.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.tutorials.camera.tools.ExternalStorage;
import com.tutorials.camera.tools.RetrofitClient;
import com.tutorials.camera.tools.VersionTools;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationActivity extends AppCompatActivity implements View.OnClickListener
{
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        initViews();
    }

    private void initViews()
    {
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.cleanTxt).setOnClickListener(this);

        FloatingActionButton configBtn = findViewById(R.id.configBtn);
        configBtn.setOnClickListener(this);
        FloatingActionButton storeBtn = findViewById(R.id.storeBtn);
        storeBtn.setOnClickListener(this);

        TextInputEditText userPassEdt = findViewById(R.id.userPassEdt);
        userPassEdt.setOnEditorActionListener((v, actionId, event) ->
        {
            if (actionId == EditorInfo.IME_ACTION_DONE)
            {
                findViewById(R.id.btn_login).performClick();
            }
            return false;
        });

        UserDao userDao = SCamera.getInstance().getDaoSession().getUserDao();
        List<User> list = userDao.loadAll();
        if(list.size()>0)
            findViewById(R.id.cleanTxt).setVisibility(View.VISIBLE);

        AppCompatTextView titleTextView = findViewById(R.id.titleTextView);
        String title = titleTextView.getText().toString();
        titleTextView.setText(String.format("%s  %s",title, LocalParams.CURRENT_VERSION));
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
                builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
                builder.setPositiveButton("Ok", (dialogInterface, i) ->
                {
                    dialogInterface.dismiss();
                    cleanLogin();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            case R.id.storeBtn:
                List<File> sd = AppTools.getStorages(this);
                StringBuilder b = new StringBuilder();
                for(File s:sd) b.append(s).append("\n");

                /*String message;
                if(AppTools.hasStorage(true))
                {
                    message = "hasStorage";
                }
                else
                {
                    message = "notStorage";
                }
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();*/
                //Toast.makeText(getApplicationContext(),b.toString(),Toast.LENGTH_SHORT).show();

                Map<String, File> map = ExternalStorage.getAllStorageLocations();

                for (Map.Entry<String,File> entry:map.entrySet())
                {
                    String build = entry.getKey() + " :: " + entry.getValue();
                    Toast.makeText(getApplicationContext(), build,Toast.LENGTH_SHORT).show();
                }

//                StorageManager storageManager = (StorageManager) this.getSystemService(Context.STORAGE_SERVICE);
//
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N && storageManager!=null)
//                {
//                    List<StorageVolume> storageVolumeList = storageManager.getStorageVolumes();
//                }

                File f = Environment.getExternalStorageDirectory();
                Toast.makeText(getApplicationContext(), f.toString(),Toast.LENGTH_SHORT).show();

                List<File> files = AppTools.getRootFolders();

                for(File file:files)
                {
                    Toast.makeText(getApplicationContext(), file.toString(),Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void cleanLogin()
    {
        UserDao userDao = SCamera.getInstance().getDaoSession().getUserDao();
        userDao.deleteAll();
        List<User> list = userDao.loadAll();
        if(list.size()>0)
            findViewById(R.id.cleanTxt).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.cleanTxt).setVisibility(View.GONE);
    }

    private void launchProgress()
    {
        progressDialog = new ProgressDialog(AuthenticationActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
    }

    private void cancelProgress()
    {
        if(progressDialog!=null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
    }

    private void login(String userName, String userPass)
    {
        launchProgress();

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
                    cancelProgress();

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
                    final User user = new User();
                    user.setUserName(userName);
                    user.setUserPass(userPass);

                    VersionTools versionTools = VersionTools.getInstance();
                    versionTools.setVersionCallBackListener(new VersionTools.VersionCallBackListener()
                    {
                        @Override
                        public void onSuccess()
                        {
                            IUsers iUsers = RetrofitClient.getRetrofitInstance(AuthenticationActivity.this).create(IUsers.class);
                            Call<Token> call = iUsers.login(user);
                            call.enqueue(new Callback<Token>()
                            {
                                @Override
                                public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response)
                                {
                                    cancelProgress();

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
                                }
                            });
                        }

                        @Override
                        public void onMessage(String message) {

                        }


                        @Override
                        public void onError(String message) {

                        }

                        @Override
                        public void onHacking() {

                        }

                        @Override
                        public void onFailure(Throwable t) {

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
                        cancelProgress();
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
                cancelProgress();
                UserDao userDao = SCamera.getInstance().getDaoSession().getUserDao();
                User dbUser = userDao.queryBuilder().where(UserDao.Properties.UserName.eq(userName)).unique();
                if(dbUser!=null)
                {
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
                    ((EditText)findViewById(R.id.userNameEdt)).setError("Incorrect Username");
                }
            }
            //endregion
        }
    }

}
