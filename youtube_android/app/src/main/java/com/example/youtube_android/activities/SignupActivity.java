package com.example.youtube_android.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.youtube_android.R;
import com.example.youtube_android.utils.SignUpRequest;
import com.example.youtube_android.viewModels.UsersViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class SignupActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_PERMISSION = 100;
    private EditText firstName, lastName, username, password, confirmPassword;
    private Button addImageButton;
    private Button cancelButton, signUpButton;
    private TextView loginText;
    private ImageView profileImageView;
    private String currentPhotoPath;
    private UsersViewModel usersViewModel;
    private String imageBase64;
    private  String userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        addImageButton = findViewById(R.id.addImageButton);
        cancelButton = findViewById(R.id.cancelButton);
        signUpButton = findViewById(R.id.signUpButton);
        loginText = findViewById(R.id.loginText);
        profileImageView = findViewById(R.id.profileImageView);

        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    String firstNameStr = firstName.getText().toString();
                    String lastNameStr = lastName.getText().toString();
                    String user = username.getText().toString();
                    String passwordStr = password.getText().toString();
                    String confirmPasswordStr = confirmPassword.getText().toString();

                    SignUpRequest signUpRequest = new SignUpRequest(
                            UUID.randomUUID().toString(), // Create a random ID
                            firstNameStr,
                            lastNameStr,
                            user,
                            passwordStr,
                            confirmPasswordStr,
                            userImage
                    );

                    usersViewModel.signUp(signUpRequest);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSourceDialog();
            }
        });

        usersViewModel.getSignUpResult().observe(this, success -> {
            if (success) {
                Toast.makeText(SignupActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SignupActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImageSourceDialog() {
        CharSequence[] options = {"Choose from Gallery", "Take a Photo"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(options, new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                if (which == 0) {
                    openImageChooser();
                } else if (which == 1) {
                    checkPermissions();
                }
            }
        });
        builder.show();
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                showToast("Permission denied");
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImageView.setImageBitmap(getCircularBitmap(bitmap));
                profileImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                saveImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profileImageView.setImageBitmap(getCircularBitmap(imageBitmap));
            profileImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            saveImage(imageBitmap);
        }
    }

    private void saveImage(Bitmap bitmap) {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String base64String = "data:image/jpeg;base64,";
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = new File(storageDir, imageFileName);
        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            currentPhotoPath = imageFile.getAbsolutePath();
            imageBase64 = convertToBase64(bitmap); // Convert image to Base64
            userImage = base64String + imageBase64;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = width > height ? height : width;
        int newHeight = width > height ? height : width;

        Bitmap output = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect srcRect = new Rect(0, 0, newWidth, newHeight);
        final Rect destRect = new Rect(0, 0, newWidth, newHeight);
        final RectF rectF = new RectF(destRect);

        float radius = newWidth / 2;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xff424242);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, srcRect, destRect, paint);

        return output;
    }

    private boolean validateInput() {
        boolean valid = true;
        if (TextUtils.isEmpty(firstName.getText().toString())) {
            firstName.setError("First Name is required");
            valid = false;
        }
        if (TextUtils.isEmpty(lastName.getText().toString())) {
            lastName.setError("Last Name is required");
            valid = false;
        }
        if (TextUtils.isEmpty(username.getText().toString())) {
            username.setError("Username is required");
            valid = false;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Password is required");
            valid = false;
        } else if (!isValidPassword(password.getText().toString())) {
            password.setError("Password must contain upper and lower case letters, numbers, and special characters");
            valid = false;
        }
        if (TextUtils.isEmpty(confirmPassword.getText().toString())) {
            confirmPassword.setError("Confirm Password is required");
            valid = false;
        } else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            confirmPassword.setError("Passwords do not match");
            valid = false;
        }
        if (currentPhotoPath == null) {
            showToast("Profile image is required");
            valid = false;
        }
        return valid;
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}";
        return password.matches(passwordPattern);
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void clearInputs() {
        firstName.setText("");
        lastName.setText("");
        username.setText("");
        password.setText("");
        confirmPassword.setText("");
        profileImageView.setImageResource(android.R.drawable.ic_menu_camera);
        currentPhotoPath = null;
    }

    private String convertToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}