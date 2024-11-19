package com.example.youtube_android;

import android.app.Application;
import android.content.Context;

public class YouTubeApplication extends Application {
    public static Context context;

    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
    }
}
