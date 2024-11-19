//package com.example.ytb.adapters;
//
//import android.media.MediaPlayer;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.VideoView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.ytb.R;
//import com.example.ytb.entities.ShortsData;
//
//import java.util.List;
//
//public class ShortsAdapter extends RecyclerView.Adapter<ShortsAdapter.ShortsViewHolder> {
//    List<ShortsData> shortsDataList;
//
//    public ShortsAdapter(List<ShortsData> shortsDataList) {
//        this.shortsDataList = shortsDataList;
//    }
//
//    @NonNull
//    @Override
//    public ShortsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shorts, parent, false);
//        return new ShortsViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ShortsViewHolder holder, int position) {
//        holder.setShortsData(shortsDataList.get(position));
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return shortsDataList.size();
//    }
//
//
//    public class ShortsViewHolder extends RecyclerView.ViewHolder{
//        VideoView videoView;
//        TextView shortsUser, shortsTitle;
//        ImageView shortsImage;
//
//        public ShortsViewHolder(@NonNull View itemView) {
//            super(itemView);
//            videoView = itemView.findViewById(R.id.videoView);
//            shortsUser = itemView.findViewById(R.id.shortsUser);
//            shortsTitle = itemView.findViewById(R.id.shortsTitle);
//            shortsImage = itemView.findViewById(R.id.shortsImage);
//        }
//
//        public void setShortsData(ShortsData shortsData){
//            shortsUser.setText(shortsData.getShortsUser());
//            shortsTitle.setText(shortsData.getShortsTitle());
//            videoView.setVideoPath(shortsData.getShortsPath());
//            shortsImage.setImageResource(shortsData.getShortsImage());
//
//            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    mediaPlayer.start();
//                    float videoRatio = mediaPlayer.getVideoWidth() / (float) mediaPlayer.getVideoHeight();
//                    float screenRatio = videoView.getWidth() / (float) videoView.getHeight();
//
//                    float scale = videoRatio / screenRatio;
//                    if (scale >= 1f){
//                        videoView.setScaleX(scale);
//                    } else {
//                        videoView.setScaleY(1f/scale);
//                    }
//                }
//            });
//
//
//            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//                    mediaPlayer.start();
//                }
//            });
//        }
//    }
//}

package com.example.youtube_android.adapters;
//
//import android.media.MediaPlayer;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.VideoView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.youtube_android.R;
//import com.example.youtube_android.entities.*;
//
//import java.util.List;
//
//public class ShortsAdapter extends RecyclerView.Adapter<ShortsAdapter.ShortsViewHolder> {
//    List<ShortsData> shortsDataList;
//
//    public ShortsAdapter(List<ShortsData> shortsDataList) {
//        this.shortsDataList = shortsDataList;
//    }
//
//    @NonNull
//    @Override
//    public ShortsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shorts, parent, false);
//        return new ShortsViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ShortsViewHolder holder, int position) {
//        holder.setShortsData(shortsDataList.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return shortsDataList.size();
//    }
//
//    public class ShortsViewHolder extends RecyclerView.ViewHolder {
//        VideoView videoView;
//        TextView shortsUser, shortsTitle;
//        ImageView shortsImage;
//
//        public ShortsViewHolder(@NonNull View itemView) {
//            super(itemView);
//            videoView = itemView.findViewById(R.id.videoView);
//            shortsUser = itemView.findViewById(R.id.shortsUser);
//            shortsTitle = itemView.findViewById(R.id.shortsTitle);
//            shortsImage = itemView.findViewById(R.id.shortsImage);
//        }
//
//        public void setShortsData(ShortsData shortsData) {
//            shortsUser.setText(shortsData.getShortsUser());
//            shortsTitle.setText(shortsData.getShortsTitle());
//            videoView.setVideoPath(shortsData.getShortsPath());
//            shortsImage.setImageResource(shortsData.getShortsImage());
//
//            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    mediaPlayer.start();
//                    float videoRatio = mediaPlayer.getVideoWidth() / (float) mediaPlayer.getVideoHeight();
//                    float screenRatio = videoView.getWidth() / (float) videoView.getHeight();
//
//                    float scale = videoRatio / screenRatio;
//                    if (scale >= 1f) {
//                        videoView.setScaleX(scale);
//                    } else {
//                        videoView.setScaleY(1f / scale);
//                    }
//                }
//            });
//
//            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//                    mediaPlayer.start();
//                }
//            });
//        }
//    }
//}
// ShortsAdapter.java


//package com.example.youtube_android.adapters;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.youtube_android.R;
import com.example.youtube_android.entities.ShortsData;
import java.util.List;

public class ShortsAdapter extends RecyclerView.Adapter<ShortsAdapter.ShortsViewHolder> {

    private List<ShortsData> shortsDataList;

    public ShortsAdapter(List<ShortsData> shortsDataList) {
        this.shortsDataList = shortsDataList;
    }

    @NonNull
    @Override
    public ShortsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shorts, parent, false);
        return new ShortsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShortsViewHolder holder, int position) {
        ShortsData shortsData = shortsDataList.get(position);
        holder.videoView.setVideoPath(shortsData.getShortsPath());
        holder.videoView.start();
        holder.username.setText(shortsData.getShortsUser());
        holder.title.setText(shortsData.getShortsTitle());
        holder.channelImage.setImageResource(shortsData.getShortsImage());
    }

    @Override
    public int getItemCount() {
        return shortsDataList.size();
    }

    static class ShortsViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        TextView username;
        TextView title;
        ImageView channelImage;

        public ShortsViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            username = itemView.findViewById(R.id.shortsUser);
            title = itemView.findViewById(R.id.shortsTitle);
            channelImage = itemView.findViewById(R.id.shortsImage);
        }
    }
}
