package com.example.eirini.hiddenstories;


import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.media.MediaPlayer;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.eirini.hiddenstories.Utils.Chapter;
import com.example.eirini.hiddenstories.Utils.DataOfStory;
import com.example.eirini.hiddenstories.Utils.DataSharedViewModel;
import com.example.eirini.hiddenstories.Utils.Listener;
import com.example.eirini.hiddenstories.Utils.StoryInfo;
import com.example.eirini.hiddenstories.Utils.UserData;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class NFCreadingFragment extends Fragment{

    public static final String TAG = NFCreadingFragment.class.getSimpleName();
    View fragment_view;

    //Layout
    //Nfc reading
    RelativeLayout read_layout;
    private TextView mTvMessage;
    private Button bt_continue;
    //Narration
    RelativeLayout narration_layout;
    private TextView mTvNarration;
    private Button bt_play;
    private Button bt_pause;
    private Button bt_next;
    private Button bt_replay;

    MediaPlayer mp;
    String dataResourceDirectory = "raw";

    //For NFC read and communication with Story Activity
    private Listener mListener;

    //For Data collection
    List<UserData> userDataList;
    StoryInfo current_story;
    String my_choice;

    //To check which action I use
    boolean act_narration = false;

    private DataSharedViewModel model;


    public static NFCreadingFragment newInstance(List<UserData> listUserData, StoryInfo cur_story) {
        Bundle bundle = new Bundle();
        NFCreadingFragment fragment = new NFCreadingFragment();
        bundle.putSerializable("list of user data", (Serializable) listUserData);
        bundle.putSerializable("story", cur_story);
        fragment.setArguments(bundle);
        Log.d(TAG, "newInstance bundle should not be null");
        return fragment;
    }

    //Before onCreateView
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "nfc on create");
        current_story = (StoryInfo) getArguments().getSerializable("story");
        current_story.setVisited(true);
        //Initialise user data storage
        userDataList = (List<UserData>) getArguments().getSerializable("list of user data");
        //first time should be null
        if(userDataList == null){
            userDataList = new ArrayList<>();
        }
        if(!userDataList.isEmpty()){
            //inform shared data
            ViewModelProviders.of(getActivity()).get(DataSharedViewModel.class).setUserDataList(userDataList);
        }
        my_choice = "Who is speaking";
        //Enable option menu
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        fragment_view = inflater.inflate(R.layout.fragment_nfcreading,container,false);
        initViews();
        return fragment_view;
    }

    private void initViews() {
        Log.d(TAG, "initViews()");
        narration_layout = fragment_view.findViewById(R.id.narration_layout);
        read_layout = fragment_view.findViewById(R.id.read_layout);
        mTvMessage = (TextView) fragment_view.findViewById(R.id.tv_message);
        mTvNarration = (TextView) fragment_view.findViewById(R.id.tv_narration);
        bt_continue = (Button) fragment_view.findViewById(R.id.bt_continue);
        bt_next = (Button) fragment_view.findViewById(R.id.bt_end);
        bt_play = (Button) fragment_view.findViewById(R.id.bt_play);
        bt_pause = (Button) fragment_view.findViewById(R.id.bt_stop);
        bt_replay = (Button) fragment_view.findViewById(R.id.bt_loop);

        if(!act_narration){
            narration_layout.setVisibility(View.GONE);
            read_layout.setVisibility(View.VISIBLE);
            mTvMessage.setText(my_choice);
            bt_continue.setVisibility(View.GONE);
        }
        else{
            narration_layout.setVisibility(View.VISIBLE);
            read_layout.setVisibility(View.GONE);
            bt_next.setVisibility(View.VISIBLE);
            bt_play.setVisibility(View.GONE);
            bt_pause.setVisibility(View.VISIBLE);
            bt_replay.setVisibility(View.VISIBLE);
            bt_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mp.pause();
                    bt_play.setVisibility(View.VISIBLE);
                    bt_pause.setVisibility(View.GONE);
                }
            });
            bt_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mp.start();
                    bt_play.setVisibility(View.GONE);
                    bt_pause.setVisibility(View.VISIBLE);
                }
            });
            bt_replay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mp.stop();
                    mp.reset();
                    mp.start();
                    bt_play.setVisibility(View.GONE);
                    bt_pause.setVisibility(View.VISIBLE);
                }
            });

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (StoryActivity)context;
        mListener.onDialogDisplayed();
    }

    // create an action bar button
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actionbar_menu, menu);
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

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "nfc fragment on detach");
        mListener.onDialogDismissed();
    }

    void showDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        InfoFragment newFragment = InfoFragment.newInstance();
        newFragment.show(ft, "dialog");
    }

    public void onNfcDetected(Ndef ndef){
        readFromNFC(ndef);
    }

    private void readFromNFC(Ndef ndef) {
        for(int i = 0; i<userDataList.size();i++)
            Log.d(TAG, "read: Data of user is"+userDataList.get(i).isFavorite());
        String message = " ";
        if(ndef==null){
            return;
        }
        try {
            Log.d(TAG, "try to connect");
            ndef.connect();
            Log.d(TAG, "connected");
            NdefMessage ndefMessage = ndef.getNdefMessage();
            String g_message;
            while (ndefMessage == null){
                //TO BE CORRECTED
                readFromNFC(ndef);
            }
            message = new String(ndefMessage.getRecords()[0].getPayload());
            my_choice = message;

            Log.d(TAG, "readFromNFC: "+ message);
            if("trust".equals(message)){
                //fragment_view.findViewById(R.id.nfc_reading_layout).setBackgroundColor(getResources().getColor(R.color.colorAnticipation));
                g_message = "H εμπιστοσύνη";
                mTvMessage.setText(g_message);
            }
            else if("fear".equals(message)){
                //fragment_view.findViewById(R.id.nfc_reading_layout).setBackgroundColor(getResources().getColor(R.color.colorAnticipation));
                g_message = "Ο φόβος";
                mTvMessage.setText(g_message);
            }
            else if("joy".equals(message)){
                fragment_view.findViewById(R.id.nfc_reading_layout).setBackgroundColor(getResources().getColor(R.color.colorAnticipation));
                g_message = "Η χαρά";
                mTvMessage.setText(g_message);
            }
            else if("jealousy".equals(message)){
                fragment_view.findViewById(R.id.nfc_reading_layout).setBackgroundColor(getResources().getColor(R.color.colorAnticipation));
                g_message = "Η ζήλεια";
                mTvMessage.setText(g_message);
            }
            else if("exit".equals(message)){
                fragment_view.findViewById(R.id.nfc_reading_layout).setBackgroundColor(getResources().getColor(R.color.colorAnticipation));
                g_message = "Έξοδος";
                mTvMessage.setText(g_message);
            }
            else if("anticipation".equals(message)){
                fragment_view.findViewById(R.id.nfc_reading_layout).setBackgroundColor(getResources().getColor(R.color.colorAnticipation));
                g_message = "Η ψυχραιμία";
                mTvMessage.setText(g_message);
            }
            else if("anger".equals(message)){
                fragment_view.findViewById(R.id.nfc_reading_layout).setBackgroundColor(getResources().getColor(R.color.colorAnger));
                g_message = "Ο θυμός";
                mTvMessage.setText(g_message);
            }
            else if ("boredom".equals(message)){
                fragment_view.findViewById(R.id.nfc_reading_layout).setBackgroundColor(getResources().getColor(R.color.colorBoredom));
                g_message = "Η πλήξη";
                mTvMessage.setText(g_message);
            }
            else {
                g_message = "Προσπάθησε ξανα";
                mTvMessage.setText(g_message);
            }
            ndef.close();

        } catch (IOException | FormatException e) {
            e.printStackTrace();

        }

        bt_continue.setVisibility(View.VISIBLE);
        final String finalMessage = message;
        bt_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if("exit".equals(finalMessage)){
                    handle_end();
                }
                else {
                    //Proceed to Narration
                    act_narration = true;
                    Chapter ch = current_story.chapter_search(finalMessage);
                    String character = ch.getCharacter();
                    //Keep and send emotion&character
                    //Add data to userDataList
                    add_data(finalMessage, character);
                    ViewModelProviders.of(getActivity()).get(DataSharedViewModel.class).sendUserData(finalMessage, character, current_story.getS_name());
                    display_chapter(ch);
                    my_choice = "Who is speaking";
                }
            }
        });
    }

    //Check if entry already exists and then add new user data
    void add_data(String emotion, String character){
        for(int i = 0; i < userDataList.size(); i++){
            if(userDataList.get(i).containsCharacter(character)){
                return;
            }
        }
        userDataList.add(new UserData(emotion, character, current_story.getS_name()));

    }

    void handle_boredom(){
        //Save all the data & exit(?)
        //Before exit, use the latest list of user data
        ViewModelProviders.of(getActivity()).get(DataSharedViewModel.class).getUserdata().observe(this, new Observer<List<UserData>>() {
            @Override
            public void onChanged(@Nullable List<UserData> userData) {
                userDataList = userData;
            }
        });
        DataOfStory data = new DataOfStory(userDataList, current_story);
        for(int i = 0; i<userDataList.size();i++)
            Log.d(TAG, "Data of user is"+userDataList.get(i).isFavorite());
        mListener.dataHandling(data);
        Log.d(TAG, "data Handling for boredom");
        //getActivity().finish();
    }

    //if all chapters are completed
    void handle_end(){
        ViewModelProviders.of(getActivity()).get(DataSharedViewModel.class).getUserdata().observe(this, new Observer<List<UserData>>() {
            @Override
            public void onChanged(@Nullable List<UserData> userData) {
                userDataList = userData;
            }
        });
        current_story.setDone(true);
        DataOfStory data = new DataOfStory(userDataList, current_story);
        for(int i = 0; i<userDataList.size();i++)
            Log.d(TAG, "Data of user is"+userDataList.get(i).isFavorite());
        mListener.dataHandling(data);
        Log.d(TAG, "data Handling for end");
    }

    void display_chapter(final Chapter cur_chapter){
        //Inform remaining stories number
        if (!cur_chapter.isVisited()) {
            ViewModelProviders.of(getActivity()).get(DataSharedViewModel.class).newChapter();
            current_story.subRemaining_sub_stories();
            cur_chapter.setVisited(true);
        }

        if(current_story.getRemaining_sub_stories()== 7){
            current_story.setDone(true);
        }

        //Data of chapter
        HashMap<String, String> ch_info = cur_chapter.getChapter_info();
        Log.d(TAG, "display chapter");
        String title = ch_info.get("title");
        String text = ch_info.get("text");

        update_screen(text, ch_info.get("character"));

        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Proceed to Narration
                act_narration = false;
                bt_continue.setVisibility(View.GONE);
                if(current_story.isDone())
                    update_screen("You heard all the stories! You are free to go if you want.", "Hidden Stories");
                else
                    update_screen("Who is speaking?", "Hidden Stories");

                if("boredom".equals(cur_chapter.getType())){
                    handle_boredom();
                }
            }
        });
    }

    void update_screen(String text, String title){
        int rem = current_story.getRemaining_sub_stories();
        String rem_chapters = Integer.toString(rem);
        Log.d(TAG, "update screen()");
        //MAYBE TEXT IS NULL LETS CHECK
        if(act_narration){
            getActivity().setTitle(title+" "+rem_chapters+ " / 7");
            narration_layout.setVisibility(View.VISIBLE);
            mTvNarration.setText(text);
            read_layout.setVisibility(View.GONE);
            bt_play.setVisibility(View.GONE);
        }
        else {
            getActivity().setTitle(title);
            narration_layout.setVisibility(View.GONE);
            mTvMessage.setText(text);
            bt_continue.setVisibility(View.GONE);
            read_layout.setVisibility(View.VISIBLE);
            fragment_view.findViewById(R.id.nfc_reading_layout).setBackgroundColor(getResources().getColor(R.color.defaultBackground));
        }
    }

}
