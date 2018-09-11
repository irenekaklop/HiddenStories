package com.example.eirini.hiddenstories;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.eirini.hiddenstories.Utils.CheckedBoxListener;
import com.example.eirini.hiddenstories.Utils.StoryInfo;
import com.example.eirini.hiddenstories.Utils.UserProfile;
import com.example.eirini.hiddenstories.Utils.ViewPagerAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    UserProfile userProfile = new UserProfile();
    ArrayList<StoryInfo> list_of_stories;
    ArrayList<String> userEmotions = new ArrayList<>();

    //For Sliding between screens
    ViewPager viewPager;
    MenuItem prevMenuItem;
    BottomNavigationView bottomNavigationView;

    BeaconDetectionFragment mBeaconDetectionFragment;
    UserTrailFragment mUserTrailFragment;
    UserProfileFragment mUserProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get Data
        Intent intent = getIntent();
        list_of_stories = (ArrayList<StoryInfo>) intent.getSerializableExtra("storiesList");

        //Init view pager
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_detect_beacons:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.action_my_trail:
                                viewPager.setCurrentItem(2);
                                break;
                            case R.id.action_my_profile:
                                viewPager.setCurrentItem(1);
                                break;
                        }
                        return true;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mBeaconDetectionFragment = BeaconDetectionFragment.newInstance(list_of_stories);
        mUserProfileFragment = UserProfileFragment.newInstance(userProfile);
        mUserTrailFragment = UserTrailFragment.newInstance();
        adapter.addFragment(mBeaconDetectionFragment);
        adapter.addFragment(mUserProfileFragment);
        adapter.addFragment((mUserTrailFragment));
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("data handle", "on main activity result"+resultCode+" "+requestCode);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                userEmotions = data.getStringArrayListExtra("emotions");
                Log.d("data handle", "onMain data are : "+userEmotions);
            }
        }
    }

}
