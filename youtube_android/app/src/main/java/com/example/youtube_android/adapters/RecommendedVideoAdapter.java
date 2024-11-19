package com.example.youtube_android.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.youtube_android.R;
import com.example.youtube_android.activities.UserVideosActivity;
import com.example.youtube_android.activities.VideoWatchActivity;
import com.example.youtube_android.entities.User;
import com.example.youtube_android.entities.VideoItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecommendedVideoAdapter extends RecyclerView.Adapter<RecommendedVideoAdapter.RecommendedVideoViewHolder> {
    private List<VideoItem> data;
    private Context context;
    private String username;
    private String token;
    private String currentVideoId;

    private User currentUser;

    public RecommendedVideoAdapter(Context context, List<VideoItem> data, String username, String token, String currentVideoId, User currentUser) {
        this.context = context;
        this.data = filterOutCurrentVideo(data, currentVideoId);
        this.username = username;
        this.token = token;
        this.currentVideoId = currentVideoId;
        this.currentUser = currentUser;
    }


    private List<VideoItem> filterOutCurrentVideo(List<VideoItem> videos, String currentVideoId) {
        List<VideoItem> filteredVideos = new ArrayList<>();
        for (VideoItem video : videos) {
            if (!video.get_id().equals(currentVideoId)) {
                filteredVideos.add(video);
            }
        }
        return filteredVideos;

    }
    // update the data and refresh the RecyclerView
    public void updateData(List<VideoItem> newData) {
        this.data = filterOutCurrentVideo(newData, currentVideoId); // Update the data and apply the filter
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    @NonNull
    @Override
    public RecommendedVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommended_video, parent, false);
        return new RecommendedVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedVideoViewHolder holder, int position) {
        VideoItem videoItem = data.get(position);

        // Load videoItem thumbnail
        if (videoItem.getThumbnail() != null && videoItem.getThumbnail().startsWith("/data")) {
            Bitmap bitmap = BitmapFactory.decodeFile(videoItem.getThumbnail());
            if (bitmap != null) {
                holder.videoThumbnail.setImageBitmap(bitmap);
            } else {
                Glide.with(context)
                        .load(videoItem.getPath())
                        .into(holder.videoThumbnail);
            }
        } else {
            Glide.with(context)
                    .load(videoItem.getThumbnail())
                    .into(holder.videoThumbnail);
        }

        // Load video path if available
        if (videoItem.getPath() != null && videoItem.getPath().startsWith("/data")) {
            File videoFile = new File(videoItem.getPath());
            if (videoFile.exists()) {
                Log.d("VideoLoading", "Video path is valid: " + videoItem.getPath());
            } else {
                Log.e("VideoLoading", "Video file not found: " + videoItem.getPath());
            }
        }

        holder.titleRecommended.setText(videoItem.getTitle());
        holder.publisherRecommended.setText(videoItem.getPublisher());
        holder.viewsRecommended.setText(videoItem.getViews());
        holder.dateRecommended.setText(videoItem.getPublishedDate());

        // Load publisher profile image
        if (videoItem.getUserImage() != null && !videoItem.getUserImage().isEmpty()) {
            Glide.with(context)
                    .load(videoItem.getUserImage())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.logoPublisher);
        }
        holder.logoPublisher.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserVideosActivity.class);
            intent.putExtra("userId", videoItem.getUserId());
            intent.putExtra("username", username);
            intent.putExtra("token", token);
            intent.putExtra("currentUser", currentUser);
            intent.putExtra("selectedUserName", videoItem.getPublisher());
            intent.putExtra("selectedUserImage", videoItem.getUserImage());
            context.startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoWatchActivity.class);
            if (currentUser != null) {
                intent.putExtra("currentUser", currentUser);
            }
            intent.putExtra("username", username);
            intent.putExtra("token", token);
            intent.putExtra("videoId", videoItem.get_id());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class RecommendedVideoViewHolder extends RecyclerView.ViewHolder {
        public ImageView videoThumbnail;
        public TextView titleRecommended;
        public ImageView logoPublisher;
        public TextView publisherRecommended;
        public TextView viewsRecommended;
        public TextView dateRecommended;

        public RecommendedVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            titleRecommended = itemView.findViewById(R.id.titleRecommended);
            logoPublisher = itemView.findViewById(R.id.logoPublisher);
            publisherRecommended = itemView.findViewById(R.id.publisherRecommended);
            viewsRecommended = itemView.findViewById(R.id.viewsRecommended);
            dateRecommended = itemView.findViewById(R.id.dateRecommended);
        }
    }
}
