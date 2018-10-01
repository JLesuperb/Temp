package com.tutorials.camera.tools;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

public class AppTools
{
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
}
