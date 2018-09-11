package com.example.eirini.hiddenstories;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.eirini.hiddenstories.Utils.StoryInfo;
import com.example.eirini.hiddenstories.Utils.UserProfile;


import java.io.Serializable;

public class NarrationFragment extends Fragment {

    View view;
    //Data
    StoryInfo current_story;
    UserProfile userProfile;

    //For layout
    TextView story_text;
    Button bt_play;
    Button bt_pause;
    Button bt_continue;
    Button bt_replay;

    MediaPlayer mp;
    String dataResourceDirectory = "raw";

    public static NarrationFragment newInstance() {
        NarrationFragment fragment = new NarrationFragment();
        Bundle bundle = new Bundle();
        //bundle.putSerializable("story list", (Serializable) Story);
        //bundle.putSerializable("user profile", (Serializable) UserProf);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startStory();
    }

    private void startStory(){
        //Add to user profile the story
        String story_name = current_story.getS_name();
        userProfile.add_story(story_name);

        //NFC reading
        //when it returns save emotion to profile, start narration about this emotion
        //start NFC reading again

    }

    private void display_emotion_page(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_narration, container, false);

        //Retrive data
        current_story = (StoryInfo) getArguments().getSerializable("story list");
        userProfile = (UserProfile) getArguments().getSerializable("user profile");

        //Init layout
        story_text = view.findViewById(R.id.tv_narration);
        bt_play = view.findViewById(R.id.bt_play);
        bt_play.setVisibility(View.GONE);
        bt_replay = view.findViewById(R.id.bt_loop);
        bt_pause = view.findViewById(R.id.bt_stop);
        bt_continue = view.findViewById(R.id.bt_continue);

        return view;
    }

}
