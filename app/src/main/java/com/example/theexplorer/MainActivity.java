package com.example.theexplorer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.theexplorer.ui.home.HomeFragment;
import com.example.theexplorer.ui.map.MapFragment;
import com.example.theexplorer.ui.profile.ProfileFragment;
import com.example.theexplorer.ui.scan.ZXingScannerScan;
import com.example.theexplorer.services.UserService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.theexplorer.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    final private UserService userService = new UserService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_scan, R.id.navigation_map)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                switchFragment(new HomeFragment(),"Home");
                return true;
            }
            else if (item.getItemId() == R.id.navigation_scan)
            {
                Intent toGo = new Intent(MainActivity.this, ZXingScannerScan.class);
                startActivity(toGo);
                return true;
            }
            else if (item.getItemId() == R.id.navigation_map) {
                switchFragment(new MapFragment(),"Map");
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toolbar_profile) {
            switchFragment(new ProfileFragment(),"Profile");
            return true;
        }
        else if (item.getItemId() == R.id.toolbar_search) {
            Toast.makeText(this, "This button works", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void switchFragment(Fragment fragment, String title) {
        Fragment navHost = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        int currentFragment = navHost.getChildFragmentManager().getFragments().get(0).getId();
        navHost
                .getChildFragmentManager()
                .beginTransaction()
                .replace(currentFragment,fragment)
                .commit();
        getSupportActionBar().setTitle(title);
    }

}
