package com.wsti.expensemanager.ui.register;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.wsti.expensemanager.data.UserDataSource;
import com.wsti.expensemanager.data.UserRepository;

public class RegisterViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public RegisterViewModelFactory(Context context) {
        this.context = context;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            return (T) new RegisterViewModel(context, UserRepository.getInstance(new UserDataSource(context)));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}