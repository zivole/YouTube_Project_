package com.example.youtube_android.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Entity
@TypeConverters(VideoItem.CommentConverter.class)
public class VideoItem implements Serializable {

    @PrimaryKey
    @NonNull
    private String _id;
    private String title;
    private String thumbnail;
    private String publisher;
    private String views;
    private String publishedDate;
    private String path;
    private String userImage;
    private String userId;
    @TypeConverters(CommentConverter.class)
    private List<String> comments;

    // Constructor 1
    public VideoItem(String _id, String title, String thumbnail, String publisher, String views, String publishedDate, String path, String userImage, String userId, List<String> comments) {
        this._id = _id;
        this.title = title;
        this.thumbnail = thumbnail;
        this.publisher = publisher;
        this.views = views;
        this.publishedDate = publishedDate;
        this.path = path;
        this.userImage = userImage;
        this.userId = userId;
        this.comments = comments;
    }

    // Constructor 2
    public VideoItem(String _id, String thumbnail, String title, String publisher, String views, String publishedDate, String path) {
        this._id = _id;
        this.title = title;
        this.thumbnail = thumbnail;
        this.publisher = publisher;
        this.views = views;
        this.publishedDate = publishedDate;
        this.path = path;
    }

    // Constructor 3
    public VideoItem(String _id, String title, String thumbnail, String publisher, String views, String publishedDate) {
        this._id = _id;
        this.title = title;
        this.thumbnail = thumbnail;
        this.publisher = publisher;
        this.views = views;
        this.publishedDate = publishedDate;
    }

    public VideoItem(String _id, String title, String thumbnail, String publisher, String views, String publishedDate, String path, String userImage, String userId) {
        this._id = _id;
        this.title = title;
        this.thumbnail = thumbnail;
        this.publisher = publisher;
        this.views = views;
        this.publishedDate = publishedDate;
        this.path = path;
        this.userImage = userImage;
        this.userId = userId;
        this.comments = new ArrayList<>();
    }
    // Default constructor
    public VideoItem() {
    }

    // Getters and setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "VideoItem{" +
                "id=" + _id +
                ", title='" + title + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", publisher='" + publisher + '\'' +
                ", views='" + views + '\'' +
                ", publishedDate='" + publishedDate + '\'' +
                ", path='" + path + '\'' +
                ", userImage='" + userImage + '\'' +
                ", userId='" + userId + '\'' +
                ", comments='" + comments + '\'' +
                '}';
    }

    // Converter class to handle List<String> to String and vice versa
    public static class CommentConverter {
        @TypeConverter
        public static String fromComments(List<String> comments) {
            return comments == null ? null : String.join(",", comments);
        }

        @TypeConverter
        public static List<String> toComments(String data) {
            return data == null ? null : Arrays.asList(data.split(","));
        }
    }
}
