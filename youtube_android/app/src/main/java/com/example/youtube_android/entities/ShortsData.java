package com.example.youtube_android.entities;
public class ShortsData {
    private String shortsPath, shortsUser, shortsTitle;
    private int shortsImage;

    public ShortsData(String shortsPath, String shortsTitle,String shortsUser, int shortsImage) {
        this.shortsPath = shortsPath;
        this.shortsTitle = shortsTitle;
        this.shortsUser = shortsUser;
        this.shortsImage = shortsImage;
    }

    public String getShortsPath() {
        return shortsPath;
    }

    public String getShortsUser() {
        return shortsUser;
    }

    public String getShortsTitle() {
        return shortsTitle;
    }

    public int getShortsImage() {
        return shortsImage;
    }
}
