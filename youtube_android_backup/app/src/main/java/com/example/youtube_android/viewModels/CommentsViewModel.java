package com.example.youtube_android.viewModels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.youtube_android.entities.Comment;
import com.example.youtube_android.repositories.CommentsRepository;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

public class CommentsViewModel extends AndroidViewModel {

    private final CommentsRepository commentsRepository;
    private  MutableLiveData<List<Comment>> commentsLiveData;

    public CommentsViewModel(Application application) {
        super(application);
        commentsRepository = new CommentsRepository(application);
        commentsLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Comment>> getCommentsByVideoId(String videoId) {
        commentsRepository.getCommentsByVideoId(videoId, new CommentsRepository.RepositoryCallback<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> result) {
                commentsLiveData.postValue(result);
            }

            @Override
            public void onError(Throwable t) {
                // Handle error
            }
        });
        return commentsLiveData;
    }


    public LiveData<List<Comment>> createComment(String token, Comment comment) {
        commentsRepository.createComment(token, comment, new CommentsRepository.RepositoryCallback<Comment>() {
            @Override
            public void onSuccess(Comment result) {
                List<Comment> comments = commentsLiveData.getValue();
                if (comments != null) {
                    comments.add(result);
                    commentsLiveData.postValue(comments);
                }
            }

            @Override
            public void onError(Throwable t) {
                // Handle error
            }
        });
        return commentsLiveData;
    }

    public LiveData<Boolean> updateComment(String token, Comment comment) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        commentsRepository.updateComment(token, comment, new CommentsRepository.RepositoryCallback<Comment>() {
            @Override
            public void onSuccess(Comment result) {
                resultLiveData.postValue(true);
            }

            @Override
            public void onError(Throwable t) {
                resultLiveData.postValue(null);
            }
        });
        return resultLiveData;
    }

    public LiveData<Boolean> deleteComment(String token, String commentId) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        commentsRepository.deleteComment(token, commentId, new CommentsRepository.RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                resultLiveData.postValue(true);
            }

            @Override
            public void onError(Throwable t) {
                resultLiveData.postValue(false);
                Log.e("CommentsViewModel", "Failed to delete comment", t);
            }
        });
        return resultLiveData;
    }
}
