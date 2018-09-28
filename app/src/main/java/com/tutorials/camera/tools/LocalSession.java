package com.tutorials.camera.tools;

public class LocalSession {
    private static final LocalSession ourInstance = new LocalSession();

    @org.jetbrains.annotations.Contract(pure = true)
    public static LocalSession getInstance() {
        return ourInstance;
    }

    private LocalSession()
    {
    }
}
