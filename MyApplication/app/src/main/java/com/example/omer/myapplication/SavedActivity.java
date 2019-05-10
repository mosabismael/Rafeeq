package com.example.omer.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

public class SavedActivity extends AppCompatActivity implements
        FragmentHome.OnFragmentInteractionListener {
    private FrameLayout mMainFrame;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar appBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        final FragmentHome fHome = new FragmentHome();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.saved_main_frame, fHome);
        fragmentTransaction.commit();

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_saved);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawerOpen, R.string.drowerClose);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        appBar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Saved");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_name);

        View view = findViewById(R.id.activity_saved);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        }


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.drawer_logot: {
                        SessionManager sessionManager = new SessionManager(getApplicationContext());
                        sessionManager.set_login(false);
                        startActivity(new Intent(getBaseContext(), LoginActivity.class));
                        finish();
                        break;
                    }

                    case R.id.drawer_about: {

                    }

                    case R.id.drawer_home: {
                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                        finish();
                    }
                }
                //close navigation drawer
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_saved);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
