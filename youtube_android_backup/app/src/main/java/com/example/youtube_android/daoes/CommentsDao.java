package com.example.youtube_android.daoes;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.youtube_android.entities.Comment;

import java.util.List;

@Dao
public interface CommentsDao {
    @Query("SELECT * FROM Comment")
    List<Comment> index();

    @Query("SELECT * FROM Comment WHERE _id=:id")
    Comment getById(String id);

    @Query("SELECT * FROM Comment WHERE videoId=:videoId")
    List<Comment> getCommentsForVideo(String videoId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Comment... comments);

    @Update
    void update(Comment... comments);

    @Delete
    void delete(Comment... comments);

    @Query("DELETE FROM Comment WHERE _id=:id")
    void deleteById(String id);

    @Query("DELETE FROM Comment WHERE videoId=:videoId")
    void deleteCommentsForVideo(String videoId);
}

