package com.example.youtube_android.viewModels;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.youtube_android.api.VideosAPI;
import com.example.youtube_android.entities.VideoItem;
import com.example.youtube_android.repositories.VideosRepository;

import java.io.File;
import java.util.List;

public class VideoItemViewModel extends AndroidViewModel {
    private VideosRepository videosRepository;
    private MutableLiveData<List<VideoItem>> videoListData;
    private MutableLiveData<VideoItem> videoData;
    private MutableLiveData<List<VideoItem>> userVideosLiveData = new MutableLiveData<>();

    public VideoItemViewModel(Application application) {
        super(application);
        videosRepository = new VideosRepository(application);
        videoListData = videosRepository.getVideoListData();
        videoData = videosRepository.getVideoData();
    }

    public void fetch20Videos() {
        videosRepository.fetch20Videos();
    }

    public MutableLiveData<List<VideoItem>> getVideoListData() {
        return videoListData;
    }

    public void getVideoById(String videoId, MutableLiveData<VideoItem> videoItemLiveData) {
        videosRepository.getVideoById(videoId, new VideosAPI.VideoItemCallback() {
            @Override
            public void onSuccess(VideoItem videoItem) {
                videoItemLiveData.postValue(videoItem);
            }

            @Override
            public void onError(Throwable t) {
                videoItemLiveData.postValue(null);
            }
        });
    }

    public void addVideoLocally(VideoItem videoItem) {
        videosRepository.saveVideoLocally(videoItem);
    }

    public void uploadVideo(VideoItem videoItem, File videoFile, Uri thumbnailUri, String token, VideoUploadCallback callback) {
        videosRepository.uploadVideo(videoItem, videoFile, thumbnailUri, token, new VideoUploadCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
                fetch20Videos();
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }

    public interface VideoUploadCallback {
        void onSuccess();
        void onError(Throwable t);
    }

    public MutableLiveData<VideoItem> getVideoData() {
        return videoData;
    }

    public void deleteVideo(VideoItem videoItem, String token, VideoDeleteCallback callback) {
        videosRepository.deleteVideo(videoItem, token, new VideosRepository.VideoDeleteCallback() {
            @Override
            public void onSuccess() {
                // Fetch the updated list of videos to ensure the UI is in sync
                fetch20Videos();
                callback.onSuccess();
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

    public void updateVideo(String videoId, String token, VideoItem videoItem, VideoUpdateCallback callback) {
        videosRepository.updateVideo(videoId, token, videoItem, new VideosRepository.VideoItemCallback() {
            @Override
            public void onSuccess(VideoItem updatedVideoItem) {
                // Update the LiveData with the updated video item
                videoData.postValue(updatedVideoItem);

                // Fetch the latest list of videos to ensure the entire list is up to date
                fetch20Videos();

                // Notify the callback that the update was successful
                callback.onSuccess();
            }

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        });
    }

    public interface VideoUpdateCallback {
        void onSuccess();
        void onError(Throwable t);
    }

    public MutableLiveData<List<VideoItem>> getUserVideos(String userId) {
        videosRepository.getUserVideosFromServer(userId, new VideosRepository.FetchVideosCallback() {
            @Override
            public void onFetch(List<VideoItem> newVideos) {
                if (newVideos != null && !newVideos.isEmpty()) {
//                    videosRepository.saveVideosLocally(newVideos);
                    userVideosLiveData.postValue(newVideos);
                } else {
                    List<VideoItem> videosFromDb = videosRepository.getUserVideosFromRoom(userId);
                    userVideosLiveData.postValue(videosFromDb);
                }
            }
        });
        return userVideosLiveData;
    }

    public void clearAllVideosFromRoom(Runnable onComplete) {
        videosRepository.clearAllVideosFromRoom(onComplete);
    }

    public MutableLiveData<List<VideoItem>> getUserVideosLiveData() {
        return userVideosLiveData;
    }
}
