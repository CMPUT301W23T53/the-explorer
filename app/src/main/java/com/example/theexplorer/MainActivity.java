package com.example.theexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.theexplorer.databinding.ActivityMainBinding;
import com.example.theexplorer.ui.home.HomeFragment;
import com.example.theexplorer.ui.map.MapFragment;
import com.example.theexplorer.ui.profile.ProfileFragment;
import com.example.theexplorer.ui.scan.ZXingScannerScan;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity{

    private ActivityMainBinding binding;
    private FragmentManager manager;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_scan, R.id.navigation_map)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment navHost = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
                int currentFragment = navHost.getChildFragmentManager().getFragments().get(0).getId();
                if(item.getItemId() == R.id.navigation_home) {
                    navHost.getChildFragmentManager().beginTransaction().replace(currentFragment,HomeFragment.class,null).commit();
                    return true;
                }
                else if(item.getItemId() == R.id.navigation_scan) {
                    Intent toGo = new Intent(MainActivity.this, ZXingScannerScan.class);
                    startActivity(toGo);
                    return true;
                }
                else if(item.getItemId() == R.id.navigation_map) {
                    navHost.getChildFragmentManager().beginTransaction().replace(currentFragment, MapFragment.class,null).commit();
                    return true;
                }
                return false;
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.toolbar_profile){
//            Toast.makeText(this, "This button works", Toast.LENGTH_SHORT).show();
//
//            int toReplaceID = navController.getCurrentDestination().getId();
//            Log.d("High",String.valueOf(toReplaceID));
//            Fragment toDisplay = new ProfileFragment();
//            FragmentTransaction td = this.manager.beginTransaction();
//            td.replace(toReplaceID,toDisplay);
//            td.commit();
//            return true;
//        }
//        else if (item.getItemId() == R.id.toolbar_search){
//            Toast.makeText(this,"This button works", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

}