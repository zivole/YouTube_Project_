package com.example.youtube_android.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.youtube_android.MainActivity;
import com.example.youtube_android.R;
import com.example.youtube_android.adapters.VideoAdapter;
import com.example.youtube_android.entities.User;
import com.example.youtube_android.noUseActivities.GamingActivity;
import com.example.youtube_android.noUseActivities.MoviesActivity;
import com.example.youtube_android.noUseActivities.MusicActivity;
import com.example.youtube_android.noUseActivities.NewsActivity;
import com.example.youtube_android.noUseActivities.SportsActivity;
import com.example.youtube_android.utils.ImageUtil;
import com.example.youtube_android.viewModels.UsersViewModel;
import com.example.youtube_android.viewModels.VideoItemViewModel;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class UserVideosActivity extends AppCompatActivity {

    private ImageView userImage;
    private ImageView selectedUserImage;
    private TextView userName;
    private TextView selectedUserName;
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private VideoItemViewModel videoItemViewModel;
    private UsersViewModel usersViewModel;
    private String token;
    private String username;
    private User currentUser;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private FloatingActionButton fab;
    private BottomNavigationView bottomNavigationView;
    private BottomAppBar bottomAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_videos);

        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_menu);
        fab = findViewById(R.id.fab);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        Toolbar toolbar = findViewById(R.id.toolbar_video);

        // Initialize the selected user views
        selectedUserImage = findViewById(R.id.selected_user_img);
        selectedUserName = findViewById(R.id.selected_user_name);

        // Retrieve data from intent
        Intent intent = getIntent();
        final String userId = intent.getStringExtra("userId");
        final String selectedUserNameFromIntent = intent.getStringExtra("selectedUserName");
        final String selectedUserImageFromIntent = intent.getStringExtra("selectedUserImage");
        username = intent.getStringExtra("username");
        token = intent.getStringExtra("token");
        currentUser = (User) intent.getSerializableExtra("currentUser");

        // Initialize ViewModels
        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        videoItemViewModel = new ViewModelProvider(this).get(VideoItemViewModel.class);

        // Set up Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set up user details view for logged-in user
        View headerView = navigationView.getHeaderView(0);
        userImage = headerView.findViewById(R.id.user_img);
        userName = headerView.findViewById(R.id.user_name);

        // Trigger user details fetch and observe the result
        usersViewModel.getUserFromToken(token);
        usersViewModel.getUserDetailsResult().observe(this, user -> {
            if (user != null) {
                currentUser = user;
                setUserDetails(userImage, userName, currentUser);
                updateMenuItems(true);
            } else {
                resetUserDetails();
            }
        });

        // Set the selected user's details in the main content area
        setUserDetails(selectedUserImage, selectedUserName, selectedUserNameFromIntent, selectedUserImageFromIntent);

        // Setup RecyclerView
        setupRecyclerView();

        // Fetch videos for the selected user
        fetchUserVideos(userId);

        // Handle logo click to return to home
        findViewById(R.id.logo_video).setOnClickListener(v -> {
            Intent homeIntent = new Intent(UserVideosActivity.this, HomeActivity.class);
            homeIntent.putExtra("username", username);
            homeIntent.putExtra("token", token);
            homeIntent.putExtra("currentUser", currentUser);
            videoItemViewModel.fetch20Videos();
            startActivity(homeIntent);

//            // Clear all videos from Room
//            videoItemViewModel.clearAllVideosFromRoom(() -> {
//                // Once the clearing is done, fetch the 20 videos from the server
//                videoItemViewModel.fetch20Videos();
//            startActivity(homeIntent);
//            });
        });

        // Handle FloatingActionButton click for video upload
        fab.setOnClickListener(view -> {
            if (currentUser != null) {
                Intent uploadIntent = new Intent(UserVideosActivity.this, UploadVideoActivity.class);
                uploadIntent.putExtra("username", username);
                uploadIntent.putExtra("token", token);
                uploadIntent.putExtra("currentUser", currentUser);
                startActivity(uploadIntent);
            } else {
                Toast.makeText(UserVideosActivity.this, "Please log in to upload a video", Toast.LENGTH_SHORT).show();
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

        setupNavigationView();
        setupBottomNavigationView();
    }

    private void setUserDetails(ImageView imageView, TextView textView, User user) {
        textView.setText(user.getUsername());

        if (user.getImage() != null) {
            if (isBase64(user.getImage())) {
                // Handle Base64 encoded image
                try {
                    Bitmap decodedBitmap = ImageUtil.decodeBase64ToBitmap(user.getImage(), 100, 100);
                    if (decodedBitmap != null) {
                        // Compress image
                        String compressedImageBase64 = ImageUtil.resizeAndCompressAndEncodeImage(decodedBitmap, 100, 75);

                        // Save the compressed image to file and keep the path in Room
                        String imagePath = ImageUtil.saveBase64AsFile(this, compressedImageBase64, "user_image_" + user.getUsername() + ".jpg");

                        // Load the image into the ImageView
                        Glide.with(this)
                                .load(imagePath)
                                .circleCrop()
                                .into(imageView);
                    } else {
                        imageView.setImageResource(R.drawable.user_img);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    imageView.setImageResource(R.drawable.user_img);
                }
            } else {
                // Handle URL or file path
                try {
                    Glide.with(this)
                            .load(user.getImage()) // Load directly from URL or file path
                            .circleCrop()
                            .into(imageView);
                } catch (Exception e) {
                    e.printStackTrace();
                    imageView.setImageResource(R.drawable.user_img);
                }
            }
        } else {
            imageView.setImageResource(R.drawable.user_img);
        }
    }

    private void setUserDetails(ImageView imageView, TextView textView, String name, String imageUrlOrBase64) {
        textView.setText(name);

        if (imageUrlOrBase64 != null && !imageUrlOrBase64.isEmpty()) {
            if (isBase64(imageUrlOrBase64)) {
                // Handle Base64 encoded image
                try {
                    Bitmap decodedBitmap = ImageUtil.decodeBase64ToBitmap(imageUrlOrBase64, 100, 100);
                    if (decodedBitmap != null) {
                        // Compress image
                        String compressedImageBase64 = ImageUtil.resizeAndCompressAndEncodeImage(decodedBitmap, 100, 75);

                        // Save the compressed image to file and keep the path in Room
                        String imagePath = ImageUtil.saveBase64AsFile(this, compressedImageBase64, "user_image_" + name + ".jpg");

                        // Load the image into the ImageView
                        Glide.with(this)
                                .load(imagePath)
                                .circleCrop()
                                .into(imageView);
                    } else {
                        imageView.setImageResource(R.drawable.user_img);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    imageView.setImageResource(R.drawable.user_img);
                }
            } else {
                // Handle URL or file path
                try {
                    Glide.with(this)
                            .load(imageUrlOrBase64) // Load directly from URL or file path
                            .circleCrop()
                            .into(imageView);
                } catch (Exception e) {
                    e.printStackTrace();
                    imageView.setImageResource(R.drawable.user_img);
                }
            }
        } else {
            imageView.setImageResource(R.drawable.user_img);
        }
    }

    private boolean isBase64(String str) {
        // Check if the string is in Base64 format (simple heuristic check)
        return str.startsWith("data:image") || str.length() % 4 == 0 && str.matches("^[A-Za-z0-9+/=]+$");
    }

    private void resetUserDetails() {
        userName.setText(getString(R.string.user_welcome));
        userImage.setImageResource(R.drawable.user_img);
        updateMenuItems(false);
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoAdapter = new VideoAdapter(this, new ArrayList<>(), username, token, videoItemViewModel, currentUser);
        recyclerView.setAdapter(videoAdapter);
    }

    private void fetchUserVideos(String userId) {
        videoItemViewModel.getUserVideos(userId).observe(this, videos -> {
            if (videos != null) {
                videoAdapter.updateVideoList(videos);
            } else {
                Toast.makeText(UserVideosActivity.this, "Failed to fetch videos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            Intent intent = null;
            int itemId = menuItem.getItemId();
            if (itemId == R.id.nav_trending) {
                intent = new Intent(UserVideosActivity.this, MainActivity.class);
            } else if (itemId == R.id.nav_music) {
                intent = new Intent(UserVideosActivity.this, MusicActivity.class);
            } else if (itemId == R.id.nav_gaming) {
                intent = new Intent(UserVideosActivity.this, GamingActivity.class);
            } else if (itemId == R.id.nav_movies) {
                intent = new Intent(UserVideosActivity.this, MoviesActivity.class);
            } else if (itemId == R.id.nav_news) {
                intent = new Intent(UserVideosActivity.this, NewsActivity.class);
            } else if (itemId == R.id.nav_sports) {
                intent = new Intent(UserVideosActivity.this, SportsActivity.class);
            } else if (itemId == R.id.log_in) {
                intent = new Intent(UserVideosActivity.this, LoginActivity.class);
            } else if (itemId == R.id.signup) {
                intent = new Intent(UserVideosActivity.this, SignupActivity.class);
            } else if (itemId == R.id.logout) {
                performLogout();
                intent = new Intent(UserVideosActivity.this, UserVideosActivity.class);
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

    private void setupBottomNavigationView() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent  intent = null;
            if (item.getItemId() == R.id.bottom_home) {
                intent = new Intent(UserVideosActivity.this, HomeActivity.class);
                // Clear all videos from Room and then fetch the 20 videos from the server
                Intent finalIntent = intent;
                videoItemViewModel.fetch20Videos();
                startActivity(finalIntent);
//                videoItemViewModel.clearAllVideosFromRoom(() -> {
//                    videoItemViewModel.fetch20Videos();
//                    startActivity(finalIntent);
//                });
            } else if (item.getItemId() == R.id.bottom_shorts) {
                intent = new Intent(UserVideosActivity.this, ShortsActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("token", token);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            } else {
                intent = null;
            }
            return intent != null;
        });
    }

    private void performLogout() {
        currentUser = null;
        username = null;
        token = null;
        resetUserDetails();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    private void updateMenuItems(boolean isLoggedIn) {
        Menu menu = navigationView.getMenu();
        MenuItem loginItem = menu.findItem(R.id.log_in);
        MenuItem signupItem = menu.findItem(R.id.signup);
        MenuItem logoutItem = menu.findItem(R.id.logout);

        loginItem.setVisible(!isLoggedIn);
        signupItem.setVisible(!isLoggedIn);
        logoutItem.setVisible(isLoggedIn);
    }
}
