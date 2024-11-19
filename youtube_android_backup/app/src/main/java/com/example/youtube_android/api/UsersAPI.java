package com.example.youtube_android.api;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.youtube_android.R;
import com.example.youtube_android.YouTubeApplication;
import com.example.youtube_android.daoes.UsersDao;
import com.example.youtube_android.entities.User;
import com.example.youtube_android.repositories.UsersRepository;
import com.example.youtube_android.utils.GlobalToken;
import com.example.youtube_android.utils.SignInRequest;
import com.example.youtube_android.utils.SignUpRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsersAPI {
    private MutableLiveData<List<User>> userListData;
    private MutableLiveData<Boolean> signUpResult;
    private MutableLiveData<Boolean> signInResult;
    private MutableLiveData<User> userDetailsResult;
    private MutableLiveData<Boolean> validatePasswordResult;
    private MutableLiveData<Boolean> checkUsernameExistsResult;
    private MutableLiveData<User> getUserByUsernameResult;
    private UsersDao usersDao;
    private UsersRepository usersRepository;
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;

    public UsersAPI(MutableLiveData<List<User>> userListData, UsersDao usersDao, MutableLiveData<Boolean> signUpResult, MutableLiveData<Boolean> signInResult, MutableLiveData<User> userDetailsResult, MutableLiveData<Boolean> validatePasswordResult, MutableLiveData<Boolean> checkUsernameExistsResult, MutableLiveData<User> getUserByUsernameResult, UsersRepository usersRepository) {
        this.userListData = userListData;
        this.usersDao = usersDao;
        this.signUpResult = signUpResult;
        this.signInResult = signInResult;
        this.userDetailsResult = userDetailsResult;
        this.validatePasswordResult = validatePasswordResult;
        this.checkUsernameExistsResult = checkUsernameExistsResult;
        this.getUserByUsernameResult = getUserByUsernameResult;
        this.usersRepository = usersRepository;

        retrofit = new Retrofit.Builder()
                .baseUrl(YouTubeApplication.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

//    public void signIn(String username, String password) {
//        SignInRequest signInRequest = new SignInRequest(username, password);
//        Call<JsonObject> call = webServiceAPI.signIn(signInRequest);
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    JsonObject jsonObject = response.body();
//                    JsonObject userJson = jsonObject.getAsJsonObject("user");
//                    Gson gson = new Gson();
//                    User user = gson.fromJson(userJson, User.class);
//                    userDetailsResult.postValue(user); // Update userDetailsResult with the user object
//                    createToken(user.getUsername()); // Create token after successful sign-in
//                    usersRepository.handleSignInResponse(user);
//                } else {
//                    signInResult.postValue(false); // Handle sign-in failure
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                signInResult.postValue(false); // Handle failure
//            }
//        });
//    }


    public void signIn(String username, String password) {
        SignInRequest signInRequest = new SignInRequest(username, password);
        Call<JsonObject> call = webServiceAPI.signIn(signInRequest);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JsonObject userJson = jsonObject.getAsJsonObject("user");
                    Gson gson = new Gson();

                    User user = gson.fromJson(userJson, User.class);
                    // Extract the "id" directly from the JSON object
                    if (userJson.has("id")) {
                        String userId = userJson.get("id").getAsString();  // Extract the "id" field
                        user.set_id(userId);  // Set the id in the User object
                    }

                    userDetailsResult.postValue(user); // Update userDetailsResult with the user object
                    createToken(user.getUsername()); // Create token after successful sign-in
                    usersRepository.handleSignInResponse(user);
                } else {
                    signInResult.postValue(false); // Handle sign-in failure
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                signInResult.postValue(false); // Handle failure
            }
        });
    }


    public void createToken(String username) {
        JsonObject userJson = new JsonObject();
        userJson.addProperty("username", username);
        Call<JsonObject> call = webServiceAPI.createToken(userJson);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject tokenResponse = response.body();
                    String token = tokenResponse.get("token").getAsString();
                    GlobalToken.token = token;
                    signInResult.postValue(true);
                } else {
                    GlobalToken.token = null;
                    signInResult.postValue(false); // Handle token creation failure
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                GlobalToken.token = null;
                signInResult.postValue(false); // Handle failure
            }
        });
    }

