package com.example.eirini.hiddenstories;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.eirini.hiddenstories.Utils.DataOfStory;
import com.example.eirini.hiddenstories.Utils.Listener;
import com.example.eirini.hiddenstories.Utils.StoryInfo;
import com.example.eirini.hiddenstories.Utils.UserData;
import com.example.eirini.hiddenstories.Utils.ViewPagerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class StoryActivity extends AppCompatActivity implements Listener{
    public static final String TAG = StoryActivity.class.getSimpleName();

    StoryInfo current_story;
    //Emotions and Characters collected by user
    List<UserData> userDataList;
    String user_choice = "Who is speaking?";

    private boolean isDialogDisplayed = false;
    protected NfcAdapter mNfcAdapter;

    NFCreadingFragment mNfcReadFragment;
    UserCollectionFragment mUserCollectionFragment;

    //For Sliding between screens
    ViewPager viewPager;
    MenuItem prevMenuItem;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_activity);
        initNFC();
        //Get the chapter list from other activity
        Intent intent = getIntent();
        current_story = (StoryInfo) intent.getSerializableExtra("stories list");
        userDataList = (List<UserData>) intent.getSerializableExtra("User data list");
        if(current_story == null){
            //Throw user to an error page(?)
            Log.d(TAG,"In here");
            Intent err_intent = new Intent(StoryActivity.this, ErrorActivity.class);
            startActivity(err_intent);
            finish();
        }

        if(userDataList==null){
            userDataList = new ArrayList<>();
        }
        Log.d(TAG, "Read user data list"+userDataList);
        //Init view pager
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        //Initialise bottom navigation view
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_my_profile:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.action_read:
                                viewPager.setCurrentItem(0);
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
        mNfcReadFragment = NFCreadingFragment.newInstance(userDataList, current_story);
        mUserCollectionFragment = UserCollectionFragment.newInstance(current_story.getS_name());
        adapter.addFragment(mNfcReadFragment);
        adapter.addFragment(mUserCollectionFragment);
        viewPager.setAdapter(adapter);
    }

    private void initNFC(){
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    public void dataHandling(DataOfStory data){
        //This data list is the emotions of user
        current_story = data.getuStory();
        userDataList = data.getUserDataList();
        for(int i = 0; i<userDataList.size();i++)
            Log.d(TAG, "Data of user is"+userDataList.get(i).isFavorite());
        //Return to MainActivity emotions collected, and current story
        Intent returnIntent = new Intent();
        returnIntent.putExtra("user data list", (Serializable) userDataList);
        returnIntent.putExtra("current story", current_story);
        //Return data
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @Override
    public void onDialogDisplayed() {
        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {
        isDialogDisplayed = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Story Activity on resume");
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected,tagDetected,ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if(mNfcAdapter!= null)
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Story Activity on pause");
        if(mNfcAdapter!= null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Log.d(TAG, "onNewIntent: "+intent.getAction());
        Log.d(TAG, "onNewIntent tag is "+tag);
        if(tag != null) {
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            Ndef ndef = Ndef.get(tag);

            if (isDialogDisplayed) {
                //mNfcReadFragment =(NFCreadingFragment) getSupportFragmentManager().findFragmentByTag("nfc");
                if(mNfcReadFragment!=null && mNfcReadFragment.isVisible()){
                    mNfcReadFragment.onNfcDetected(ndef);
                }
            }
        }
        else{
            Toast.makeText(this, getString(R.string.message_tag_undetected), Toast.LENGTH_SHORT).show();
        }
    }
}
