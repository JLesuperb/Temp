package com.tutorials.camera.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.tutorials.camera.R;
import com.tutorials.camera.SCamera;
import com.tutorials.camera.data.LocalData;
import com.tutorials.camera.interfaces.ILink;
import com.tutorials.camera.models.Link;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppTools
{
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

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
        LayoutInflater layoutInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.prompt_server_address, null);View view = layoutInflater.inflate(R.layout.mylayout, item );
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

            /*AppCompatEditText addressEdt = view.findViewById(R.id.addressEdt);
            if(addressEdt.getText()!=null && !addressEdt.getText().toString().trim().isEmpty())
            {
                SCamera.getInstance().setServerString(addressEdt.getText().toString().trim());
                new LocalData(activity).setString("serverAddress",addressEdt.getText().toString().trim());
                dialogInterface.dismiss();
            }*/

            builder.setNegativeButton("Sync", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.dismiss();
                    loadServerAddress(activity);
                }
            });

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else
        {
            Toast.makeText(activity,"Can't open view right now",Toast.LENGTH_LONG).show();
        }
    }

    private static void loadServerAddress(final Activity activity)
    {
        final ProgressDialog progressDialog = new ProgressDialog(activity,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Synchronize...");
        progressDialog.show();

        //AppTools.loadConfigDialog(this);
        ILink iLink = RetrofitClient.getRetrofitInstance().create(ILink.class);
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
                        Toast.makeText(activity,SCamera.getInstance().getServerString(),Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    AppTools.toastError(activity,activity.getString(R.string.the_server_is_not_found_please_check_your_internet_connection));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Link> call, @NonNull Throwable t)
            {
                progressDialog.dismiss();
                toastError(activity,t);
            }
        });
    }

    /*private static <E> E cloneObject(E item)
    {
        try
        {
            E clone = item.getClass().newInstance();
            for (Field field : item.getClass().getDeclaredFields())
            {
                field.setAccessible(true);
                field.set(clone, field.get(item));
            }
            return clone;
        }catch(Exception e){
            return null;
        }
    }*/

    /*public static class Processor
    {
        public static <T> T createInstance(){
            T t = new T();
            return t;
        }
    }*/

    public static String getDate(Long milliSeconds)
    {
        // Create a DateFormatter object for displaying date in specified format.
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd" /*"yyyy/MM/dd HH:mm:ss"*/);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @NonNull
    public static Boolean dateCompare(Date date1, Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public static Long getTimeMillis(Date date)
    {
        return date.getTime();
    }

    public static void copyFile(String from, String to) throws IOException
    {
        File fromFile =new File(from);
        File toFile =new File(to);
        InputStream inStream = new FileInputStream(fromFile);
        OutputStream outStream = new FileOutputStream(toFile);
        byte[] buffer = new byte[1024];

        int length;
        //copy the file content in bytes
        while ((length = inStream.read(buffer)) > 0)
        {
            outStream.write(buffer, 0, length);
        }

        inStream.close();
        outStream.close();
        fromFile.delete();
    }

    public static Date toDate(String date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try
        {
            return sdf.parse(date);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static String fromDate(Date date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(date);
    }


    public static String getCurrentDate()
    {
        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date today = Calendar.getInstance().getTime();
        return df.format(today);
    }

    /** Create a File for saving an image or video */
    public static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static void toastError(Context context, String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    public static void toastError(Context context, Throwable t)
    {

        if(t instanceof TimeoutException)
        {
            Toast.makeText(context,context.getString(R.string.your_internet_connection_is_too_slow_please_try_again),Toast.LENGTH_LONG).show();
        }
        else if(t instanceof SocketTimeoutException)
        {
            Toast.makeText(context,context.getString(R.string.your_internet_connection_is_too_slow_please_try_again),Toast.LENGTH_LONG).show();
        }
        else if(t instanceof SocketException)
        {
            Toast.makeText(context,context.getString(R.string.no_internet_connection_check_your_internet_connection_and_try_again),Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(context,context.getString(R.string.network_failed_please_check_your_internet_connection),Toast.LENGTH_LONG).show();
        }
    }

    public static void sync(final Activity activity)
    {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.show();

        AsyncDownloader downloader = new AsyncDownloader();

        /*Intent updateIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://173.212.228.130:1814/api/Versions/GetApp"));
        activity.startActivity(updateIntent);*/

        downloader.execute("http://173.212.228.130:1814/api/Versions/GetApp");
        downloader.setProgressDialog(progressDialog);

        downloader.setDownloadListener(new AsyncDownloader.DownloadListener() {
            @Override
            public void onCallback(AsyncDownloader.FileApk fileApk)
            {
                if(fileApk.isSuccess())
                {
                    File apkFile = new File(fileApk.getFileName());
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri imageUri = FileProvider.getUriForFile(activity,"com.tutorials.camera",apkFile);
                    intent.setDataAndType(imageUri, "application/vnd.android.package-archive");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    activity.startActivity(intent);

                    /*Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                            .setDataAndType(Uri.parse(fileApk.getFileName()),
                                    "application/vnd.android.package-archive");
                    promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(promptInstall);*/

                    /*Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri imageUri = FileProvider.getUriForFile(activity,"com.tutorials.camera",apkFile);
                    //intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                    intent.setDataAndType(imageUri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);*/

                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri apkUri = FileProvider.getUriForFile(activity,"com.tutorials.camera",apkFile);
                        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                        intent.setData(apkUri);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        activity.startActivity(intent);
                    }
                    else
                        {
                        Uri apkUri = Uri.fromFile(apkFile);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }*/


                }
                else
                {
                    Toast.makeText(activity,fileApk.getErrorString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static boolean hasStorage(boolean requireWriteAccess) {
        //TODO: After fix the bug,  add "if (VERBOSE)" before logging errors.
        String state = Environment.getExternalStorageState();
        Log.v("hasStorage", "storage state is " + state);

        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            if (requireWriteAccess) {
                boolean writable = checkFsWritable();
                Log.v("hasStorage", "storage writable is " + writable);
                return writable;
            } else {
                return true;
            }
        }
        else return !requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private static boolean checkFsWritable() {
        // Create a temporary file to see whether a volume is really writeable.
        // It's important not to put it in the root directory which may have a
        // limit on the number of files.
        String directoryName =
                Environment.getExternalStorageDirectory().toString() + "/DCIM";
        File directory = new File(directoryName);
        if (!directory.isDirectory()) {
            if (!directory.mkdirs()) {
                return false;
            }
        }
        File f = new File(directoryName, ".probe");
        try {
            // Remove stale file if any
            if (f.exists()) {
                f.delete();
            }
            if (!f.createNewFile()) {
                return false;
            }
            f.delete();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public static HashSet<String> getExternalMounts() {
        final HashSet<String> out = new HashSet<>();
        String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
        String s = "";
        try {
            final Process process = new ProcessBuilder().command("mount")
                    .redirectErrorStream(true).start();
            process.waitFor();
            final InputStream is = process.getInputStream();
            final byte[] buffer = new byte[1024];
            while (is.read(buffer) != -1) {
                s = s + new String(buffer);
            }
            is.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        // parse output
        final String[] lines = s.split("\n");
        for (String line : lines) {
            if (!line.toLowerCase(Locale.US).contains("asec")) {
                if (line.matches(reg)) {
                    String[] parts = line.split(" ");
                    for (String part : parts) {
                        if (part.startsWith("/"))
                            if (!part.toLowerCase(Locale.US).contains("vold"))
                                out.add(part);
                    }
                }
            }
        }
        return out;
    }

    @NonNull
    public static List<File> getStorages(Context context)
    {
        File[] externalStorageFiles=ContextCompat.getExternalFilesDirs(context,null);
        return Arrays.asList(externalStorageFiles);
    }

    public static final String SD_CARD = "sdCard";
    public static final String EXTERNAL_SD_CARD = "externalSdCard";
    private static final String ENV_SECONDARY_STORAGE = "SECONDARY_STORAGE";

    public static Map<String, File> getAllStorageLocations()
    {
        Map<String, File> storageLocations = new HashMap<>(10);
        File sdCard = Environment.getExternalStorageDirectory();
        storageLocations.put(SD_CARD, sdCard);
        final String rawSecondaryStorage = System.getenv(ENV_SECONDARY_STORAGE);
        if (!TextUtils.isEmpty(rawSecondaryStorage))
        {
            String[] externalCards = rawSecondaryStorage.split(":");
            for (int i = 0; i < externalCards.length; i++)
            {
                String path = externalCards[i];
                storageLocations.put(EXTERNAL_SD_CARD + String.format(i == 0 ? "" : "_%d", i), new File(path));
            }
        }
        return storageLocations;
    }

    public static Set<File> getStorageDirectories(Context context) {
        HashSet<File> storageDirectories = null;
        try
        {
            // Use reflection to retrieve storage volumes because required classes and methods are hidden in AOSP.
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            if(storageManager!=null)
            {
                Method method = storageManager.getClass().getMethod("getVolumeList");
                StorageVolume[] storageVolumes = (StorageVolume[]) method.invoke(storageManager);
                if (storageVolumes != null && storageVolumes.length > 0)
                {
                    storageDirectories = new HashSet<>();
                    for (StorageVolume volume : storageVolumes) {
                        //storageDirectories.add(new File(volume.));
                    }
                }
            }


        }
        catch (Exception e)
        {
            Log.e("", e.getMessage());
        }
        return storageDirectories;
    }

    @NonNull
    public static List<File> getRootFolders()
    {
        //String removableStoragePath;
        File fileList[] = new File("/storage/").listFiles();
        List<File> files = new ArrayList<>();
        for (File file : fileList)
        {
            if(!file.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath()) && file.isDirectory() && file.canRead())
            {
                files.add(file);
            }
            //removableStoragePath = file.getAbsolutePath();
        }
        return files;
    }

    @NonNull
    public static List<File> getExternalSDCards()
    {
        //String removableStoragePath;
        File fileList[] = new File("/storage/").listFiles();
        List<File> files = new ArrayList<>();
        for (File file : fileList)
        {
            if(!file.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath()) && file.isDirectory() && file.canRead())
            {
                files.add(file);
            }
            //removableStoragePath = file.getAbsolutePath();
        }
        return files;
    }
}
