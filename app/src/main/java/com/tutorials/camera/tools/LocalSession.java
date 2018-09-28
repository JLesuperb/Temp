package com.tutorials.camera.tools;

public class LocalSession {
    private static final LocalSession ourInstance = new LocalSession();

    public static LocalSession getInstance() {
        return ourInstance;
    }

    private LocalSession() {
    }
}
