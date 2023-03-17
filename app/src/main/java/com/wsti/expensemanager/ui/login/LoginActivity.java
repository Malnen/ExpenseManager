package com.wsti.expensemanager.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.wsti.expensemanager.MainActivity;
import com.wsti.expensemanager.R;
import com.wsti.expensemanager.data.Result;
import com.wsti.expensemanager.data.model.User;
import com.wsti.expensemanager.databinding.ActivityLoginBinding;
import com.wsti.expensemanager.ui.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private ProgressBar loadingProgressBar;
    private LoginViewModelFactory factory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setLoginViewListeners();
        TextWatcher afterTextChangedListener = setUsernameListeners();
        setPasswordListeners(afterTextChangedListener);
        setLoginButtonListener();
        setRegisterButtonListener();
        registerButton.setEnabled(true);
        tryToLoginOnRememberedUser();
    }

    private void init() {
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        usernameEditText = binding.username;
        passwordEditText = binding.password;
        loginButton = binding.login;
        registerButton = binding.signUp;
        loadingProgressBar = binding.loading;

        ConstraintLayout root = binding.getRoot();
        setContentView(root);
    }

    private void setLoginViewListeners() {
        Context context = getApplicationContext();
        factory = new LoginViewModelFactory(context);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, factory);
        loginViewModel = viewModelProvider.get(LoginViewModel.class);
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }

                loginButton.setEnabled(loginFormState.isDataValid());
                Integer usernameError = loginFormState.getUsernameError();
                if (usernameError != null) {
                    String error = getString(usernameError);
                    usernameEditText.setError(error);
                }

                Integer passwordError = loginFormState.getPasswordError();
                if (passwordError != null) {
                    String error = getString(passwordError);
                    passwordEditText.setError(error);
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }

                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }

                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        Context applicationContext = getApplicationContext();
        Toast toast = Toast.makeText(applicationContext, welcome, Toast.LENGTH_LONG);
        toast.show();
        startMainActivity();
    }

    private void showLoginFailed(Integer errorString) {
        Context applicationContext = getApplicationContext();
        Toast toast = Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT);
        toast.show();
    }

    private TextWatcher setUsernameListeners() {
        Editable usernameEditTextText = usernameEditText.getText();
        Editable passwordEditTextText = passwordEditText.getText();
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String username = usernameEditTextText.toString();
                String password = passwordEditTextText.toString();
                loginViewModel.loginDataChanged(username, password);
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        return afterTextChangedListener;
    }

    private void setPasswordListeners(TextWatcher afterTextChangedListener) {
        Editable usernameEditTextText = usernameEditText.getText();
        Editable passwordEditTextText = passwordEditText.getText();
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String username = usernameEditTextText.toString();
                    String password = passwordEditTextText.toString();
                    loginViewModel.login(username, password);
                }

                return false;
            }
        });
    }

    private void setLoginButtonListener() {
        Editable usernameEditTextText = usernameEditText.getText();
        Editable passwordEditTextText = passwordEditText.getText();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                String username = usernameEditTextText.toString();
                String password = passwordEditTextText.toString();
                loginViewModel.login(username, password);
            }
        });
    }

    private void setRegisterButtonListener() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context applicationContext = getApplicationContext();
                Intent intent = new Intent(applicationContext, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void tryToLoginOnRememberedUser() {
        Result<User> userResult = factory.tryToLoginOnRememberedUser();
        if (userResult instanceof Result.Success) {
            showWelcomBackToast((Result.Success<User>) userResult);
            startMainActivity();
        }
    }

    private void showWelcomBackToast(Result.Success<User> userResult) {
        User user = userResult.getData();
        String welcome = getString(R.string.welcome_back) + user.getLogin();
        Context applicationContext = getApplicationContext();
        Toast toast = Toast.makeText(applicationContext, welcome, Toast.LENGTH_LONG);
        toast.show();
    }

    private void startMainActivity() {
        Context applicationContext = getApplicationContext();
        Intent intent = new Intent(applicationContext, MainActivity.class);
        startActivity(intent);
        finish();
    }

}