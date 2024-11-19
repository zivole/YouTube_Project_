package com.example.youtube_android.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.youtube_android.R;
import com.example.youtube_android.adapters.ShortsAdapter;
import com.example.youtube_android.entities.ShortsData;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class ShortsActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private List<ShortsData> shortsDataList;
    private ShortsAdapter shortsAdapter;
    private BottomNavigationView bottomNavigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shorts); // Ensure this is the correct layout file

        viewPager2 = findViewById(R.id.viewPager2); // Ensure the ID matches with your XML
        shortsDataList = new ArrayList<>();

        // Populate the shortsDataList with ShortsData objects
        populateShortsDataList();

        shortsAdapter = new ShortsAdapter(shortsDataList);
        viewPager2.setAdapter(shortsAdapter);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.bottom_home) {
                    Intent intent = new Intent(ShortsActivity.this, HomeActivity.class);
//                    intent.putExtra("userDetails", UserDatabase.getInstance().getCurrentUser());
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.bottom_shorts) {
                    Intent intent = new Intent(ShortsActivity.this, ShortsActivity.class);
//                    intent.putExtra("userDetails", UserDatabase.getInstance().getCurrentUser());
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

    }

    private void populateShortsDataList() {
        shortsDataList.add(new ShortsData("android.resource://" + getPackageName() + "/raw/resort_in_thailand_part_2", "@android_knowledge", "Resort in Thailand part 2", R.drawable.channel1));
//        shortsDataList.add(new ShortsData("android.resource://" + getPackageName() + "/raw/sea_turtle", "@android_knowledge", "UV is a Directed Graph", R.drawable.channel1));
        shortsDataList.add(new ShortsData("android.resource://" + getPackageName() + "/raw/resort_in_thailand_part_1", "@android_knowledge", "Resort in Thailand part 1", R.drawable.channel1));
//        shortsDataList.add(new ShortsData("android.resource://" + getPackageName() + "/raw/uv_piano", "@android_knowledge", "UV Plays Piano", R.drawable.channel1));
        shortsDataList.add(new ShortsData("android.resource://" + getPackageName() + "/raw/sea_turtles_diving", "@android_knowledge", "Sea Turtle in Koh Tao", R.drawable.channel1));
//        shortsDataList.add(new ShortsData("android.resource://" + getPackageName() + "/raw/pass_pass", "@android_knowledge", "Pass Pass", R.drawable.channel1));
//        shortsDataList.add(new ShortsData("android.resource://" + getPackageName() + "/raw/uv_piano_2", "@android_knowledge", "UV Plays Piano #2", R.drawable.channel1));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
    }
}