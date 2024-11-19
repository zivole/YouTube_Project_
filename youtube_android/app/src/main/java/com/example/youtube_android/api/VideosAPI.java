package com.example.youtube_android.api;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.example.youtube_android.R;
import com.example.youtube_android.YouTubeApplication;
import com.example.youtube_android.entities.VideoItem;
import com.example.youtube_android.utils.ImageUtil;
import com.example.youtube_android.viewModels.VideoItemViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideosAPI {
    private WebServiceAPI webServiceAPI;
    private MutableLiveData<List<VideoItem>> videoListData;
    private MutableLiveData<Boolean> addVideoResult;
    private MutableLiveData<Boolean> deleteVideoResult;
    private MutableLiveData<Boolean> updateVideoResult;
    private MutableLiveData<VideoItem> videoData;

    private static final String TAG = "VideoWatchActivity"; // Or any meaningful tag name

    private static Retrofit retrofit = null;

    public VideosAPI(MutableLiveData<List<VideoItem>> videoListData, MutableLiveData<Boolean> addVideoResult, MutableLiveData<Boolean> deleteVideoResult, MutableLiveData<Boolean> updateVideoResult, MutableLiveData<VideoItem> videoData) {
        this.videoListData = videoListData;
        this.addVideoResult = addVideoResult;
        this.deleteVideoResult = deleteVideoResult;
        this.updateVideoResult = updateVideoResult;
        this.videoData = videoData;

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Configure OkHttpClient with longer timeouts
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .readTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    okhttp3.Response response = null;
                    int tryCount = 0;
                    while (response == null && tryCount < 3) {
                        try {
                            response = chain.proceed(request);
                        } catch (Exception e) {
                            tryCount++;
                            if (tryCount >= 3) throw e;  // Fail after 3 tries
                        }
                    }
                    return response;
                })
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(YouTubeApplication.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }
    public void fetch20Videos(final VideoListCallback callback) {
        Call<JsonObject> call = webServiceAPI.get20Videos();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.d("VideosAPI", "Videos fetched successfully: " + response.toString());
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        try {
                            List<VideoItem> videoItems = new ArrayList<>();
                            Gson gson = new Gson();

                            // Stream and process popular videos
                            JsonArray popularVideosArray = jsonObject.getAsJsonArray("popularVideos");
                            if (popularVideosArray != null) {
                                processVideoArray(popularVideosArray, videoItems, gson);
                            }

                            // Stream and process random videos
                            JsonArray randomVideosArray = jsonObject.getAsJsonArray("randomVideos");
                            if (randomVideosArray != null) {
                                processVideoArray(randomVideosArray, videoItems, gson);
                            }

                            Log.d("VideosAPI", "Total number of videos parsed: " + videoItems.size());
                            callback.onSuccess(videoItems);
                        } catch (Exception e) {
                            Log.e("VideosAPI", "Error parsing video items: " + e.getMessage(), e);
                            callback.onError(e);
                        }
                    } else {
                        Log.e("VideosAPI", "Response body is null");
                        callback.onError(new NullPointerException("Response body is null"));
                    }
                } else {
                    Log.e("VideosAPI", "Failed to fetch videos: " + response.message());
                    callback.onError(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("VideosAPI", "onFailure called. URL: " + call.request().url() + " Error: " + t.getMessage(), t);
                callback.onError(t);
            }
        });
    }

    private void processVideoArray(JsonArray videoArray, List<VideoItem> videoItems, Gson gson) {
        for (JsonElement element : videoArray) {
            VideoItem videoItem = gson.fromJson(element, VideoItem.class);

            // Save thumbnail image to file
            String thumbnailBase64 = videoItem.getThumbnail();
            if (thumbnailBase64 != null && !thumbnailBase64.isEmpty()) {
                String thumbnailFilePath = ImageUtil.saveBase64AsFile(YouTubeApplication.context, thumbnailBase64, "thumbnail_" + videoItem.get_id() + ".jpg");
                if (thumbnailFilePath != null) {
                    videoItem.setThumbnail(thumbnailFilePath);  // Store file path instead of Base64 string
                } else {
                    Log.e("ImageProcessing", "Failed to save thumbnail as file for video: " + videoItem.get_id());
                }
            }

            // Save user image to file
            String userImageBase64 = videoItem.getUserImage();
            if (userImageBase64 != null && !userImageBase64.isEmpty()) {
                String userImageFilePath = ImageUtil.saveBase64AsFile(YouTubeApplication.context, userImageBase64, "userImage_" + videoItem.get_id() + ".jpg");
                if (userImageFilePath != null) {
                    videoItem.setUserImage(userImageFilePath);  // Store file path instead of Base64 string
                } else {
                    Log.e("ImageProcessing", "Failed to save user image as file for video: " + videoItem.get_id());
                }
            }

            videoItems.add(videoItem);
        }
    }

    public void getVideoById(String videoId, VideoItemCallback callback) {
        Call<JsonObject> call = webServiceAPI.getVideo(videoId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        Gson gson = new Gson();
                        VideoItem videoItem = gson.fromJson(jsonObject, VideoItem.class);
                        callback.onSuccess(videoItem);
                    } else {
                        callback.onError(new Throwable("Empty response from server"));
                    }
                } else {
                    callback.onError(new Throwable("Failed to fetch video"));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onError(t);
            }
        });
    }


    public void getRecommendedVideos(String videoId, String userId, String username, VideoListCallback callback) {
        Log.d(TAG, "Sending userId: " + userId + ", username: " + username);  // Log the values being sent

        Call<JsonArray> call = webServiceAPI.getRecommendedVideos(videoId, userId, username);

        Log.d(TAG, "Request URL: " + call.request().url());  // Log the full request URL

        try {
            call.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    Log.d(TAG, "onResponse called");  // Log when we get a response

                    if (response.isSuccessful()) {
                        JsonArray jsonArray = response.body();
                        if (jsonArray != null) {
                            Gson gson = new Gson();
                            List<VideoItem> recommendedVideos = new ArrayList<>();
                            for (JsonElement element : jsonArray) {
                                VideoItem videoItem = gson.fromJson(element, VideoItem.class);
                                recommendedVideos.add(videoItem);
                            }
                            callback.onSuccess(recommendedVideos);
                        } else {
                            callback.onError(new Throwable("Empty response from server"));
                        }
                    } else {
                        callback.onError(new Throwable("Failed to fetch recommended videos"));
                    }
                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    Log.e(TAG, "onFailure called: " + t.getMessage(), t);  // Log the error with stack trace
                    callback.onError(t);
                }

            });
        } catch (Exception e) {
            Log.e(TAG, "Exception occurred during enqueue: " + e.getMessage(), e);
        }
    }




