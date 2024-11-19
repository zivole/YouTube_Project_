package com.example.youtube_android.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.youtube_android.utils.ImageUtil;
import com.example.youtube_android.viewModels.VideoItemViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private static final String TAG = "VideoAdapter";
    private List<VideoItem> videoItemList;
    private final Context context;
    private final String username;
    private final String token;
    private VideoItemViewModel videoItemViewModel;
    private User currentUser;


    public VideoAdapter(Context context, List<VideoItem> videoItemList, String username, String token, VideoItemViewModel viewModel, User currentUser) {
        this.context = context;
        this.videoItemList = videoItemList;
        this.username = username;
        this.token = token;
        this.videoItemViewModel = viewModel;
        this.currentUser = currentUser;
    }
    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoItem videoItem = videoItemList.get(position);
        Log.d(TAG, "Binding video item at position: " + position);

        // Handle video thumbnail
        if (videoItem.getThumbnail() != null) {
            Log.d(TAG, "Loading thumbnail from file path...");
            Glide.with(context)
                    .load(videoItem.getThumbnail()) // Load from file path
                    .into(holder.thumbnailImageView);
        } else {
            Log.d(TAG, "Thumbnail path is null, setting default image...");
        }

        holder.titleTextView.setText(videoItem.getTitle());
        holder.channelName.setText(videoItem.getPublisher());
        holder.viewsTextView.setText(videoItem.getViews());

        // Format and set the published date
        String formattedDate = formatDateString(videoItem.getPublishedDate());
        holder.timeTextView.setText(formattedDate);

        // Handle video thumbnail
        if (videoItem.getThumbnail() != null) {
            Log.d(TAG, "Loading thumbnail from file path...");
            Glide.with(context)
                    .load(videoItem.getThumbnail()) // Load from file path
                    .into(holder.thumbnailImageView);
        } else {
            Log.d(TAG, "Thumbnail path is null, setting default image...");
        }

        // Handle publisher profile image
        if (videoItem.getUserImage() != null) {
            Log.d(TAG, "Loading user image from file path...");
            Glide.with(context)
                    .load(videoItem.getUserImage()) // Load from file path
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.channelImageView);
        } else {
            Log.d(TAG, "User image path is null, setting default image...");
            holder.channelImageView.setImageResource(R.drawable.user_img); // Set a default image if no user image is available
        }

        holder.channelImageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserVideosActivity.class);
            intent.putExtra("userId", videoItem.getUserId());
            intent.putExtra("username", username);
            intent.putExtra("token", token);
            if (currentUser != null) {
                intent.putExtra("currentUser", currentUser);
            }
            intent.putExtra("selectedUserName", videoItem.getPublisher());
            intent.putExtra("selectedUserImage", videoItem.getUserImage());
            context.startActivity(intent);
        });

        holder.videoCard.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoWatchActivity.class);
            // Pass the currentUser object (since it's Serializable)
            if (currentUser != null) {
                intent.putExtra("currentUser", currentUser);
            }
            intent.putExtra("username", username);
            intent.putExtra("token", token);
            intent.putExtra("videoId", videoItem.get_id());
            Log.d(TAG, "Passing videoId: " + videoItem.get_id());
            context.startActivity(intent);
        });
    }

    private String formatDateString(String dateString) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = originalFormat.parse(dateString);
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }


    @Override
    public int getItemCount() {
        return videoItemList != null ? videoItemList.size() : 0;
    }

    public void updateVideoList(List<VideoItem> newVideoList) {
        videoItemList = newVideoList;
        Log.d("VideoAdapter", "Video list updated with " + newVideoList.size() + " items");
        notifyDataSetChanged();
    }

    public List<VideoItem> getVideos() {
        return videoItemList;
    }

    public void addVideoItem(VideoItem videoItem) {
        videoItemList.add(videoItem);
        notifyItemInserted(videoItemList.size());
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        LinearLayout videoCard;
        ImageView thumbnailImageView;
        TextView titleTextView;
        ImageView channelImageView;
        TextView viewsTextView;
        TextView timeTextView;
        TextView channelName;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoCard = itemView.findViewById(R.id.videoCard);
            thumbnailImageView = itemView.findViewById(R.id.videoThumbnail);
            titleTextView = itemView.findViewById(R.id.videoTitle);
            channelImageView = itemView.findViewById(R.id.channelImage);
            viewsTextView = itemView.findViewById(R.id.views);
            timeTextView = itemView.findViewById(R.id.publishDate);
            channelName = itemView.findViewById(R.id.channelName);
        }
    }

    private static class DecodeImageTask extends AsyncTask<Void, Void, Bitmap> {
        private ImageView imageView;
        private String base64Image;

        public DecodeImageTask(ImageView imageView, String base64Image) {
            this.imageView = imageView;
            this.base64Image = base64Image;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                String base64ImageWithoutPrefix = base64Image.split(",")[1];
                byte[] decodedString = Base64.decode(base64ImageWithoutPrefix, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                if (decodedBitmap != null) {
                    // Resize and compress the image
                    String resizedImageBase64 = ImageUtil.resizeAndCompressAndEncodeImage(decodedBitmap, 500, 100);
                    return ImageUtil.decodeBase64ToBitmap(resizedImageBase64, 500, 500);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error decoding Base64 image: ", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                Log.d(TAG, "Bitmap set to ImageView successfully.");
            } else {
                Log.e(TAG, "Failed to decode Base64 image.");
            }
        }
    }
}
