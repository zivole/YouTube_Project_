package com.example.youtube_android.repositories;

import android.content.Context;
import android.util.Log;

import com.example.youtube_android.api.CommentsAPI;
import com.example.youtube_android.daoes.CommentsDao;
import com.example.youtube_android.databases.CommentsDB;
import com.example.youtube_android.entities.Comment;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsRepository {

    private final CommentsDao commentsDao;
    private final CommentsAPI commentsAPI;
    private final ExecutorService executorService;

    public CommentsRepository(Context context) {
        CommentsDB db = CommentsDB.getInstance(context);
        this.commentsDao = db.commentsDao();
        this.commentsAPI = new CommentsAPI();
        this.executorService = Executors.newFixedThreadPool(4);
    }

    public void getCommentsByVideoId(String videoId, RepositoryCallback<List<Comment>> callback) {
        executorService.execute(() -> {
            List<Comment> localComments = commentsDao.getCommentsForVideo(videoId);
            if (localComments != null && !localComments.isEmpty()) {
                callback.onSuccess(localComments);
            }

            commentsAPI.getCommentsByVideoId(videoId).enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Comment> serverComments = parseCommentsFromJsonArray(response.body());
                        executorService.execute(() -> {
                            commentsDao.deleteCommentsForVideo(videoId); // Clear existing comments in Room
                            commentsDao.insert(serverComments.toArray(new Comment[0])); // Insert server comments to Room
                            callback.onSuccess(serverComments);
                        });
                    } else {
                        callback.onError(new Throwable("Failed to fetch comments from server"));
                    }
                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    // If server fails, fallback to local Room data
                    if (localComments != null && !localComments.isEmpty()) {
                        callback.onSuccess(localComments);
                    } else {
                        callback.onError(t);
                    }
                }
            });
        });
    }


    public void createComment(String token, Comment comment, RepositoryCallback<Comment> callback) {
        executorService.execute(() -> {
            commentsAPI.createComment(token, convertCommentToJson(comment)).enqueue(new retrofit2.Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Comment createdComment = parseCommentFromJson(response.body());

                        // Moving this into the executor to ensure it's done on a background thread
                        executorService.execute(() -> {
                          commentsDao.insert(createdComment);
                            callback.onSuccess(createdComment);
                        });

                    } else {
                        callback.onError(new Throwable("Failed to create comment"));
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    callback.onError(t);
                }
            });
        });
    }

    public void updateComment(String token, Comment comment, RepositoryCallback<Comment> callback) {
        executorService.execute(() -> {
            commentsAPI.updateComment(token, comment.get_id(), convertContentToJson(comment)).enqueue(new retrofit2.Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Comment updatedComment = comment;

                    if (response.isSuccessful()) {
                        try {
                            if (response.body() != null) {
                                updatedComment = parseCommentFromJson(response.body());
                            }
                            commentsDao.update(updatedComment);
                            callback.onSuccess(updatedComment);

                        } catch (Exception e) {
                            Log.e("CommentsRepository", "Failed to update comment locally", e);
                            callback.onSuccess(updatedComment);
                        }
                    } else {
                        Log.e("CommentsRepository", "Failed to update comment on server.");
                        callback.onSuccess(updatedComment);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("CommentsRepository", "Network failure during comment update", t);
                    callback.onSuccess(comment);
                }
            });
        });
    }

    public void deleteComment(String token, String commentId, RepositoryCallback<Void> callback) {
        executorService.execute(() -> {
            commentsAPI.deleteComment(token, commentId).enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        try {
                            Comment comment = commentsDao.getById(commentId);
                            if (comment != null) {
                                commentsDao.deleteById(commentId);
                            } else {
                                Log.w("CommentsRepository", "Comment not found locally, skipping local delete.");
                            }
                        } catch (Exception e) {
                            Log.e("CommentsRepository", "Failed to delete comment locally", e);
                        } finally {
                            // Return success to the callback regardless of local delete result
                            callback.onSuccess(null);
                        }
                    } else {
                        callback.onError(new Throwable("Failed to delete comment from server"));
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    callback.onError(t);
                }
            });
        });
    }

    private void syncWithRoom(List<Comment> serverComments) {
        executorService.execute(() -> commentsDao.insert(serverComments.toArray(new Comment[0])));
    }

    private JsonObject convertCommentToJson(Comment comment) {
        JsonObject json = new JsonObject();
        json.addProperty("content", comment.getContent());
        json.addProperty("userId", comment.getUserName());
        json.addProperty("videoId", comment.getVideoId());
        return json;
    }
    private JsonObject convertContentToJson(Comment comment) {
        JsonObject json = new JsonObject();
        json.addProperty("content", comment.getContent());
        return json;
    }

    private Comment parseCommentFromJson(JsonObject jsonObject) {
        if (jsonObject.has("_id") && jsonObject.has("content") && jsonObject.has("userId") && jsonObject.has("videoId")) {
            String id = jsonObject.get("_id").getAsString();
            String content = jsonObject.get("content").getAsString();
            JsonObject userObject = jsonObject.getAsJsonObject("userId");
            String userId = userObject.get("_id").getAsString();
            String username = userObject.get("username").getAsString();
            String videoId = jsonObject.get("videoId").getAsString();
            return new Comment(id, content, username, videoId);
        }
        return null;
    }

    private List<Comment> parseCommentsFromJsonArray(JsonArray jsonArray) {
        List<Comment> comments = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();
            Comment comment = parseCommentFromJson(jsonObject);
            if (comment != null) {
                comments.add(comment);
            }
        }
        return comments;
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(Throwable t);
    }
}
