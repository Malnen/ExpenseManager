package com.wsti.expensemanager.ui.register;

import android.app.Activity;
import android.content.Context;
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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.wsti.expensemanager.R;
import com.wsti.expensemanager.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private ProgressBar loadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setRegisterViewListeners();
        TextWatcher afterTextChangedListener = setUsernameListeners();
        setPasswordListeners(afterTextChangedListener);
        setEmailListeners(afterTextChangedListener);
        setRegisterButtonListener();
        ActionBar appBar = getSupportActionBar();
        appBar.setTitle("Register");
    }

    private void init() {
        ActivityRegisterBinding binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        usernameEditText = binding.username;
        emailEditText = binding.email;
        passwordEditText = binding.password;
        registerButton = binding.register;
        loadingProgressBar = binding.loading;

        ConstraintLayout root = binding.getRoot();
        setContentView(root);
    }

    private void setRegisterViewListeners() {
        Context context = getApplicationContext();
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, new RegisterViewModelFactory(context));
        registerViewModel = viewModelProvider.get(RegisterViewModel.class);
        registerViewModel.getRegisterFormState().observe(this, new Observer<RegisterFormState>() {
            @Override
            public void onChanged(RegisterFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }

                registerButton.setEnabled(loginFormState.isDataValid());
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

        registerViewModel.getRegisterResult().observe(this, new Observer<RegisterResult>() {
            @Override
            public void onChanged(RegisterResult registerResult) {
                if (registerResult == null) {
                    return;
                }

                loadingProgressBar.setVisibility(View.GONE);
                String error = registerResult.getError();
                RegisteredUserView success = registerResult.getSuccess();
                if (error != null) {
                    showRegisterFailed(error);
                } else if (success != null) {
                    updateUiWithUser(success);
                }

            }
        });
    }

    private void showRegisterFailed(String error) {
        Context applicationContext = getApplicationContext();
        Toast toast = Toast.makeText(applicationContext, error, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void updateUiWithUser(RegisteredUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        Context applicationContext = getApplicationContext();
        Toast toast = Toast.makeText(applicationContext, welcome, Toast.LENGTH_LONG);
        toast.show();
        setResult(Activity.RESULT_OK);
        finish();
    }

    private TextWatcher setUsernameListeners() {
        Editable usernameEditText = this.usernameEditText.getText();
        Editable passwordEditText = this.passwordEditText.getText();
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String username = usernameEditText.toString();
                String password = passwordEditText.toString();
                String email = emailEditText.toString();
                registerViewModel.loginDataChanged(username, password, email);
            }
        };

        this.usernameEditText.addTextChangedListener(afterTextChangedListener);
        return afterTextChangedListener;
    }

    private void setPasswordListeners(TextWatcher afterTextChangedListener) {
        Editable usernameEditText = this.usernameEditText.getText();
        Editable passwordEditText = this.passwordEditText.getText();
        Editable emailEditText = this.emailEditText.getText();
        this.passwordEditText.addTextChangedListener(afterTextChangedListener);
        this.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String username = usernameEditText.toString();
                    String password = passwordEditText.toString();
                    String email = emailEditText.toString();
                    registerViewModel.register(username, password, email);
                }

                return false;
            }
        });
    }

    private void setEmailListeners(TextWatcher afterTextChangedListener) {
        Editable usernameEditText = this.usernameEditText.getText();
        Editable passwordEditText = this.passwordEditText.getText();
        Editable emailEditText = this.emailEditText.getText();
        this.emailEditText.addTextChangedListener(afterTextChangedListener);
        this.emailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String username = usernameEditText.toString();
                    String password = passwordEditText.toString();
                    String email = emailEditText.toString();
                    registerViewModel.register(username, password, email);
                }

                return false;
            }
        });
    }

    private void setRegisterButtonListener() {
        Editable usernameEditText = this.usernameEditText.getText();
        Editable passwordEditText = this.passwordEditText.getText();
        Editable emailEditText = this.emailEditText.getText();
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                String username = usernameEditText.toString();
                String password = passwordEditText.toString();
                String email = emailEditText.toString();
                registerViewModel.register(username, password, email);
            }
        });
    }

}