public void addVideoController(VideoItem videoItem, MultipartBody.Part file, String token, VideoItemViewModel.VideoUploadCallback callback) {
        String compressedThumbnail = ImageUtil.resizeAndCompressAndEncodeImage(
                ImageUtil.loadBitmapFromFile(videoItem.getThumbnail()), 500, 80);
        RequestBody titlePart = RequestBody.create(MediaType.parse("text/plain"), videoItem.getTitle());
        RequestBody publishedDatePart = RequestBody.create(MediaType.parse("text/plain"), videoItem.getPublishedDate());
        RequestBody thumbnailUrlPart = RequestBody.create(MediaType.parse("text/plain"), compressedThumbnail);

        Call<JsonObject> call = webServiceAPI.addVideoController(titlePart, publishedDatePart, thumbnailUrlPart, file, "Bearer " + token);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("VideoUpload", "Response code: " + response.code());
                Log.d("VideoUpload", "Response message: " + response.message());
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    try {
                        Log.e("VideoUpload", "Upload failed: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("VideoUpload", "Failed to read error body", e);
                    }
                    callback.onError(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("VideoUpload", "Network failure: " + t.getMessage(), t);
                callback.onError(t);
            }
        });
    }
    public void updateVideo(String videoId, String token, VideoItem videoItem, VideoItemCallback callback) {
        Call<VideoItem> call = webServiceAPI.updateVideo(videoId, "Bearer " + token, videoItem);
        call.enqueue(new Callback<VideoItem>() {
            @Override
            public void onResponse(Call<VideoItem> call, Response<VideoItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("Failed to update video"));
                }
            }

            @Override
            public void onFailure(Call<VideoItem> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public interface VideoItemCallback {
        void onSuccess(VideoItem videoItem);
        void onError(Throwable t);
    }

    public void deleteVideoFromServer(String videoId, String token, VideoDeleteCallback callback) {
        Call<JsonObject> call = webServiceAPI.deleteVideo(videoId, "Bearer " + token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    Log.e("VideosAPI", "Failed to delete video: " + response.message());
                    callback.onError(new Throwable("Failed to delete video: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("VideosAPI", "Error during video deletion", t);
                callback.onError(t);
            }
        });
    }
    public void getUserVideos(String userId, VideoListCallback callback) {
        Call<JsonArray> call = webServiceAPI.getUserVideos(userId);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<VideoItem> videoItems = new ArrayList<>();
                    JsonArray jsonArray = response.body();
                    Gson gson = new Gson();

                    for (JsonElement element : jsonArray) {
                        VideoItem videoItem = gson.fromJson(element, VideoItem.class);
                        videoItems.add(videoItem);
                    }
                    callback.onSuccess(videoItems);
                } else {
                    callback.onError(new Exception("Failed to fetch videos"));
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public interface VideoListCallback {
        void onSuccess(List<VideoItem> videoItems);
        void onError(Throwable t);
    }


    public interface VideoDeleteCallback {
        void onSuccess();
        void onError(Throwable t);
    }

}
