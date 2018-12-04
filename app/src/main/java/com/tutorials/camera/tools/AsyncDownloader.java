package com.tutorials.camera.tools;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.tutorials.camera.SCamera;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class AsyncDownloader extends AsyncTask<String, String, AsyncDownloader.FileApk>
{
    private static String TAG = AsyncDownloader.class.getCanonicalName();
    static class FileApk
    {
        private Boolean success;
        private String fileName;
        private String errorString;

        Boolean isSuccess() {
            return success;
        }

        void setSuccess(Boolean success) {
            this.success = success;
        }

        String getFileName() {
            return fileName;
        }

        void setFileName(String fileName) {
            this.fileName = fileName;
        }

        String getErrorString() {
            return errorString;
        }

        void setErrorString(String errorString) {
            this.errorString = errorString;
        }
    }

    public interface DownloadListener
    {
        void onCallback(FileApk fileApk);
    }

    private ProgressDialog progressDialog;
    private DownloadListener downloadListener;

    AsyncDownloader()
    {

    }

    void setDownloadListener(DownloadListener downloadListener)
    {
        this.downloadListener = downloadListener;
    }

    void setProgressDialog(ProgressDialog progressDialog)
    {
        this.progressDialog = progressDialog;
    }

    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected AsyncDownloader.FileApk doInBackground(String... strings)
    {
        int count;
        try {
            URL url = new URL(strings[0]);
            URLConnection connection = url.openConnection();
            connection.connect();
            // getting file length
            int lengthOfFile = connection.getContentLength();


            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            //Append timestamp to file name
            String fileName = AppTools.getUniqueString() + ".apk";

            String folder = SCamera.getInstance().getFolderName("tmp");

            File directory = new File(folder);

            AppTools.clean(directory);

            if (!directory.exists())
            {
                directory.mkdirs();
            }
            File file = new File(folder,fileName);
            // Output stream to write file
            OutputStream output = new FileOutputStream(file);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1)
            {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lengthOfFile));
                Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();
            FileApk fileApk = new FileApk();
            fileApk.setSuccess(true);
            File f = new File(folder,fileName);
            fileApk.setFileName(f.getAbsolutePath());
            return fileApk;

        }
        catch (Exception e)
        {
            Log.e("Error: ", e.getMessage());
            FileApk fileApk = new FileApk();
            fileApk.setSuccess(false);
            fileApk.setErrorString(e.getMessage());
            return fileApk;
        }

        //return "Something went wrong";
    }

    protected void onProgressUpdate(String... progress)
    {
        // setting progress percentage
        progressDialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(AsyncDownloader.FileApk fileApk)
    {
        // dismiss the dialog after the file was downloaded
        this.progressDialog.dismiss();
        if(downloadListener!=null)
            downloadListener.onCallback(fileApk);

        // Display File path after downloading
        /*Toast.makeText(activity,
                message, Toast.LENGTH_LONG).show();*/
    }
}
