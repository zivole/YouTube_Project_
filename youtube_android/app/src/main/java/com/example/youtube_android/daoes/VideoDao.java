package com.example.youtube_android.daoes;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.youtube_android.entities.VideoItem;

import java.util.List;

@Dao
public interface VideoDao {
    @Query("SELECT * FROM VideoItem")
    List<VideoItem> index();

    @Query("SELECT * FROM VideoItem WHERE _id = :id")
    VideoItem get(String id);
    @Query("SELECT * FROM VideoItem WHERE userId = :userId")
    List<VideoItem> getUserVideos(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(VideoItem... videoItems);

    @Update
    void update(VideoItem... videos);

    @Delete
    void delete(VideoItem... videos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVideos(VideoItem... videoItems);
    @Query("DELETE FROM VideoItem")
    void clearTable();

}