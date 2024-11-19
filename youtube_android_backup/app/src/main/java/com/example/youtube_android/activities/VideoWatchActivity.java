package com.example.youtube_android.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.youtube_android.CommentFragment;
import com.example.youtube_android.MainActivity;
import com.example.youtube_android.R;
import com.example.youtube_android.YouTubeApplication;
import com.example.youtube_android.adapters.RecommendedVideoAdapter;
import com.example.youtube_android.api.VideosAPI;
import com.example.youtube_android.daoes.UsersDao;
import com.example.youtube_android.daoes.VideoDao;
import com.example.youtube_android.databases.UsersDB;
import com.example.youtube_android.databases.VideoDB;
import com.example.youtube_android.entities.User;
import com.example.youtube_android.entities.VideoItem;
import com.example.youtube_android.noUseActivities.GamingActivity;
import com.example.youtube_android.noUseActivities.MoviesActivity;
import com.example.youtube_android.noUseActivities.MusicActivity;
import com.example.youtube_android.noUseActivities.NewsActivity;
import com.example.youtube_android.noUseActivities.SportsActivity;
import com.example.youtube_android.utils.ImageUtil;
import com.example.youtube_android.viewModels.UsersViewModel;
import com.example.youtube_android.viewModels.VideoItemViewModel;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class VideoWatchActivity extends AppCompatActivity {

    private boolean isLiked = false;
    private boolean isSubscribed = false;
    private boolean isDarkMode = false;
    private TextView videoTitle, publisher, views, datePublished;
    private ImageView channelImageView;
    private EditText editVideoTitle;
    private ImageView logoVideo;
    private ImageButton editButton, saveButton, deleteButton, openCommentsButton, subscribeButton, likeButton, unlikeButton, shareButton;
    private VideoItem videoItem;
    private String videoId;
    private List<VideoItem> videoItemList;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private boolean isCommentVisible = false;
    private MenuItem loginItem, signupItem, logoutItem;
    private ImageView userImage;
    private TextView userName;
    private String currentVideoId;
    private FrameLayout commentFrame;
    private User currentUser;
    private boolean isCommentFrameVisible = false;
    private VideoDao videoDao;
    private VideoDB videoDB;
    private UsersDB usersDB;
    private UsersDao usersDao;
    private ExoPlayer exoPlayer;
    private PlayerView playerView;

    private String username;
    private String token;
    private UsersViewModel usersViewModel;
    private VideoItemViewModel videoItemViewModel;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_watch);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        videoDB = Room.databaseBuilder(getApplicationContext(), VideoDB.class, "VideosDB")
                .allowMainThreadQueries()
                .build();

        videoDao = videoDB.videoDao();

        drawerLayout = findViewById(R.id.drawer_layout_video);
        navigationView = findViewById(R.id.nav_view_video);
        setUpToolbar();
        setupNavigationView();

        View headerView = navigationView.getHeaderView(0);
        userName = headerView.findViewById(R.id.user_name);
        userImage = headerView.findViewById(R.id.user_img);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        token = intent.getStringExtra("token");
        videoId = intent.getStringExtra("videoId");
        currentUser = (User) intent.getSerializableExtra("currentUser");

        Log.d(TAG, "Received videoId: " + videoId);

        videoItemViewModel = new ViewModelProvider(this).get(VideoItemViewModel.class);

        if (videoId != null) {
            MutableLiveData<VideoItem> videoItemLiveData = new MutableLiveData<>();
            videoItemViewModel.getVideoById(videoId, videoItemLiveData);
            videoItemLiveData.observe(this, videoItem -> {
                if (videoItem != null) {
                    this.videoItem = videoItem;
                    videoWatch(videoItem.get_id());
                } else {
                    Log.e(TAG, "VideoItem is null");
                }
            });
        } else {
            Log.e(TAG, "VideoId is null");
        }

        if (username != null && token != null) {
            usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
            usersViewModel.getUserFromToken(token);
            usersViewModel.getUserDetailsResult().observe(this, user -> {
                if (user != null) {
                    currentUser = user;
                    setUserDetails(currentUser);
                    updateMenuItems(true);
                } else {
                    userName.setText(getString(R.string.user_welcome));
                    userImage.setImageResource(R.drawable.user_img);
                    updateMenuItems(false);
                }
            });
        } else {
            userName.setText(getString(R.string.user_welcome));
            userImage.setImageResource(R.drawable.user_img);
            updateMenuItems(false);
        }

        playerView = findViewById(R.id.player_view);
        videoTitle = findViewById(R.id.videoTitle);
        publisher = findViewById(R.id.publisher);
        editVideoTitle = findViewById(R.id.editVideoTitle);
        editButton = findViewById(R.id.editButton);
        saveButton = findViewById(R.id.saveButton);
        likeButton = findViewById(R.id.Like);
        openCommentsButton = findViewById(R.id.openCommentsButton);
        subscribeButton = findViewById(R.id.subscribe);
        unlikeButton = findViewById(R.id.Unlike);
        shareButton = findViewById(R.id.Share);
        datePublished = findViewById(R.id.date);
        channelImageView = findViewById(R.id.logoPublisher);
        views = findViewById(R.id.views_videoWatch);
        deleteButton = findViewById(R.id.deleteButton);
        logoVideo = findViewById(R.id.logo_video);

        int dark = AppCompatDelegate.getDefaultNightMode();
        isDarkMode = dark == 2;

        exoPlayer = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(exoPlayer);

        Menu menu = navigationView.getMenu();
        loginItem = menu.findItem(R.id.log_in);
        signupItem = menu.findItem(R.id.signup);
        logoutItem = menu.findItem(R.id.logout);

        MenuItem switchItem = navigationView.getMenu().findItem(R.id.switchMode);
        SwitchCompat switchCompat = (SwitchCompat) switchItem.getActionView();
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        editButton.setOnClickListener(v -> {
            if (currentUser != null) {
                toggleEditMode(true);
            } else {
                Toast.makeText(VideoWatchActivity.this, "You are not logged in ", Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(v -> {
            if (currentUser != null && currentUser.getUsername().equals(videoItem.getPublisher())) {
                saveChanges();
                toggleEditMode(false);
            } else {
                Toast.makeText(VideoWatchActivity.this, "You can only edit your own video", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (currentUser != null && currentUser.getUsername().equals(videoItem.getPublisher())) {
                deleteVideo();
            } else {
                Toast.makeText(VideoWatchActivity.this, "You can only delete your own video", Toast.LENGTH_SHORT).show();
            }
        });

        subscribeButton.setOnClickListener(v -> {
            if (!isDarkMode) {
                if (isSubscribed) {
                    Toast.makeText(VideoWatchActivity.this, "You unsubscribed from this author now", Toast.LENGTH_SHORT).show();
                    subscribeButton.setImageResource(R.drawable.ic_subscribe);
                } else {
                    Toast.makeText(VideoWatchActivity.this, "You subscribed to this author now", Toast.LENGTH_SHORT).show();
                    subscribeButton.setImageResource(R.drawable.ic_after_subscribe);
                }
            } else {
                if (isSubscribed) {
                    Toast.makeText(VideoWatchActivity.this, "You unsubscribed from this author now", Toast.LENGTH_SHORT).show();
                    subscribeButton.setImageResource(R.drawable.ic_subscribe_white);
                } else {
                    Toast.makeText(VideoWatchActivity.this, "You subscribed to this author now", Toast.LENGTH_SHORT).show();
                    subscribeButton.setImageResource(R.drawable.ic_after_subscribe_white);
                }
                isSubscribed = !isSubscribed;
            }
        });

        logoVideo.setOnClickListener(v -> {
            Intent intent1 = new Intent(VideoWatchActivity.this, HomeActivity.class);
            intent1.putExtra("username", username);
            intent1.putExtra("token", token);
            intent1.putExtra("currentUser", currentUser);
            startActivity(intent1);
        });

        commentFrame = findViewById(R.id.comment_frame);
        openCommentsButton.setOnClickListener(v -> {
            if (!isCommentFrameVisible) {
                showCommentFrame();
            } else {
                hideCommentFrame();
            }
        });

        likeButton.setOnClickListener(v -> {
            if (!isDarkMode) {
                if (isLiked) {
                    likeButton.setImageResource(R.drawable.ic_like);
                } else {
                    Toast.makeText(VideoWatchActivity.this, "You liked this video", Toast.LENGTH_SHORT).show();
                    likeButton.setImageResource(R.drawable.ic_after_like);
                }
                isLiked = !isLiked;
            } else {
                if (isLiked) {
                    likeButton.setImageResource(R.drawable.ic_like_white);
                } else {
                    Toast.makeText(VideoWatchActivity.this, "You liked this video", Toast.LENGTH_SHORT).show();
                    likeButton.setImageResource(R.drawable.ic_after_like_white);
                }
                isLiked = !isLiked;
            }
        });

        unlikeButton.setOnClickListener(v -> {
            if (!isDarkMode) {
                if (isLiked) {
                    unlikeButton.setImageResource(R.drawable.ic_unlike);
                } else {
                    Toast.makeText(VideoWatchActivity.this, "You unliked this video", Toast.LENGTH_SHORT).show();
                    unlikeButton.setImageResource(R.drawable.ic_after_unlike);
                }
                isLiked = !isLiked;
            } else {
                if (isLiked) {
                    unlikeButton.setImageResource(R.drawable.ic_unlike_white);
                } else {
                    Toast.makeText(VideoWatchActivity.this, "You unliked this video", Toast.LENGTH_SHORT).show();
                    unlikeButton.setImageResource(R.drawable.ic_after_unlike_white);
                }
                isLiked = !isLiked;
            }
        });

        shareButton.setOnClickListener(this::showPopupMenu);

        RecyclerView recyclerView = findViewById(R.id.recommended_videos_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with an empty list and set it to RecyclerView
        RecommendedVideoAdapter adapter = new RecommendedVideoAdapter(this, new ArrayList<>(), username, token, videoId, currentUser);
        recyclerView.setAdapter(adapter);

        // Fetch recommended videos
        VideosAPI videoAPI = new VideosAPI(null, null, null, null, null);

        if (currentUser != null && currentUser.get_id() != null) {
            Log.d("CURRENT_USER_ID", "Current User ID: " + currentUser.get_id());
        } else {
            Log.d("CURRENT_USER_ID", "Current User is null or ID is null");
        }

// Proceed to call the getRecommendedVideos method
        videoAPI.getRecommendedVideos(videoId, currentUser != null ? currentUser.get_id() : null, username, new VideosAPI.VideoListCallback() {
            @Override
            public void onSuccess(List<VideoItem> recommendedVideos) {
                // Call updateData to refresh the adapter with the new recommended videos
                adapter.updateData(recommendedVideos);
            }

            @Override
            public void onError(Throwable t) {
                Log.e("VideoWatchActivity", "Error fetching recommended videos: " + t);
                Toast.makeText(VideoWatchActivity.this, "Failed to load recommended videos: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void setUserDetails(User user) {
        if (user != null) {
            userName.setText(user.getUsername());

            // Decode the Base64 to Bitmap using ImageUtil
            try {
                // First, check if the image data starts with the data:image/jpeg;base64, header
                String imageData = user.getImage();
                if (imageData.startsWith("data:image/jpeg;base64,")) {
                    imageData = imageData.substring("data:image/jpeg;base64,".length());
                }

                byte[] decodedBytes = Base64.decode(imageData, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                if (bitmap != null) {
                    Log.d("VideoWatchActivity", "Bitmap decoded successfully");

                    // Compress and resize the bitmap
                    String resizedImageBase64 = ImageUtil.resizeAndCompressAndEncodeImage(bitmap, 500, 100); // Quality set to 100
                    Bitmap resizedBitmap = ImageUtil.decodeBase64ToBitmap(resizedImageBase64, 500, 500); // Use desired width and height

                    // Use Glide to load the bitmap and display it as a circle-cropped image
                    Glide.with(this)
                            .load(resizedBitmap)
                            .apply(RequestOptions.circleCropTransform())
                            .into(userImage);
                    Log.d("VideoWatchActivity", "Image loaded with Glide");
                } else {
                    Log.e("VideoWatchActivity", "Failed to decode Base64 image");
                    userImage.setImageResource(R.drawable.user_img);
                }
            } catch (IllegalArgumentException e) {
                Log.e("VideoWatchActivity", "Invalid Base64 string", e);
                userImage.setImageResource(R.drawable.user_img);
            }
        } else {
            userName.setText(getString(R.string.user_welcome));
            userImage.setImageResource(R.drawable.user_img);
        }
    }

    private void showCommentFrame() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        CommentFragment commentFragment = new CommentFragment(videoId, currentUser, token);

        fragmentTransaction.replace(R.id.comment_frame, commentFragment);
        fragmentTransaction.commit();
        commentFrame.setVisibility(View.VISIBLE);
        isCommentFrameVisible = true;
    }

    private void hideCommentFrame() {
        commentFrame.setVisibility(View.GONE);
        isCommentFrameVisible = false;
    }

    @SuppressLint("DiscouragedApi")
    private void videoWatch(String id) {
        if (videoItem != null) {
            Log.d(TAG, "Displaying video with ID: " + id);

            // Construct the full video path if necessary
            String fullVideoPath = videoItem.getPath();
            if (!fullVideoPath.startsWith("http")) {
                fullVideoPath = YouTubeApplication.context.getString(R.string.uploadslUrl) + fullVideoPath;
            }

            // Replace backslashes with forward slashes
            fullVideoPath = fullVideoPath.replace("\\", "/");

            // Setup media
            MediaItem mediaItem = MediaItem.fromUri(fullVideoPath);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();

            // Play video
            exoPlayer.play();

            // Set other video details
            videoTitle.setText(videoItem.getTitle());
            publisher.setText(videoItem.getPublisher());
            Glide.with(this)
                    .load(videoItem.getUserImage())
                    .apply(RequestOptions.circleCropTransform())
                    .into(channelImageView);

            channelImageView.setOnClickListener(v -> {
                Intent intent = new Intent(VideoWatchActivity.this, UserVideosActivity.class);
                intent.putExtra("userId", videoItem.getUserId());
                intent.putExtra("username", username);
                intent.putExtra("token", token);
                intent.putExtra("currentUser", currentUser);
                intent.putExtra("selectedUserName", videoItem.getPublisher());
                intent.putExtra("selectedUserImage", videoItem.getUserImage());
                startActivity(intent);
            });

            datePublished.setText(videoItem.getPublishedDate());
            views.setText(String.valueOf(videoItem.getViews()));
        } else {
            Log.e(TAG, "VideoItem is null in videoWatch()");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (exoPlayer != null) {
            exoPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void deleteVideo() {
        videoItemViewModel.deleteVideo(videoItem, token, new VideoItemViewModel.VideoDeleteCallback() {
            @Override
            public void onSuccess() {
                // Navigate back to the home activity
                returnToHomeActivity();
                Toast.makeText(VideoWatchActivity.this, "Video deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(VideoWatchActivity.this, "Failed to delete video: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleEditMode(boolean isEditing) {
        if (isEditing) {
            videoTitle.setVisibility(View.GONE);
            editVideoTitle.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
            editVideoTitle.setText(videoTitle.getText());
        } else {
            videoTitle.setVisibility(View.VISIBLE);
            editVideoTitle.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.GONE);
        }
    }

    // VideoWatchActivity.java
    private void saveChanges() {
        videoItem.setTitle(editVideoTitle.getText().toString());

        videoItemViewModel.updateVideo(videoItem.get_id(), token, videoItem, new VideoItemViewModel.VideoUpdateCallback() {
            @Override
            public void onSuccess() {
                videoTitle.setText(editVideoTitle.getText().toString());
                Toast.makeText(VideoWatchActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
                toggleEditMode(false); // Exit edit mode after saving
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(VideoWatchActivity.this, "Failed to save changes: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPopupMenu(View view) {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        Context styledContext;

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            styledContext = new ContextThemeWrapper(this, R.style.PopupMenuDark);
        } else {
            styledContext = new ContextThemeWrapper(this, R.style.PopupMenuLight);
        }

        PopupMenu popupMenu = new PopupMenu(styledContext, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_share, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_share_facebook) {
                Toast.makeText(VideoWatchActivity.this, "Share on Facebook", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.menu_share_twitter) {
                Toast.makeText(VideoWatchActivity.this, "Share on Twitter", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.menu_share_instagram) {
                Toast.makeText(VideoWatchActivity.this, "Share on Instagram", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });

        popupMenu.show();
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_video);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMenuItems(currentUser != null);
    }

    private void updateMenuItems(boolean isLoggedIn) {
        if (loginItem != null && signupItem != null && logoutItem != null) {
            if (isLoggedIn) {
                loginItem.setVisible(false);
                signupItem.setVisible(false);
                logoutItem.setVisible(true);
            } else {
                loginItem.setVisible(true);
                signupItem.setVisible(true);
                logoutItem.setVisible(false);
            }
        }
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            Intent intent = null;
            int itemId = menuItem.getItemId();
            if (itemId == R.id.nav_trending) {
                intent = new Intent(VideoWatchActivity.this, MainActivity.class);
            } else if (itemId == R.id.nav_music) {
                intent = new Intent(VideoWatchActivity.this, MusicActivity.class);
            } else if (itemId == R.id.nav_gaming) {
                intent = new Intent(VideoWatchActivity.this, GamingActivity.class);
            } else if (itemId == R.id.nav_movies) {
                intent = new Intent(VideoWatchActivity.this, MoviesActivity.class);
            } else if (itemId == R.id.nav_news) {
                intent = new Intent(VideoWatchActivity.this, NewsActivity.class);
            } else if (itemId == R.id.nav_sports) {
                intent = new Intent(VideoWatchActivity.this, SportsActivity.class);
            } else if (itemId == R.id.log_in) {
                intent = new Intent(VideoWatchActivity.this, LoginActivity.class);
            } else if (itemId == R.id.signup) {
                intent = new Intent(VideoWatchActivity.this, SignupActivity.class);
            } else if (itemId == R.id.logout) {
                performLogout();
                intent = new Intent(VideoWatchActivity.this, HomeActivity.class);
            }
            if (intent != null) {
                intent.putExtra("username", username);
                intent.putExtra("token", token);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
            return true;
        });
    }

    private void returnToHomeActivity() {
        Intent intent = new Intent(VideoWatchActivity.this, HomeActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("token", token);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
        finish();
    }

    private void performLogout() {
        currentUser = null;
        username = null;
        token = null;
        userName.setText(getString(R.string.user_welcome));
        userImage.setImageResource(R.drawable.user_img);
        updateMenuItems(false);
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

}
