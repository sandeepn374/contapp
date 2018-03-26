package com.voiceitbase.audiorecorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class pricingpolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changedprcingpolicy);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.earningsnav) {
                    Intent intent = new Intent(getApplicationContext(), designchangeearnings.class);
                    startActivity(intent);


                } else if (id == R.id.uploadsnav) {

                    Intent intent = new Intent(getApplicationContext(), FileListNewMulti.class);
                    startActivity(intent);



                }
                else if(id==R.id.pricingpolicynav   ){
                    Intent intent = new Intent(getApplicationContext(), pricingpolicy.class);
                    startActivity(intent);

                }
                else if(id==R.id.homenav   ){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                }



                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        navigationView.bringToFront();
        navigationView.bringToFront();
        View headerLayout = navigationView.getHeaderView(0);
        TextView nameswipe=headerLayout.findViewById(R.id.nameswipe);
        TextView earningsswipe=headerLayout.findViewById(R.id.earningsswipe);
        nameswipe.setText(RegisterActivity.getDefaults("user",getApplicationContext()));
        String earningString=RegisterActivity.getDefaults("totalincome",getApplicationContext());
        if (earningString!=null)
            earningsswipe.setText("Rs "+earningString);




    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(getApplicationContext(),MainActivity.class);

        startActivity(setIntent);
    }


}
