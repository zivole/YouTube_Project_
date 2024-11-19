package com.example.youtube_android.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.youtube_android.api.UsersAPI;
import com.example.youtube_android.daoes.UsersDao;
import com.example.youtube_android.entities.User;
import com.example.youtube_android.utils.GlobalToken;
import com.example.youtube_android.utils.SignUpRequest;

import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersRepository {
    private UsersAPI usersAPI;
    private UsersDao usersDao;
    private MutableLiveData<List<User>> userListData;
    private MutableLiveData<Boolean> signUpResult;
    private MutableLiveData<Boolean> signInResult;
    private MutableLiveData<User> userDetailsResult;
    private MutableLiveData<Boolean> validatePasswordResult;
    private MutableLiveData<Boolean> checkUsernameExistsResult;
    private MutableLiveData<User> getUserByUsernameResult;

    public UsersRepository(UsersDao usersDao) {
        this.usersDao = usersDao;
        userListData = new MutableLiveData<>();
        signUpResult = new MutableLiveData<>();
        signInResult = new MutableLiveData<>();
        userDetailsResult = new MutableLiveData<>();
        validatePasswordResult = new MutableLiveData<>();
        checkUsernameExistsResult = new MutableLiveData<>();
        getUserByUsernameResult = new MutableLiveData<>();
        usersAPI = new UsersAPI(userListData, usersDao, signUpResult, signInResult, userDetailsResult, validatePasswordResult, checkUsernameExistsResult, getUserByUsernameResult, this);
    }

    public void signUp(SignUpRequest signUpRequest) {
        usersAPI.signUp(signUpRequest);
    }

    public void signIn(String username, String password) {
        usersAPI.signIn(username, password);
    }

    public void handleSignInResponse(User user) {
        new Thread(() -> {
            User existingUser = usersDao.getUserByUserName(user.getUsername());
            if (existingUser == null) {
                usersDao.insert(user);
            }
        }).start();
    }

    public void getUserFromToken(String token) {
        usersAPI.getUserFromToken(token);
    }

    public MutableLiveData<User> getUserDetailsResult() {
        return userDetailsResult;
    }

    public MutableLiveData<List<User>> getUserListData() {
        return userListData;
    }

    public MutableLiveData<Boolean> getSignUpResult() {
        return signUpResult;
    }

    public MutableLiveData<Boolean> getSignInResult() {
        return signInResult;
    }

    public User getUserLocally(String username) {
        return usersDao.getUserByUserName(username);
    }
    public void updateUser(User user, Consumer<String> callback) {
        usersAPI.updateUser(user.get_id(), user, callback);
    }

    public void deleteUser(String id, Consumer<String> callback) {
        usersAPI.deleteUser(id, callback);
    }

}
