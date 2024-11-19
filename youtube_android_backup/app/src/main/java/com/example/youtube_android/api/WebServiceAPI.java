package com.example.youtube_android.api;

import com.example.youtube_android.entities.User;
import com.example.youtube_android.entities.VideoItem;
import com.example.youtube_android.utils.SignInRequest;
import com.example.youtube_android.utils.SignUpRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebServiceAPI {
    @GET("users")
    Call<List<User>> getUsers();

    @POST("users/signin")
    Call<JsonObject> signIn(@Body SignInRequest signInRequest);

    @POST("tokens")
    Call<JsonObject> createToken(@Body JsonObject userName);

    @GET("/api/users/user_by_token")
    Call<JsonObject> getUserFromToken(@Header("Authorization") String authHeader);

    @POST("users/signup")
    Call<JsonObject> signUp(@Body SignUpRequest signUpRequest);
    @POST("users")
    Call<Void> createUser(@Body User user);


    @PATCH("users/{id}")
    Call<JsonObject> updateUser(@Path("id") String username, @Body User user);

    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") String username);

//    @POST("users/validatePassword")
//    Call<Boolean> validatePassword(@Body String password);
//
//    @POST("users/checkUsernameExists")
//    Call<Boolean> checkUsernameExists(@Body String userName);
//


    @GET("users/username")
    Call<User> getUserByUsername(@Query("userName") String userName);
    @GET("videos")
    Call<JsonObject> get20Videos();
    @GET("videos/{id}")
    Call<JsonObject> getVideo(@Path("id") String id);

    @GET("videos/{id}/recommended")
    Call<JsonArray> getRecommendedVideos(
            @Path("id") String id,
            @Header("x-user-id") String userId,
            @Header("x-username") String username
    );
    @Multipart

    @POST("/api/videos/add")
    Call<JsonObject> addVideoController(
            @Part("title") RequestBody title,
            @Part("publishedDate") RequestBody publishedDate,
            @Part("thumbnail") RequestBody thumbnailUrl,
            @Part MultipartBody.Part file,
            @Header("Authorization") String authHeader

        );
    @PATCH("/api/videos/{pid}")
    Call<VideoItem> updateVideo(
            @Path("pid") String videoId,
            @Header("Authorization") String token,
            @Body VideoItem videoItem
    );
    @DELETE("/api/videos/{pid}")
    Call<JsonObject> deleteVideo(@Path("pid") String videoId, @Header("Authorization") String token);
    @GET("comments")
    Call<JsonArray> getCommentsByVideoId(@Query("videoId") String videoId);

    @POST("comments")
    Call<JsonObject> createComment(@Header("Authorization") String token, @Body JsonObject commentData);

    @PUT("comments/{id}")
    Call<JsonObject> updateComment(@Header("Authorization") String token, @Path("id") String commentId, @Body JsonObject commentData);

    @DELETE("comments/{id}")
    Call<Void> deleteComment(@Header("Authorization") String token, @Path("id") String commentId);
    @GET("/api/videos/users/{userId}/videos")
    Call<JsonArray> getUserVideos(@Path("userId") String userId);
}
