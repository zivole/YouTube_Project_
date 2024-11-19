package com.example.youtube_android.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.youtube_android.MainActivity;
import com.example.youtube_android.R;
import com.example.youtube_android.adapters.VideoAdapter;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private ImageButton searchBtn;
    private RecyclerView lvRecyclerView;
    private List<VideoItem> videoItemList;
    private VideoAdapter videoAdapter;
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fab;
    private static final int UPLOAD_VIDEO_REQUEST_CODE = 1;
    private VideoItem newVideo;

    private MenuItem loginItem, signupItem, logoutItem;
    private ImageView userImage;
    private TextView userName;
    private User currentUser;

    private VideoItemViewModel videoItemViewModel;
    private UsersViewModel usersViewModel;

    private String username;
    private String token;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_menu);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchBtn = findViewById(R.id.search_btn);
        lvRecyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        userImage = findViewById(R.id.user_img);
        userName = findViewById(R.id.user_name);

        // Set user details in UI
        View headerView = navigationView.getHeaderView(0);
        userName = headerView.findViewById(R.id.user_name);
        userImage = headerView.findViewById(R.id.user_img);

        // Add click listener to the user image
        userImage.setOnClickListener(v -> {
            if (currentUser != null) {
                Intent userDetailsIntent = new Intent(HomeActivity.this, UserDetailsActivity.class);
                userDetailsIntent.putExtra("username", currentUser.getUsername());
                userDetailsIntent.putExtra("token", token);
                userDetailsIntent.putExtra("_id", currentUser.get_id());
                userDetailsIntent.putExtra("currentUser", currentUser);

                if (currentUser.getImage() != null) {
                    // Compress the image before passing
                    String compressedImage = ImageUtil.compressAndEncodeBase64Image(
                            ImageUtil.decodeBase64ToBitmap(currentUser.getImage(), 100, 100),75  // Compress to 50% quality
                    );
                    userDetailsIntent.putExtra("profileImage", compressedImage);
                }
                startActivity(userDetailsIntent);
            } else {
                Toast.makeText(HomeActivity.this, "Please log in to edit your details", Toast.LENGTH_SHORT).show();
            }
        });

        Menu menu = navigationView.getMenu();
        loginItem = menu.findItem(R.id.log_in);
        signupItem = menu.findItem(R.id.signup);
        logoutItem = menu.findItem(R.id.logout);

        // Retrieve user details from Intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        token = intent.getStringExtra("token");
        currentUser = (User) intent.getSerializableExtra("currentUser");


        // Initialize ViewModels
        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        videoItemViewModel = new ViewModelProvider(this).get(VideoItemViewModel.class);

        if (username != null && token != null) {
            usersViewModel.getUserFromToken(token);
            usersViewModel.getUserDetailsResult().observe(this, user -> {
                if (user != null) {
                    currentUser = user;
                    setUserDetails(currentUser);
                    updateMenuItems(true);
                } else {
                  resetUserDetails();
                }
            });
        } else {
            resetUserDetails();
        }

        setupRecyclerView();
        setupSearchView();
        setupSwipeRefreshLayout();
        setUpToolbar();
        setupBottomNavigationView();
        setupNavigationView();

        searchBtn.setOnClickListener(v -> {
            searchView.setVisibility(View.VISIBLE);
            searchView.setIconified(false);
        });

        // Check if a new video was passed in the intent
        newVideo = (VideoItem) getIntent().getSerializableExtra("new_video");
        if (newVideo != null) {
            onVideoUploaded(newVideo);
        }

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (currentUser != null) {
                Intent uploadIntent = new Intent(HomeActivity.this, UploadVideoActivity.class);
                uploadIntent.putExtra("username", username);
                uploadIntent.putExtra("token", token);
                uploadIntent.putExtra("currentUser", currentUser);
                startActivityForResult(uploadIntent, UPLOAD_VIDEO_REQUEST_CODE);
            } else {
                Toast.makeText(HomeActivity.this, "Please log in to upload a video", Toast.LENGTH_SHORT).show();
            }
        });

        MenuItem switchItem = navigationView.getMenu().findItem(R.id.switchMode);
        SwitchCompat switchCompat = (SwitchCompat) switchItem.getActionView();
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Fetch videos
       videoItemViewModel.fetch20Videos();
    }

    private void resetUserDetails() {
        userName.setText(getString(R.string.user_welcome));
        userImage.setImageResource(R.drawable.user_img);
        updateMenuItems(false);
    }

    private void setupRecyclerView() {
        videoItemList = new ArrayList<>();
        videoAdapter = new VideoAdapter(this, videoItemList, username, token, videoItemViewModel, currentUser);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        lvRecyclerView.setLayoutManager(gridLayoutManager);
        lvRecyclerView.setAdapter(videoAdapter);

        videoItemViewModel.getVideoListData().observe(this, videoItems -> {
            if (videoItems != null && !videoItems.isEmpty()) {
                videoAdapter.updateVideoList(videoItems);
                videoItemList = videoItems;
            }
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setVisibility(View.GONE);
                filterVideos(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterVideos(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            searchView.setVisibility(View.GONE);
            videoAdapter.updateVideoList(videoItemList);
            return false;
        });
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            videoItemViewModel.fetch20Videos();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void performLogout() {
        currentUser = null;
        username = null;
        token = null;
        resetUserDetails();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    private void setUserDetails(User user) {
        userName.setText(user.getUsername());
        try {
            String imageData = user.getImage();
            if (imageData.startsWith("data:image/jpeg;base64,")) {
                imageData = imageData.substring("data:image/jpeg;base64,".length());
            }

            byte[] decodedBytes = Base64.decode(imageData, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            if (bitmap != null) {
                Glide.with(this)
                        .load(bitmap)
                        .apply(RequestOptions.circleCropTransform())
                        .into(userImage);
            } else {
                userImage.setImageResource(R.drawable.user_img);
            }
        } catch (IllegalArgumentException e) {
            Log.e("HomeActivity", "Invalid Base64 string", e);
            userImage.setImageResource(R.drawable.user_img);
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupBottomNavigationView() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("token", token);
            intent.putExtra("currentUser", currentUser);
            if (item.getItemId() == R.id.bottom_shorts) {
                intent = new Intent(HomeActivity.this, ShortsActivity.class);
            }
            startActivity(intent);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoItemList != null) {
            videoItemList.clear();
        } else {
            videoItemList = new ArrayList<>();
        }
        videoAdapter.notifyDataSetChanged();
        updateMenuItems(currentUser != null);
    }

    private void updateMenuItems(boolean isLoggedIn) {
        if (loginItem != null && signupItem != null && logoutItem != null) {
            loginItem.setVisible(!isLoggedIn);
            signupItem.setVisible(!isLoggedIn);
            logoutItem.setVisible(isLoggedIn);
        }
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            Intent intent = null;
            int itemId = menuItem.getItemId();
            if (itemId == R.id.nav_trending) {
                intent = new Intent(HomeActivity.this, MainActivity.class);
            } else if (itemId == R.id.nav_music) {
                intent = new Intent(HomeActivity.this, MusicActivity.class);
            } else if (itemId == R.id.nav_gaming) {
                intent = new Intent(HomeActivity.this, GamingActivity.class);
            } else if (itemId == R.id.nav_movies) {
                intent = new Intent(HomeActivity.this, MoviesActivity.class);
            } else if (itemId == R.id.nav_news) {
                intent = new Intent(HomeActivity.this, NewsActivity.class);
            } else if (itemId == R.id.nav_sports) {
                intent = new Intent(HomeActivity.this, SportsActivity.class);
            } else if (itemId == R.id.log_in) {
                intent = new Intent(HomeActivity.this, LoginActivity.class);
            } else if (itemId == R.id.signup) {
                intent = new Intent(HomeActivity.this, SignupActivity.class);
            } else if (itemId == R.id.logout) {
                performLogout();
                intent = new Intent(HomeActivity.this, HomeActivity.class);
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

    private void filterVideos(String query) {
        List<VideoItem> filteredList = new ArrayList<>();
        for (VideoItem video : videoItemList) {
            if (video.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(video);
            }
        }
        videoAdapter.updateVideoList(filteredList);
    }

    public void onVideoUploaded(VideoItem newVideo) {
        videoItemViewModel.addVideoLocally(newVideo); // Add the video to the Room database
        videoItemList.add(newVideo);
        videoAdapter.notifyItemInserted(videoItemList.size() - 1);
        lvRecyclerView.scrollToPosition(videoItemList.size() - 1);
        Toast.makeText(this, "Video added successfully!", Toast.LENGTH_SHORT).show();
    }
}
