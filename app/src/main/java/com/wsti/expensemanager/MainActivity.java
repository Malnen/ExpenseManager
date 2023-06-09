package com.wsti.expensemanager;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.wsti.expensemanager.data.ExpenseDataSource;
import com.wsti.expensemanager.data.ExpenseRepository;
import com.wsti.expensemanager.data.UserDataSource;
import com.wsti.expensemanager.data.UserRepository;
import com.wsti.expensemanager.data.model.User;
import com.wsti.expensemanager.databinding.ActivityMainBinding;
import com.wsti.expensemanager.ui.login.LoginActivity;

import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private UserRepository userRepository;
    private ExpenseRepository expenseRepository;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRepositories();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        DrawerLayout root = binding.getRoot();
        setContentView(root);
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_expenses, R.id.nav_statistics, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        setUser();
        setUserData();
        setLogoutAction();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    private void setRepositories() {
        Context context = getApplicationContext();
        userRepository = UserRepository.getInstance(new UserDataSource(context));
        expenseRepository = ExpenseRepository.getInstance(new ExpenseDataSource(context));
    }

    private void setUser() {
        user = userRepository.getUser();
    }

    private void setUserData() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        setImage(headerView);
        setDisplayName(headerView);
        setEmail(headerView);
    }

    private void setImage(View headerView) {
        ImageView imageView = headerView.findViewById(R.id.avatar_image);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("Blank-Avatar.png");
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
            inputStream.close();
        } catch (IOException ignored) {
        }
    }

    private void setDisplayName(View headerView) {
        TextView displayName = headerView.findViewById(R.id.display_name);
        String login = user.getLogin();
        displayName.setText(login);
    }

    private void setEmail(View headerView) {
        TextView displayName = headerView.findViewById(R.id.display_email);
        String email = user.getEmail();
        displayName.setText(email);
    }

    private void setLogoutAction() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem logoutMenuItem = menu.findItem(R.id.nav_logout);
        logoutMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                expenseRepository.forgetExpenses();
                userRepository.logout();
                Context applicationContext = getApplicationContext();
                Intent intent = new Intent(applicationContext, LoginActivity.class);
                startActivity(intent);
                finish();

                return true;
            }
        });
    }


}