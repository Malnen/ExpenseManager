package com.wsti.expensemanager.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.wsti.expensemanager.data.UserRepository;
import com.wsti.expensemanager.data.Result;
import com.wsti.expensemanager.data.model.User;
import com.wsti.expensemanager.R;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final UserRepository userRepository;

    LoginViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        Result<User> result = userRepository.login(username, password);

        if (result instanceof Result.Success) {
            onLoginSuccess((Result.Success<User>) result);
        } else {
            LoginResult errorResult = new LoginResult(R.string.login_failed);
            loginResult.setValue(errorResult);
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private void onLoginSuccess(Result.Success<User> result) {
        User data = result.getData();
        String displayName = data.getLogin();
        LoggedInUserView loggedInUserView = new LoggedInUserView(displayName);
        LoginResult loginResult = new LoginResult(loggedInUserView);
        this.loginResult.setValue(loginResult);
    }

    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }

        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        }

        String trimmedUsername = username.trim();
        return !trimmedUsername.isEmpty();
    }

    private boolean isPasswordValid(String password) {
        if (password == null) {
            return false;
        }

        String trimmedPassword = password.trim();
        return trimmedPassword.length() >= 4;
    }
}