package com.example.youtube_android.repositories;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.youtube_android.api.VideosAPI;
import com.example.youtube_android.daoes.VideoDao;
import com.example.youtube_android.databases.VideoDB;
import com.example.youtube_android.entities.VideoItem;
import com.example.youtube_android.viewModels.VideoItemViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class VideosRepository {
    private VideosAPI videosAPI;
    private VideoDao videoDao;
    private MutableLiveData<List<VideoItem>> videoListData;
    private MutableLiveData<VideoItem> videoData;
    private ExecutorService executorService;
    private Context context;

    public VideosRepository(Context context) {
        videoListData = new MutableLiveData<>();
        videoData = new MutableLiveData<>();
        videosAPI = new VideosAPI(videoListData, new MutableLiveData<>(), new MutableLiveData<>(), new MutableLiveData<>(), videoData);
        videoDao = VideoDB.getInstance(context).videoDao();
        executorService = Executors.newSingleThreadExecutor();
        this.context = context;
    }

    public void fetch20Videos() {
        executorService.execute(() -> {
            try {
                List<VideoItem> cachedVideos = videoDao.index();

                if (cachedVideos != null && !cachedVideos.isEmpty()) {
                    updateVideoList();
                }

                if (isConnectedToInternet()) {
                    videosAPI.fetch20Videos(new VideosAPI.VideoListCallback() {
                        @Override
                        public void onSuccess(List<VideoItem> serverVideos) {
                            executorService.execute(() -> {
                                videoDao.clearTable();
                                videoDao.insertVideos(serverVideos.toArray(new VideoItem[0]));
                                updateVideoList();
                            });
                        }

                        @Override
                        public void onError(Throwable t) {
                            Log.e("VideosRepository", "Error fetching videos", t);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("VideosRepository", "Error during video fetching", e);
            }
        });
    }

    private void updateVideoList() {
        executorService.execute(() -> {
            List<VideoItem> cachedVideos = videoDao.index();
            List<VideoItem> topPopularVideos = getTopPopularVideos(cachedVideos);
            List<VideoItem> randomVideos = getRandomVideos(cachedVideos, topPopularVideos);

            List<VideoItem> finalCachedVideos = new ArrayList<>();
            finalCachedVideos.addAll(topPopularVideos);
            finalCachedVideos.addAll(randomVideos);

            videoListData.postValue(finalCachedVideos);
        });
    }

    private List<VideoItem> getTopPopularVideos(List<VideoItem> videos) {
        List<VideoItem> sortedVideos = new ArrayList<>(videos);
        sortedVideos.sort((v1, v2) -> parseIntSafely(v2.getViews()) - parseIntSafely(v1.getViews()));
        return sortedVideos.subList(0, Math.min(10, sortedVideos.size()));
    }

    private List<VideoItem> getRandomVideos(List<VideoItem> videos, List<VideoItem> popularVideos) {
        Set<String> popularIds = popularVideos.stream().map(VideoItem::get_id).collect(Collectors.toSet());
        List<VideoItem> remainingVideos = videos.stream()
                .filter(video -> !popularIds.contains(video.get_id()))
                .collect(Collectors.toList());

        Collections.shuffle(remainingVideos);
        return remainingVideos.stream().limit(10).collect(Collectors.toList());
    }

    private int parseIntSafely(String viewCount) {
        try {
            return viewCount != null && !viewCount.isEmpty() ? Integer.parseInt(viewCount.replaceAll(",", "")) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public MutableLiveData<List<VideoItem>> getVideoListData() {
        return videoListData;
    }

    public void getVideoById(String videoId, VideosAPI.VideoItemCallback callback) {
        videosAPI.getVideoById(videoId, new VideosAPI.VideoItemCallback() {
            @Override
            public void onSuccess(VideoItem videoItemFromServer) {
                executorService.execute(() -> {
                    videoDao.insert(videoItemFromServer);
                    callback.onSuccess(videoItemFromServer);
                });
            }

            @Override
            public void onError(Throwable t) {
                executorService.execute(() -> {
                    VideoItem cachedVideo = videoDao.get(videoId);
                    if (cachedVideo != null) {
                        callback.onSuccess(cachedVideo);
                    } else {
                        callback.onError(t);
                    }
                });
            }
        });
    }

    public void saveVideoLocally(VideoItem videoItem) {
        executorService.execute(() -> {
            videoDao.insert(videoItem);
        });
    }

    public void uploadVideo(VideoItem videoItem, File videoFile, Uri thumbnailUri, String token, VideoItemViewModel.VideoUploadCallback callback) {
        RequestBody videoBody = RequestBody.create(MediaType.parse("video/mp4"), videoFile);
        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("file", videoFile.getName(), videoBody);

        videosAPI.addVideoController(videoItem, videoPart, token, new VideoItemViewModel.VideoUploadCallback() {
            @Override
            public void onSuccess() {
                executorService.execute(() -> {
                    videoDao.insert(videoItem);
                });
                callback.onSuccess();
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void deleteVideo(VideoItem videoItem, String token, VideoDeleteCallback callback) {
        videosAPI.deleteVideoFromServer(videoItem.get_id(), token, new VideosAPI.VideoDeleteCallback() {
            @Override
            public void onSuccess() {
                executorService.execute(() -> {
                    // Delete the video from Room after successful deletion from the server
                    videoDao.delete(videoItem);

                    // Fetch the updated list of videos to ensure the UI is in sync
                    fetch20Videos();

                    // Notify the callback that the deletion was successful
                    callback.onSuccess();
                });
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }

    public interface VideoDeleteCallback {
        void onSuccess();
        void onError(Throwable t);
    }

    public void updateVideo(String videoId, String token, VideoItem videoItem, VideoItemCallback callback) {
        videosAPI.updateVideo(videoId, token, videoItem, new VideosAPI.VideoItemCallback() {
            @Override
            public void onSuccess(VideoItem updatedVideoItem) {
                executorService.execute(() -> {
                    // Update the Room database with the updated data from the server
                    videoDao.update(updatedVideoItem);

                    // Post the updated video to LiveData so that any observer will be notified
                    videoData.postValue(updatedVideoItem);
                });
                callback.onSuccess(updatedVideoItem);
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }

    public interface VideoItemCallback {
        void onSuccess(VideoItem videoItem);
        void onError(Throwable t);
    }

    public MutableLiveData<VideoItem> getVideoData() {
        return videoData;
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void getUserVideosFromServer(String userId, FetchVideosCallback callback) {
        if (isConnectedToInternet()) {
            videosAPI.getUserVideos(userId, new VideosAPI.VideoListCallback() {
                @Override
                public void onSuccess(List<VideoItem> serverVideos) {
                    executorService.execute(() -> {
//                        videoDao.clearTable(); // נקה את הטבלה
//                        videoDao.insert(serverVideos.toArray(new VideoItem[0]));
                        callback.onFetch(serverVideos);
                    });
                }

                @Override
                public void onError(Throwable t) {
                    Log.e("VideosRepository", "Error fetching videos from server", t);
                    callback.onFetch(videoDao.getUserVideos(userId));
                }
            });
        } else {
            executorService.execute(() -> callback.onFetch(videoDao.getUserVideos(userId)));
        }
    }

    public void clearAllVideosFromRoom(Runnable onComplete) {
        executorService.execute(() -> {
            videoDao.clearTable(); // This will clear all videos from Room
            onComplete.run(); // Notify that the operation is complete
        });
    }

    public List<VideoItem> getUserVideosFromRoom(String userId) {
        return videoDao.getUserVideos(userId);
    }

    public void saveVideosLocally(List<VideoItem> videos) {
        executorService.execute(() -> videoDao.insertVideos(videos.toArray(new VideoItem[0])));
    }

    public interface FetchVideosCallback {
        void onFetch(List<VideoItem> videos);
    }
}
