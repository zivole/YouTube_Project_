package com.example.youtube_android.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.youtube_android.daoes.UsersDao;
import com.example.youtube_android.entities.User;

@Database(entities = {User.class}, version = 3)
public abstract class UsersDB extends RoomDatabase {
    private static UsersDB INSTANCE;

    public abstract UsersDao usersDao();

    public static synchronized UsersDB getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UsersDB.class, "UsersDB")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
