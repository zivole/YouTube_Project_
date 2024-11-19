package com.example.youtube_android;

public class RecommendedVideo {
    private int thumbnailResId;
    private int logoResId;
    private String title;
    private String publisher;
    private String views;
    private String date;

    public RecommendedVideo(int thumbnailResId, int logoResId, String title, String publisher, String views, String date) {
        this.thumbnailResId = thumbnailResId;
        this.logoResId = logoResId;
        this.title = title;
        this.publisher = publisher;
        this.views = views;
        this.date = date;
    }

    public int getThumbnailResId() {
        return thumbnailResId;
    }

    public int getLogoResId() {
        return logoResId;
    }

    public String getTitle() {
        return title;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getViews() {
        return views;
    }

    public String getDate() {
        return date;
    }
}

