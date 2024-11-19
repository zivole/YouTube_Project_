package com.example.youtube_android.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.example.youtube_android.R;
import com.example.youtube_android.daoes.UsersDao;
import com.example.youtube_android.daoes.VideoDao;
import com.example.youtube_android.databases.UsersDB;
import com.example.youtube_android.databases.VideoDB;
import com.example.youtube_android.entities.User;
import com.example.youtube_android.entities.VideoItem;
import com.example.youtube_android.repositories.UsersRepository;
import com.example.youtube_android.viewModels.VideoItemViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;



public class UploadVideoActivity extends AppCompatActivity {

    private EditText titleEditText;
    private ImageView logo;
    private ImageButton selectVideoButton;
    private FloatingActionButton uploadButton;

    private static final int PICK_VIDEO_REQUEST = 1;
    private Uri videoUri;
    private VideoDB videoDB;
    private VideoDao videoDao;
    private String username;
    private String token;
    private String userImage;
    private VideoItemViewModel videoItemViewModel;

    private User currentUser;
    private UsersRepository usersRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        titleEditText = findViewById(R.id.uploadTitle);
        selectVideoButton = findViewById(R.id.uploadImage);
        uploadButton = findViewById(R.id.uploadButton);
        logo = findViewById(R.id.logo);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        token = intent.getStringExtra("token");
        userImage = intent.getStringExtra("userImage");
        currentUser = (User) intent.getSerializableExtra("currentUser");


        selectVideoButton.setOnClickListener(v -> openFilePicker());

        logo.setOnClickListener(v -> {
            Intent homeIntent = new Intent(UploadVideoActivity.this, HomeActivity.class);
            homeIntent.putExtra("username", username);
            homeIntent.putExtra("token", token);
            startActivity(homeIntent);
            finish();
        });

        videoDB = Room.databaseBuilder(getApplicationContext(), VideoDB.class, "VideosDB")
                .allowMainThreadQueries().build();
        videoDao = videoDB.videoDao();

        UsersDB usersDB = UsersDB.getDatabase(getApplicationContext());
        UsersDao usersDao = usersDB.usersDao();
        usersRepository = new UsersRepository(usersDao);

        videoItemViewModel = new ViewModelProvider(this).get(VideoItemViewModel.class);

        uploadButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();

