package com.example.eirini.hiddenstories;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.estimote.internal_plugins_api.cloud.CloudCredentials;
import com.estimote.internal_plugins_api.cloud.proximity.ProximityAttachment;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.proximity.ProximityObserver;
import com.estimote.proximity_sdk.proximity.ProximityObserverBuilder;
import com.estimote.proximity_sdk.proximity.ProximityZone;
import com.example.eirini.hiddenstories.Utils.CheckedBoxListener;
import com.example.eirini.hiddenstories.Utils.StoryInfo;
import com.example.eirini.hiddenstories.Utils.TotalSharedDataViewModel;
import com.example.eirini.hiddenstories.Utils.UserData;
import com.example.eirini.hiddenstories.estimote.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class BeaconDetectionFragment extends Fragment{
    //For proximity Observer
    CloudCredentials cloudCredentials;
    ProximityObserver.Handler proximityObserverHandler;

    View fragment_view;
    TextView tv_title;
    TextView tv_subtitle;
    TextView tv_remaining;
    Button bt_more;
    Menu mMenu;

    //DATA
    ArrayList<StoryInfo> storyInfosArrayList = new ArrayList<>();
    StoryInfo current_story;
    List<UserData> current_storyUserDataList;
    HashMap<String, List<UserData>> total_userData;
    private static final String DESCRIBABLE_KEY = "Stories_List_key";

    //For data sharing
    private TotalSharedDataViewModel model;

    public static BeaconDetectionFragment newInstance(ArrayList<StoryInfo> StoriesList) {
        BeaconDetectionFragment fragment = new BeaconDetectionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DESCRIBABLE_KEY, (Serializable) StoriesList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        total_userData = new HashMap<>();
        //Enable option menu
        setHasOptionsMenu(true);
        //Set the requirements for beacons
        RequirementsWizardFactory
                .createEstimoteRequirementsWizard()
                .fulfillRequirements(getActivity(),
                        new Function0<Unit>() {
                            @Override
                            public Unit invoke() {
                                Log.d("app", "requirements fulfilled");
                                startProximityContentManager();
                                return null;
                            }
                        },
                        new Function1<List<? extends Requirement>, Unit>() {
                            @Override
                            public Unit invoke(List<? extends Requirement> requirements) {
                                Log.e("app", "requirements missing: " + requirements);
                                return null;
                            }
                        },
                        new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                Log.e("app", "requirements error: " + throwable);
                                return null;
                            }
                        });
    }

    // create an action bar button
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.mMenu = menu;
        inflater.inflate(R.menu.actionbar_check, menu);
        for(int i = 0; i<mMenu.size(); i++) {
            if(mMenu.getItem(i).getItemId()==R.id.mybutton){
                mMenu.getItem(i).setVisible(false);
                break;
            }
        }
        if(mMenu == null){
            Log.d("beacon", "mMenu is null");
        }
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mybutton) {
            // do something here
            showDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    void showDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        QuestionFragment newFragment = QuestionFragment.newInstance();
        newFragment.show(ft, "dialog");
    }

    private void startProximityContentManager() {
        //proximityContentManager = new ProximityContentManager(this, ((MyApplication) getApplication()).cloudCredentials, storyUtilsArrayList);
        cloudCredentials = ((MyApplication) getActivity().getApplication()).cloudCredentials;
        ProximityObserver proximityObserver = new ProximityObserverBuilder(getActivity().getApplicationContext(), cloudCredentials)
                .withOnErrorAction(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        Log.e("app", "proximity observer error: " + throwable);
                        return null;
                    }
                })
                .withBalancedPowerMode()
                .build();

        ProximityZone zone = proximityObserver.zoneBuilder()
                .forAttachmentKeyAndValue("madgik-fernweh-s-proximity-88y", "example-proximity-zone")
                .inCustomRange(1.5) // 1.5 meters range
                .withOnEnterAction(new Function1<ProximityAttachment, Unit>() {
                    @Override
                    public Unit invoke(ProximityAttachment attachment) {
                        String title = attachment.getPayload().get("madgik-fernweh-s-proximity-88y/title");
                        if (title == null) {
                            title = "no title";
                            tv_title.setText(title);
                        }
                        else {
                            String story_title = Utils.getStoryID(title);
                            //Find story from Data
                            for(int i = 0 ; i < storyInfosArrayList.size(); i++){
                                current_story = storyInfosArrayList.get(i);
                                if(current_story.getChapterArrayList(story_title)!=null){
                                    break;
                                }
                            }
                            tv_title.setTextColor(getResources().getColor(R.color.black));
                            tv_subtitle.setTextColor(getResources().getColor(R.color.black));
                            tv_subtitle.setVisibility(View.VISIBLE);
                            tv_remaining.setVisibility(View.VISIBLE);
                            if(current_story.isDone()){
                                title = "Hello again!";
                                String subtitle = "You know my story, but this museum is big and full of other stories";
                                tv_title.setText(title);
                                tv_subtitle.setText(subtitle);
                                tv_remaining.setText("Completed");
                                tv_remaining.setTextColor(getResources().getColor(R.color.green));
                                bt_more.setVisibility(View.GONE);
                                fragment_view.findViewById(R.id.fragm_layout).setBackgroundColor(Utils.getEstimoteColor(title));
                            }
                            else{
                                // Inform about remaining stories
                                int remaining = current_story.getRemaining_sub_stories();
                                String num_stories = Integer.toString(remaining)+" / 7";

                                String subtitle = Utils.getStorySubtitle(title);
                                if(current_story.isVisited()) {
                                    title = "Hello again!";
                                    subtitle = "You visited me";
                                }
                                bt_more.setVisibility(View.VISIBLE);

                                tv_subtitle.setText(subtitle);
                                tv_remaining.setText(num_stories);
                                tv_title.setText(story_title);

                                bt_more.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //Begin the story
                                        Intent intent = new Intent(getActivity(), StoryActivity.class);
                                        intent.putExtra("stories list", current_story);
                                        //Create userdatalist for current story
                                        current_storyUserDataList = ViewModelProviders.of(getActivity()).get(TotalSharedDataViewModel.class).getUserData_current_story(current_story.getS_name());
                                        Log.d("Beacon", "current story userData:"+current_storyUserDataList);
                                        intent.putExtra("User data list", (Serializable) current_storyUserDataList);
                                        startActivityForResult(intent, 1);
                                    }
                                });
                                fragment_view.findViewById(R.id.fragm_layout).setBackgroundColor(getResources().getColor(R.color.detectBeacon));
                            }
                        }
                        return null;
                    }
                })
                .withOnExitAction(new Function1<ProximityAttachment, Unit>() {
                    @Override
                    public Unit invoke(ProximityAttachment proximityContext) {
                        //If all stories and sub-stories are done
                        if(isFinished()){
                            //END
                        }
                        String title = "Keep walking, new stories are waiting for you";
                        String subtitle = " ";
                        tv_title.setText(title);
                        fragment_view.setBackgroundColor(getResources().getColor(R.color.white_trans));
                        tv_title.setTextColor(getResources().getColor(R.color.directions));
                        fragment_view.findViewById(R.id.fragm_layout).setBackgroundColor(getResources().getColor(R.color.undetectBeacon));
                        tv_remaining.setVisibility(View.GONE);
                        tv_subtitle.setVisibility(View.GONE);
                        bt_more.setVisibility(View.GONE);
                        //Inform total story list about this change
                        for(int i=0; i < storyInfosArrayList.size(); i++){
                            StoryInfo storyInfo = storyInfosArrayList.get(i);
                            if(storyInfo.getS_name().equals(current_story.getS_name())){
                                storyInfosArrayList.remove(i);
                                storyInfosArrayList.add(current_story);
                                Log.d("Beacon", "is equal");
                                break;
                            }
                        }
                        current_story = null;
                        current_storyUserDataList = null;
                        return null;
                    }
                })
                .create();

        proximityObserver.addProximityZone(zone);
        proximityObserverHandler = proximityObserver.start();

    }

    protected void update_screen(){
        for(int i = 0; i<mMenu.size(); i++) {
            if(mMenu.getItem(i).getItemId()==R.id.mybutton){
                mMenu.getItem(i).setVisible(true);
                break;
            }
        }
        Log.d("data handle", "update_screen!");
        String title;
        String subtitle;
        String rem;
        if(current_story == null){
            title = "Keep walking!";
            subtitle = " ";
            tv_title.setText(title);
            tv_subtitle.setText(subtitle);
            return;
        }

        boolean isNotDone = false;
        //If totally done, offer the result
        for(int i = 0; i < storyInfosArrayList.size(); i++){
            if(!storyInfosArrayList.get(i).isDone()){
                isNotDone = true;
            }
        }
        if(!isNotDone){ //if it is done
            title = "FINISHED!";
            subtitle = "You heard all the stories, are you ready to find out what Ancient Market is thinking about you?";
            tv_title.setText(title);
            tv_subtitle.setText(subtitle);
            tv_remaining.setVisibility(View.GONE);
            fragment_view.setBackgroundColor(getResources().getColor(R.color.undetectBeacon));
            tv_title.setTextColor(getResources().getColor(R.color.directions));
            tv_subtitle.setTextColor(getResources().getColor(R.color.directions));
        }

        if(current_story.isVisited() && !current_story.isDone()){
            Log.d("data handle", "update_screen"+current_story.getRemaining_sub_stories());
            title = "Hello again!";
            subtitle = "You visited me";
            rem = Integer.toString(current_story.getRemaining_sub_stories())+" /6";
            tv_title.setText(title);
            tv_subtitle.setText(subtitle);
            tv_remaining.setText(rem);
        }
        if(current_story.isDone()){
            Log.d("data handle", "update_screen"+current_story.getRemaining_sub_stories());
            title = "Hello again!";
            subtitle = "You know all of my stories";
            rem = Integer.toString(current_story.getRemaining_sub_stories())+" /6";
            tv_title.setText(title);
            tv_subtitle.setText(subtitle);
            tv_remaining.setText(rem);
        }
    }

    protected boolean isFinished(){
        for(int i = 0; i < storyInfosArrayList.size(); i++){
            if(!storyInfosArrayList.get(i).isDone())
                return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        proximityObserverHandler.stop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragment_view = inflater.inflate(R.layout.fragment_beacon_detection, container, false);
        storyInfosArrayList = (ArrayList<StoryInfo>) getArguments().getSerializable(DESCRIBABLE_KEY);
        tv_title = fragment_view.findViewById(R.id.beacon_title);
        tv_subtitle = fragment_view.findViewById(R.id.beacon_subtitle);
        tv_remaining =fragment_view.findViewById(R.id.stories_rem);
        bt_more = fragment_view.findViewById(R.id.bt_continue);
        tv_title.setText("Keep walking!");
        tv_subtitle.setText("Hidden stories are waiting for you");
        fragment_view.setBackgroundColor(getResources().getColor(R.color.grey));
        tv_title.setTextColor(getResources().getColor(R.color.directions));
        tv_subtitle.setTextColor(getResources().getColor(R.color.directions));
        tv_remaining.setVisibility(View.GONE);
        bt_more.setVisibility(View.GONE);
        return fragment_view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("data handle", "on activity result"+resultCode+" "+requestCode);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                current_storyUserDataList = (List<UserData>) data.getSerializableExtra("user data list");
                current_story = (StoryInfo) data.getSerializableExtra("current story");
                //Inform shared data
                //Add to shared data, the data from the current story
                ViewModelProviders.of(getActivity()).get(TotalSharedDataViewModel.class).sendUserData(current_storyUserDataList);
                Log.d("data handle", "on Fragment data are : "+current_storyUserDataList);
                update_screen();
            }
        }
    }

}
