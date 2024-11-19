package com.example.youtube_android.databases;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.youtube_android.daoes.VideoDao;
import com.example.youtube_android.entities.VideoItem;

@Database(entities = {VideoItem.class}, version = 5, exportSchema = false)
public abstract class VideoDB extends RoomDatabase {

    private static VideoDB instance;

    public abstract VideoDao videoDao();

    @SuppressWarnings("deprecation")
    public static synchronized VideoDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            VideoDB.class, "VideosDB")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
