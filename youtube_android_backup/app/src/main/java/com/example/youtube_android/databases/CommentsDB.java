package com.example.youtube_android.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.youtube_android.daoes.CommentsDao;
import com.example.youtube_android.entities.Comment;

@Database(entities = {Comment.class}, version = 4)
public abstract class CommentsDB extends RoomDatabase {

    private static CommentsDB instance;

    public abstract CommentsDao commentsDao();

    public static synchronized CommentsDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            CommentsDB.class, "CommentsDB")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
