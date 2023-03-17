package com.wsti.expensemanager.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.content.Context;
import android.util.Patterns;

import com.wsti.expensemanager.R;
import com.wsti.expensemanager.data.Result;
import com.wsti.expensemanager.data.UserRepository;
import com.wsti.expensemanager.data.model.User;

public class RegisterViewModel extends ViewModel {

    private final Context context;

    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();
    private UserRepository userRepository;

    RegisterViewModel(Context context, UserRepository userRepository) {
        this.context = context;
        this.userRepository = userRepository;
    }

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void register(String username, String password, String email) {
        Result<User> result = userRepository.register(username, password, email);

        if (result instanceof Result.Success) {
            User data = ((Result.Success<User>) result).getData();
            String login = data.getLogin();
            RegisteredUserView success = new RegisteredUserView(login);
            registerResult.setValue(new RegisterResult(success));
        } else if (result instanceof Result.Error) {
            Exception error = ((Result.Error) result).getError();
            String message = error.getMessage();
            registerResult.setValue(new RegisterResult(message));
        } else {
            String message = context.getString(R.string.register_failed);
            registerResult.setValue(new RegisterResult(message));
        }
    }

    public void loginDataChanged(String username, String password, String email) {
        if (!isUserNameValid(username)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_username, null, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.invalid_password, null));
        } else if (!isEmailValid(email)) {
            registerFormState.setValue(new RegisterFormState(null, null, R.string.invalid_email));
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }

        return !username.trim().isEmpty();
    }

    private boolean isPasswordValid(String password) {
        if (password == null) {
            return false;
        }

        String trim = password.trim();
        return trim.length() >= 4;
    }

    private boolean isEmailValid(String username) {
        if (username == null) {
            return false;
        } else if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        }

        String trim = username.trim();
        return !trim.isEmpty();
    }
}