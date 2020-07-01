package ca.nait.dmit2504.soundboardapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    // setSupportActionBar only supports appcompat version of Toolbar
    private androidx.appcompat.widget.Toolbar mToolbar;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mDrawerNavigationView;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch(item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;
                Class fragmentClass;
                switch(menuItem.getItemId()) {
                    // TODO: Create fragments
                    case R.id.nav_home_fragment:
//                        fragmentClass =
                        break;
                    case R.id.nav_files_fragment:
//                        fragmentClass =
                        break;
                    case R.id.nav_settings_fragment:
//                        fragmentClass =
                        break;
                    default:
//                        fragmentClass =

                }
                try {
//                    fragment = fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Insert fragment by replacing existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_frameLayout, fragment).commit();

                // Set selected menu item "checked"
                menuItem.setChecked(true);
                // Set ActionBar header title
                setTitle(menuItem.getTitle());
                // Close the nav drawer
                mDrawerLayout.closeDrawers();

                return true;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views
        mToolbar  = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerNavigationView = findViewById(R.id.drawer_navigationView);

        // Replace Toolbar with ActionBar
        setSupportActionBar(mToolbar);
        // Change ActionBar icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActionBarDrawerToggle = setupDrawerToggle();
        // Setup toggle animation for hamburger menu icon
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        mActionBarDrawerToggle.syncState();

        // DrawerLayout listener
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);

        setupDrawerContent(mDrawerNavigationView);
    }

    // onPostCreate = activity start-up completed after onStart()
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync toggle state after onRestoreInstanceState
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass config changes to drawer toggle
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
}