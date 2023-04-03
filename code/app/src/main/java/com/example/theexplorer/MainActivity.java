package com.example.theexplorer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.theexplorer.ui.home.HomeFragment;
import com.example.theexplorer.ui.map.MapFragment;
import com.example.theexplorer.ui.profile.ProfileFragment;
import com.example.theexplorer.ui.scan.ZXingScannerScan;
import com.example.theexplorer.ui.search.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

/**
 * Represents the main activity of the application which controls navigation, toolbar and permission handling.
 * Extends AppCompatActivity to provide compatibility support for the app's action bar.
 */
public class MainActivity extends AppCompatActivity {

    // Holds a reference to the binding object for this activity.
    private ActivityMainBinding binding;

    /**
     * Called when the activity is starting.
     * Initializes the activity and inflates its layout.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        BottomNavigationView navView = findViewById(R.id.nav_view);
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

    /**
     * Initialize the contents of the Activity's standard options menu.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed; if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toolbar_profile) {
            switchFragment(new ProfileFragment(),"Profile");
            return true;
        }
        else if (item.getItemId() == R.id.toolbar_search) {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
    * Switches the current fragment in the navigation host with the given fragment and updates the action bar title
    * accordingly.
    *
    * @param fragment The new fragment to be displayed.
    * @param title    The title to be set for the action bar.
    */
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