//    public void getUserFromToken(String token) {
//        String authHeader = "Bearer " + token;
//        Log.d("API_REQUEST", "Sending token: " + authHeader);
//        Call<JsonObject> call = webServiceAPI.getUserFromToken(authHeader);
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                Log.d("API_RESPONSE", "Received response from server");
//                if (response.isSuccessful() && response.body() != null) {
//                    JsonObject jsonObject = response.body();
//                    Log.d("API_RESPONSE_BODY", "Response body: " + jsonObject.toString());
//                    Gson gson = new Gson();
//                    User user = gson.fromJson(jsonObject, User.class);
//                    userDetailsResult.postValue(user);
//                } else {
//                    try {
//                        Log.e("API_ERROR", "Error response: " + response.errorBody().string());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    userDetailsResult.postValue(null);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.e("API_ERROR", "Request failed", t);
//                userDetailsResult.postValue(null);
//            }
//        });
//    }


    public void getUserFromToken(String token) {
        String authHeader = "Bearer " + token;
        Log.d("API_REQUEST", "Sending token: " + authHeader);
        Call<JsonObject> call = webServiceAPI.getUserFromToken(authHeader);
        call.enqueue(new Callback<JsonObject>() {
            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                Log.d("API_RESPONSE", "Received response from server");
//                if (response.isSuccessful() && response.body() != null) {
//                    JsonObject jsonObject = response.body();
//                    Log.d("API_RESPONSE_BODY", "Response body: " + jsonObject.toString());
//                    Gson gson = new Gson();
//                    User user = gson.fromJson(jsonObject, User.class);
//                    userDetailsResult.postValue(user);
//                } else {
//                    try {
//                        Log.e("API_ERROR", "Error response: " + response.errorBody().string());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    userDetailsResult.postValue(null);
//                }
//            }

            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("API_RESPONSE", "Received response from server");
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonObject = response.body();
                    Log.d("API_RESPONSE_BODY", "Response body: " + jsonObject.toString());

                    Gson gson = new Gson();
                    // Extract the _id field from the JsonObject
           //         String userId = jsonObject.get("_id").getAsString();

                    // Convert the rest of the response to a User object
                    User user = gson.fromJson(jsonObject, User.class);

                    // Set the extracted _id to the User object (assuming the User class has a setId method)
         //           user.set_id(userId);

                    // Post the user object with the _id set
                    userDetailsResult.postValue(user);
                } else {
                    try {
                        Log.e("API_ERROR", "Error response: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    userDetailsResult.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("API_ERROR", "Request failed", t);
                userDetailsResult.postValue(null);
            }
        });
    }


    public void signUp(SignUpRequest signUpRequest) {
        Call<JsonObject> call = webServiceAPI.signUp(signUpRequest);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Extract user information from the response
                    JsonObject jsonObject = response.body();
                    JsonObject userJson = jsonObject.getAsJsonObject("user");
                    Gson gson = new Gson();
                    User user = gson.fromJson(userJson, User.class);

                    // Save the user to Room
                    saveUserLocally(user);

                    signUpResult.postValue(true);
                } else {
                    signUpResult.postValue(false);
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                signUpResult.postValue(false);
            }
        });
    }

    private void saveUserLocally(User user) {
        new Thread(() -> usersDao.insert(user)).start();
    }


    public void updateUser(String id, User user, Consumer<String> callback) {
        Call<JsonObject> call = webServiceAPI.updateUser(id, user);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    createTokenWithCallback(user.getUsername(), success -> {
                        if (success) {
                            callback.accept(String.valueOf(response.body()));
                        } else {
                            callback.accept(null);
                        }
                    });
                } else {
                    callback.accept(null);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.accept(null);
            }
        });
    }

    private void createTokenWithCallback(String username, Consumer<Boolean> tokenCallback) {
        JsonObject userJson = new JsonObject();
        userJson.addProperty("username", username);
        Call<JsonObject> call = webServiceAPI.createToken(userJson);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject tokenResponse = response.body();
                    String token = tokenResponse.get("token").getAsString();
                    GlobalToken.token = token;
                    tokenCallback.accept(true);  // Call success
                } else {
                    GlobalToken.token = null;
                    tokenCallback.accept(false); // Call failure
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                GlobalToken.token = null;
                tokenCallback.accept(false); // Call failure
            }
        });
    }

    public void deleteUser(String id, Consumer<String> callback) {
        Call<Void> call = webServiceAPI.deleteUser(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.accept(id);
                } else {
                    callback.accept(null);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.accept(null);
            }
        });
    }


}
