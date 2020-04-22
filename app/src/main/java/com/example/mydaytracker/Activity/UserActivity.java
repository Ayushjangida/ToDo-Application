package com.example.mydaytracker.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mydaytracker.Fragment.AllTasksFragment;
import com.example.mydaytracker.Fragment.MyListFragment;
import com.example.mydaytracker.R;
import com.example.mydaytracker.util.UserApi;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserActivity extends AppCompatActivity {
    private static final String TAG = "UserActivity";
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userId;
    private AdView mAdView;

    @Override
    protected void onStart() {
        super.onStart();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();
        getSupportFragmentManager().beginTransaction().add(R.id.user_activity_fragment, new MyListFragment(userId, UserActivity.this)).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
//        firebaseAuth = FirebaseAuth.getInstance();
//        user = firebaseAuth.getCurrentUser();
        Log.d("UserApi", "onCreate: " + userId);
//        getSupportFragmentManager().beginTransaction().add(R.id.user_activity_fragment, new CalenderFragment(userId, UserActivity.this)).commit();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navItemSelected);

//
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())   {
            case R.id.user_sign_out :
                firebaseAuth.signOut();
                UserApi.getInstance().setUserId(null);
                UserApi.getInstance().setFirsName(null);
                UserApi.getInstance().setLastName(null);
                startActivity(new Intent(UserActivity.this, LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navItemSelected = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectFragment = null;

            switch (item.getItemId())   {
//                case R.id.navigation_home :
//                    selectFragment = new HomeFragment(user.getUid());
//                    break;

//                case R.id.navigation_todo :
//                    selectFragment = new TaskFragment(user.getUid(), UserActivity.this);
//                    break;

                case R.id.navigation_mynotes :
                    selectFragment = new MyListFragment(user.getUid(), UserActivity.this);
                    break;

                case R.id.navigation_calender :
                    selectFragment = new AllTasksFragment(user.getUid(), UserActivity.this);
                    break;
            }
            assert selectFragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.user_activity_fragment, selectFragment).commit();
            return true;
        }
    };

//    @Override
//    public void onClick(View view) {
//        switch (view.getId())   {
//            case R.id.navigation_home :
//                textView.setText("Home");
//                break;
//
//            case R.id.navigation_todo :
//                textView.setText("ToDo");
//                break;
//
//            case R.id.navigation_mynotes :
//                textView.setText("notes");
//                break;
//
//            case R.id.navigation_calender :
//                textView.setText("Calender");
//                break;
//        }
//    }
}
