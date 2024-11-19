package com.example.youtube_android.viewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.youtube_android.daoes.UsersDao;
import com.example.youtube_android.databases.UsersDB;
import com.example.youtube_android.entities.User;
import com.example.youtube_android.repositories.UsersRepository;
import com.example.youtube_android.utils.SignUpRequest;

import java.util.List;
import java.util.function.Consumer;

public class UsersViewModel extends AndroidViewModel {
    private UsersRepository usersRepository;
    private MutableLiveData<List<User>> userListData;
    private MutableLiveData<Boolean> signUpResult;
    private MutableLiveData<Boolean> signInResult;
    private MutableLiveData<User> userDetailsResult;

    public UsersViewModel(Application application) {
        super(application);
        UsersDB usersDB = UsersDB.getDatabase(application);
        UsersDao usersDao = usersDB.usersDao();
        usersRepository = new UsersRepository(usersDao);
        userListData = usersRepository.getUserListData();
        signUpResult = usersRepository.getSignUpResult();
        signInResult = usersRepository.getSignInResult(); // Initialize from repository
        userDetailsResult = usersRepository.getUserDetailsResult();
    }

    public void signUp(SignUpRequest signUpRequest) {
        usersRepository.signUp(signUpRequest);
    }


    public void signIn(String username, String password) {
        usersRepository.signIn(username, password);
    }

    public void getUserFromToken(String token) {
        usersRepository.getUserFromToken(token);
    }

    public MutableLiveData<User> getUserDetailsResult() {
        return userDetailsResult;
    }

    public MutableLiveData<Boolean> getSignUpResult() {
        return signUpResult;
    }

    public MutableLiveData<Boolean> getSignInResult() {
        return signInResult;
    }
    public void updateUser(User user, Consumer<String> callback) {
        usersRepository.updateUser(user, callback);
    }

    public void deleteUser(String id, Consumer<String> callback) {
        usersRepository.deleteUser(id, callback);
    }
}
