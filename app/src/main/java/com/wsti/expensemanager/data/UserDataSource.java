package com.wsti.expensemanager.data;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wsti.expensemanager.data.model.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;

public class UserDataSource {
    private static final String MY_PREFS = "my_prefs";
    private static final String USERS = "users";
    private static final String REMEMBERED_USER = "rememberedUser";

    private final Context context;

    public UserDataSource(Context context) {
        this.context = context;
    }

    public Result<User> login(String username, String password) {
        try {
            Map<User, String> users = getAllUsers();
            Set<Map.Entry<User, String>> entries = users.entrySet();
            for (Map.Entry<User, String> entry : entries) {
                User user = entry.getKey();
                String userPassword = entry
                        .getValue()
                        .toLowerCase();
                String displayName = user
                        .getLogin()
                        .toLowerCase();
                if (Objects.equals(displayName, username) && Objects.equals(password, userPassword)) {
                    rememberUser(user);
                    return new Result.Success<>(user);
                }
            }

            throw new RuntimeException("User not found");
        } catch (Exception e) {
            String message = e.getMessage();
            return new Result.Error(new IOException(message, e));
        }
    }

    public Result<User> tryToLoginOnRememberedUser() {
        User rememberedUser = getRememberedUser();
        if (rememberedUser != null) {
            return new Result.Success<>(rememberedUser);
        }

        return null;
    }

    public Result<User> register(String username, String password, String email) {
        try {
            Map<User, String> users = getAllUsers();
            users.forEach((user, userPassword) -> {
                String userEmail = user.getEmail();
                String userLogin = user.getLogin();
                if (Objects.equals(userEmail, email)) {
                    throw new RuntimeException("Email already used.");
                } else if (Objects.equals(userLogin, username)) {
                    throw new RuntimeException("Username already used.");
                }
            });

            String userId = UUID.randomUUID().toString();
            User newUser = new User(userId, username, email);
            users.put(newUser, password);
            saveUser(users);

            return new Result.Success<>(newUser);
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }

    public void logout() {
        rememberUser(null);
    }

    private Map<User, String> getAllUsers() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(USERS, null);
        if (json != null) {
            Type type = new TypeToken<Map<User, String>>() {
            }.getType();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(type, new UserMapAdapter());
            Gson gson = gsonBuilder.create();

            return gson.fromJson(json, type);
        }

        return new HashMap<>();
    }

    private void saveUser(Map<User, String> users) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Type type = new TypeToken<Map<User, String>>() {
        }.getType();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(type, new UserMapAdapter());
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(users, type);
        editor.putString(USERS, json);
        editor.apply();
    }

    private void rememberUser(User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString(REMEMBERED_USER, json);
        editor.apply();
    }

    private User getRememberedUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(REMEMBERED_USER, null);
        if (json != null) {
            Type type = new TypeToken<User>() {
            }.getType();
            Gson gson = new Gson();

            return gson.fromJson(json, type);
        }

        return null;
    }
}