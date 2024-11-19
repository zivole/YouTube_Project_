package com.example.youtube_android.daoes;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.youtube_android.entities.User;

import java.util.List;

@Dao
public interface UsersDao {
    @Query("SELECT * FROM User")
    List<User> index();

    @Query("SELECT * FROM User WHERE username = :userName")
    User getUserByUserName(String userName);

    @Insert
    void insert(User... users);

    @Update
    void update(User... users);

    @Delete
    void delete(User... users);

    @Query("DELETE FROM User")
    void deleteAll();

}