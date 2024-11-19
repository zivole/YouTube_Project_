package com.example.youtube_android.api;

import com.example.youtube_android.R;
import com.example.youtube_android.YouTubeApplication;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentsAPI {

    private final WebServiceAPI webServiceAPI;

    public CommentsAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YouTubeApplication.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public Call<JsonArray> getCommentsByVideoId(String videoId) {
        return webServiceAPI.getCommentsByVideoId(videoId);
    }

    public Call<JsonObject> createComment(String token, JsonObject commentData) {
        return webServiceAPI.createComment(token, commentData);
    }

    public Call<JsonObject> updateComment(String token, String commentId, JsonObject commentData) {
        return webServiceAPI.updateComment(token, commentId, commentData);
    }

    public Call<Void> deleteComment(String token, String commentId) {
        return webServiceAPI.deleteComment(token, commentId);
    }
}
