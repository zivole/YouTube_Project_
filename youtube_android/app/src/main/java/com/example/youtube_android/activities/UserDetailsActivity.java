package com.example.youtube_android.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.youtube_android.R;
import com.example.youtube_android.entities.User;
import com.example.youtube_android.utils.GlobalToken;
import com.example.youtube_android.viewModels.UsersViewModel;

import java.util.regex.Pattern;

public class UserDetailsActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private EditText firstName, lastName, username, password;
    private Button editButton, deleteButton, saveButton, cancelEditButton, cancelButton;
    private Button confirmDeleteButton, cancelDeleteButton;
    private LinearLayout editDeleteLayout, saveCancelLayout, confirmDeleteLayout;

    private UsersViewModel usersViewModel;
    private String token;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details_activity);

        // Initialize views
        profileImageView = findViewById(R.id.profileImageView);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        saveButton = findViewById(R.id.saveButton);
        cancelEditButton = findViewById(R.id.cancelEditButton);
        cancelButton = findViewById(R.id.cancelButton);
        confirmDeleteButton = findViewById(R.id.confirmDeleteButton);
        cancelDeleteButton = findViewById(R.id.cancelDeleteButton);
        editDeleteLayout = findViewById(R.id.editDeleteLayout);
        saveCancelLayout = findViewById(R.id.saveCancelLayout);
        confirmDeleteLayout = findViewById(R.id.confirmDeleteLayout);

        // Get user details and token from intent
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        currentUser = (User) intent.getSerializableExtra("currentUser");
        String username = intent.getStringExtra("username");
        String _id = intent.getStringExtra("_id");

        // Get profile image from intent
        String profileImage = intent.getStringExtra("profileImage");
        if (profileImage != null) {
            setProfileImage(profileImage);
        }

        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        // Fetch user details from server using the token
        usersViewModel.getUserFromToken(token);
        usersViewModel.getUserDetailsResult().observe(this, user -> {
            if (user != null) {
                currentUser = user;
                populateUserDetails(user);
            } else {
                Toast.makeText(UserDetailsActivity.this, "Failed to load user details", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle edit button click
        editButton.setOnClickListener(v -> enterEditMode());

        // Handle delete button click
        deleteButton.setOnClickListener(v -> {
            editDeleteLayout.setVisibility(View.GONE);
            confirmDeleteLayout.setVisibility(View.VISIBLE);
        });

        // Handle confirm delete button click
        confirmDeleteButton.setOnClickListener(v -> deleteUser());

        // Handle cancel delete button click
        cancelDeleteButton.setOnClickListener(v -> {
            confirmDeleteLayout.setVisibility(View.GONE);
            editDeleteLayout.setVisibility(View.VISIBLE);
        });

        // Handle save button click
        saveButton.setOnClickListener(v -> saveChanges());

        // Handle cancel edit button click
        cancelEditButton.setOnClickListener(v -> exitEditMode());

        // Handle cancel button click (return to home)
        cancelButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(UserDetailsActivity.this, HomeActivity.class);
            homeIntent.putExtra("username", currentUser.getUsername());
            token = GlobalToken.token;
            homeIntent.putExtra("token", token);
            homeIntent.putExtra("currentUser", currentUser);
            startActivity(homeIntent);
            finish();
        });
    }

    private void setProfileImage(String imageData) {
        try {
            if (imageData.startsWith("data:image/jpeg;base64,")) {
                imageData = imageData.substring("data:image/jpeg;base64,".length());
            }

            byte[] decodedBytes = Base64.decode(imageData, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            if (bitmap != null) {
                Glide.with(this)
                        .load(bitmap)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.user_img);
            }
        } catch (IllegalArgumentException e) {
            Log.e("UserDetailsActivity", "Invalid Base64 string", e);
            profileImageView.setImageResource(R.drawable.user_img);
        }
    }

    private void populateUserDetails(User user) {
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        username.setText(user.getUsername());
    }

    private void enterEditMode() {
        // Show password field and save/cancel buttons
        password.setVisibility(View.VISIBLE);
        editDeleteLayout.setVisibility(View.GONE);
        saveCancelLayout.setVisibility(View.VISIBLE);

        // Make fields editable
        firstName.setEnabled(true);
        lastName.setEnabled(true);
        username.setEnabled(true);
    }

    private void exitEditMode() {
        // Hide password field and save/cancel buttons
        password.setVisibility(View.GONE);
        saveCancelLayout.setVisibility(View.GONE);
        editDeleteLayout.setVisibility(View.VISIBLE);

        // Make fields non-editable
        firstName.setEnabled(false);
        lastName.setEnabled(false);
        username.setEnabled(false);
    }

    private void saveChanges() {
        String newFirstName = firstName.getText().toString().isEmpty() ? currentUser.getFirstName() : firstName.getText().toString();
        String newLastName = lastName.getText().toString().isEmpty() ? currentUser.getLastName() : lastName.getText().toString();
        String newUsername = username.getText().toString().isEmpty() ? currentUser.getUsername() : username.getText().toString();
        String newPassword = password.getText().toString();

        if (!newPassword.isEmpty() && !isPasswordValid(newPassword)) {
            Toast.makeText(UserDetailsActivity.this, "Password must contain at least 8 characters, including uppercase, lowercase, number, and special character.", Toast.LENGTH_LONG).show();
            return;
        }

        User newUser = currentUser;
        newUser.setFirstName(newFirstName);
        newUser.setLastName(newLastName);
        newUser.setUsername(newUsername);

        if (!newPassword.isEmpty()) {
            newUser.setPassword(newPassword);
        }

        usersViewModel.updateUser(newUser, result -> {
            if (result != null) {
                Toast.makeText(UserDetailsActivity.this, "User updated successfully", Toast.LENGTH_SHORT).show();
                currentUser = newUser;
                token = GlobalToken.token;
                exitEditMode();
            } else {
                Toast.makeText(UserDetailsActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean isPasswordValid(String password) {
        Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$");
        return passwordPattern.matcher(password).matches();
    }



    private void deleteUser() {
        usersViewModel.deleteUser(currentUser.get_id(), result -> {
            if (result != null) {
                Toast.makeText(UserDetailsActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                Intent homeIntent = new Intent(UserDetailsActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
            } else {
                Toast.makeText(UserDetailsActivity.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                confirmDeleteLayout.setVisibility(View.GONE);
                editDeleteLayout.setVisibility(View.VISIBLE);
            }
        });
    }
}
