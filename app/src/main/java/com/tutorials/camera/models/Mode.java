package com.tutorials.camera.models;

public class Mode
{
    public enum ModeType{Auto,Online,Offline}

    private ModeType modeType;
    private String modeText;

    public Mode(String modeText, ModeType modeType)
    {
        this.modeText = modeText;
        this.modeType = modeType;
    }

    public String getModeText() {
        return modeText;
    }

    public ModeType getModeType() {
        return modeType;
    }

    @Override
    public String toString() {
        return modeText;
    }
}
