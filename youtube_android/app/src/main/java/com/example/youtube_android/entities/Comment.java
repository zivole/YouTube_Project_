package com.example.youtube_android.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.UUID;

@Entity(tableName = "Comment")
public class Comment implements Serializable {
    @PrimaryKey
    @NonNull
    private String _id;
    private String content;
    @NonNull
    private String userName;
    @NonNull
    private String videoId;

    // Constructor with auto-generated ID
    public Comment(@NonNull String _id, String content,  @NonNull String userName, @NonNull String videoId) {
        this._id = _id;
        this.content = content;
        this.userName = userName;
        this.videoId = videoId;
    }



    public static Comment createNewComment(String content, @NonNull String userName, @NonNull String videoId) {
        String generatedId = UUID.randomUUID().toString();
        return new Comment(generatedId, content, userName, videoId);
    }

    // Getters and Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
