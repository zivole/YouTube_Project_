package com.example.youtube_android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.youtube_android.R;
import com.example.youtube_android.entities.User;
import com.example.youtube_android.utils.GlobalToken;
import com.example.youtube_android.viewModels.UsersViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private Button loginButton, cancelButton;
    private TextView signUpText;
    private UsersViewModel usersViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        cancelButton = findViewById(R.id.cancelButton);
        signUpText = findViewById(R.id.signUpText);

        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        usersViewModel.getSignInResult().observe(this, signInSuccess -> {
            if (signInSuccess != null && signInSuccess) {
                if (GlobalToken.token != null) {
                    if (!isFinishing()) {
                        showToast("Login successful");
                    }
                    User user = usersViewModel.getUserDetailsResult().getValue();
                    if (user != null) {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("username", user.getUsername());
                        intent.putExtra("token", GlobalToken.token);
                        intent.putExtra("currentUser", user);
                        startActivity(intent);
                        finish();
                    } else {
                        if (!isFinishing()) {
                            showToast("Failed to retrieve user details");
                        }
                    }
                } else {
                    if (!isFinishing()) {
                        showToast("Token generation failed");
                    }
                }
            } else {
                if (!isFinishing()) {
                    showToast("Invalid username or password");
                }
            }
        });




        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                if (TextUtils.isEmpty(user)) {
                    username.setError("Username is required");
                    return;
                }

                if (TextUtils.isEmpty(pass)) {
                    password.setError("Password is required");
                    return;
                }

                usersViewModel.signIn(user, pass);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