            if (title.isEmpty() || videoUri == null) {
                Toast.makeText(UploadVideoActivity.this, "Please fill in all fields and select a video", Toast.LENGTH_SHORT).show();
            } else {
                // Display a ProgressDialog to indicate that the upload is in progress
                ProgressDialog progressDialog = new ProgressDialog(UploadVideoActivity.this);
                progressDialog.setMessage("Uploading video, please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                createVideoItemFromFile(UploadVideoActivity.this, title, videoUri, new VideoItemCreationCallback() {
                    @Override
                    public void onSuccess(VideoItem videoItem) {
                        File videoFile = new File(videoItem.getPath());
                        videoItemViewModel.uploadVideo(videoItem, videoFile, videoUri, token, new VideoItemViewModel.VideoUploadCallback() {
                            @Override
                            public void onSuccess() {
                                progressDialog.dismiss();
                                Toast.makeText(UploadVideoActivity.this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();
                                Intent homeIntent = new Intent(UploadVideoActivity.this, HomeActivity.class);
                                homeIntent.putExtra("username", username);
                                homeIntent.putExtra("token", token);
                                homeIntent.putExtra("currentUser", currentUser);
                                startActivity(homeIntent);
                                finish();
                            }

                            @Override
                            public void onError(Throwable t) {
                                Toast.makeText(UploadVideoActivity.this, "Failed to upload video: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(UploadVideoActivity.this, "Failed to create video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            videoUri = data.getData();
            Toast.makeText(this, "Video selected: " + videoUri.getPath(), Toast.LENGTH_SHORT).show();
        }
    }

//    private File createFileFromUri(Uri uri, Context context, String title) {
//        // Sanitize the title to create a valid file name
//        String sanitizedTitle = title.replaceAll("[^a-zA-Z0-9\\s]", "").replace(" ", "_");
//
//        // Create the file with the sanitized title as part of the name
//        File outputFile = new File(context.getCacheDir(), sanitizedTitle + ".mp4");
//        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
//             FileOutputStream fos = new FileOutputStream(outputFile)) {
//
//            byte[] buf = new byte[8192];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buf)) > 0) {
//                fos.write(buf, 0, bytesRead);
//            }
//        } catch (Exception e) {
//            Log.e("VideoCreation", "Error creating video file", e);
//            return null;
//        }
//        Log.i("VideoCreation", "File created at " + outputFile.getAbsolutePath());
//        return outputFile;
//    }



    private File createFileFromUri(Uri uri, Context context, String title) {
        // Sanitize the title to create a valid file name
        String sanitizedTitle = title.replaceAll("[^a-zA-Z0-9\\s]", "").replace(" ", "_");

        // Ensure the uploads directory exists
        File directory = new File(context.getFilesDir(), "uploads");
        if (!directory.exists()) {
            directory.mkdirs();  // Create the directory if it doesn't exist
        }

        // Create the file with the sanitized title as part of the name
        File outputFile = new File(directory, sanitizedTitle + ".mp4");
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] buf = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                fos.write(buf, 0, bytesRead);
            }
        } catch (Exception e) {
            Log.e("VideoCreation", "Error creating video file", e);
            return null;
        }
        Log.i("VideoCreation", "File created at " + outputFile.getAbsolutePath());
        return outputFile;
    }


    // Utility function to convert UUID to ObjectId
    private String convertUUIDToObjectId(String uuid) {
        // Remove dashes and take the first 24 characters (a simple, but not ideal, approach)
        return uuid.replace("-", "").substring(0, 24);
    }

    // Method to extract the title from the original file path
    public static String getTitleFromPath(String path) {
        // Extract the file name (the part after the last backslash)
        String fileName = path.substring(path.lastIndexOf("\\") + 1);

        // Find the last dash and extract the part after it
        int lastDashIndex = fileName.lastIndexOf('-');
        if (lastDashIndex != -1) {
            return fileName.substring(lastDashIndex + 1); // Return the part after the dash
        } else {
            return fileName; // If no dash is found, return the original file name
        }
    }

//    private void createVideoItemFromFile(Context context, String title, Uri uri, VideoItemCreationCallback callback) {
//        Executor executor = Executors.newSingleThreadExecutor();
//        executor.execute(() -> {
//            File videoPath = createFileFromUri(uri, context, title);
//            if (videoPath == null || !videoPath.exists()) {
//                Log.e("VideoCreation", "Failed to create video from Uri");
//                runOnUiThread(() -> callback.onError(new Exception("Failed to create video from Uri")));
//                return;
//            }
//
//            Bitmap thumbnailBitmap = createVideoThumbnail(videoPath.getAbsolutePath());
//            String thumbnailPath = null;
//            if (thumbnailBitmap != null) {
//                thumbnailPath = saveThumbnail(context, thumbnailBitmap);
//            }
//
//            String currentDate = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
//            String publishDate = currentDate;
//            String viewsCount = "0";
//            // Generate UUID and convert it to ObjectId
//            String uuid = UUID.randomUUID().toString();
//            String objectId = convertUUIDToObjectId(uuid);
//
//            User user = usersRepository.getUserLocally(username);
////            if(user!= null && !user.getImage().startsWith(base64String)){
////                 userImage = base64String + user.getImage();
////            } else {
//                 userImage = user != null ? user.getImage() : null;
//           // }
//
//            String directory = "uploads\\";
//            String videoEnd = ".mp4";
//            String addaptVideoPath = directory + title + videoEnd ;
//            VideoItem videoItem = new VideoItem(
//                    //UUID.randomUUID().toString(),
//                    objectId,
//                    title,
//                    thumbnailPath,
//                    user != null ? user.getUsername() : "Anonymous",
//                    viewsCount,
//                    publishDate,
//                    addaptVideoPath,
//                    userImage,
//                    user != null ? user.getUsername() : username,
//                    new ArrayList<>()
//            );
//
//            runOnUiThread(() -> callback.onSuccess(videoItem));
//        });
//    }


    private void createVideoItemFromFile(Context context, String title, Uri uri, VideoItemCreationCallback callback) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Ensure the directory exists and create the file
            File videoPath = createFileFromUri(uri, context, title);
            if (videoPath == null || !videoPath.exists()) {
                Log.e("VideoCreation", "Failed to create video from Uri");
                runOnUiThread(() -> callback.onError(new Exception("Failed to create video from Uri")));
                return;
            }

            // Generate the video thumbnail
            Bitmap thumbnailBitmap = createVideoThumbnail(videoPath.getAbsolutePath());
            String thumbnailPath = null;
            if (thumbnailBitmap != null) {
                thumbnailPath = saveThumbnail(context, thumbnailBitmap);
            }

            // Generate other metadata
            String currentDate = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
            String publishDate = currentDate;
            String viewsCount = "0";
            String uuid = UUID.randomUUID().toString();
            String objectId = convertUUIDToObjectId(uuid);

            // Retrieve user information
            User user = usersRepository.getUserLocally(username);
            userImage = user != null ? user.getImage() : null;

            // Adjust the video path with the sanitized title
            String directory = context.getFilesDir() + File.separator + "uploads" + File.separator;
            String videoEnd = ".mp4";
            String adaptedVideoPath = directory + title + videoEnd;

            // Create VideoItem object
            VideoItem videoItem = new VideoItem(
                    objectId,
                    title,
                    thumbnailPath,
                    user != null ? user.getUsername() : "Anonymous",
                    viewsCount,
                    publishDate,
                    adaptedVideoPath,
                    userImage,
                    user != null ? user.getUsername() : username,
                    new ArrayList<>()
            );

            // Pass the result back to the UI thread
            runOnUiThread(() -> callback.onSuccess(videoItem));
        });
    }


    public interface VideoItemCreationCallback {
        void onSuccess(VideoItem videoItem);
        void onError(Exception e);
    }

    private Bitmap createVideoThumbnail(String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoPath);
            return retriever.getFrameAtTime(1000000);
        } catch (IllegalArgumentException e) {
            Log.e("ThumbnailCreation", "Failed to create thumbnail", e);
            return null;
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                Log.e("ThumbnailCreation", "Error releasing MediaMetadataRetriever", e);
            }
        }
    }

    private String saveThumbnail(Context context, Bitmap bitmap) {
        File thumbnailFile = new File(context.getFilesDir(), "thumbnail_" + System.currentTimeMillis() + ".png");
        try (FileOutputStream fos = new FileOutputStream(thumbnailFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            Log.e("ThumbnailSave", "Error saving thumbnail", e);
            return null;
        }
        return thumbnailFile.getAbsolutePath();
    }
}