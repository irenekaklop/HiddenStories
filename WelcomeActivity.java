package com.example.eirini.hiddenstories;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eirini.hiddenstories.Utils.Chapter;
import com.example.eirini.hiddenstories.Utils.HttpHandler;
import com.example.eirini.hiddenstories.Utils.StoryInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class WelcomeActivity extends AppCompatActivity {

    private String TAG = WelcomeActivity.class.getSimpleName();
    ArrayList<StoryInfo>  storyUtilsArrayList = new ArrayList<>();

    Button bt_continue;

    int n_story ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        String intro = "Intro .... ";
        TextView intro_tv;
        //Text and sound
        //MediaPlayer mp ;
        intro_tv = findViewById(R.id.intro_text);
        intro_tv.setText(intro);

        bt_continue = findViewById(R.id.bt_continue);
        bt_continue.setVisibility(View.GONE);

        n_story = 0;
        new GetContacts().execute();
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(WelcomeActivity.this, "Json Data is downloading", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://api.myjson.com/bins/ntnc0";

            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    //Get List of Stories
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array nodes
                    JSONArray stories_arrList = jsonObj.getJSONArray("nodes");

                    //Looping through All stories
                    for(int s = 0; s <stories_arrList.length(); s++){
                        //Get s Story
                        JSONObject story = stories_arrList.getJSONObject(s);
                        String story_name = story.getString("name");

                        //Initialize new story
                        StoryInfo cur_story = new StoryInfo(story_name, n_story);
                        n_story++;

                        JSONArray chapters_arrList = story.getJSONArray("nodes");
                        // looping through All Chapters of this story
                        for (int i = 0; i < chapters_arrList.length(); i++) {
                            JSONObject chapter = chapters_arrList.getJSONObject(i);
                            //Intro/ Neutral/ Question/ Positive/ Negative/ End
                            String ch_title = chapter.getString("title");
                            String ch_type = chapter.getString("type");
                            String ch_text = chapter.getString("text");
                            String character = chapter.getString("character");

                            // Get audio from scene
                            JSONObject audio = chapter.getJSONObject("audio");
                            String src = audio.getString("src");
                            String audio_name = audio.getString("name");

                            Chapter ch = new Chapter();

                            HashMap<String, String> info = new HashMap<>();

                            info.put("type", ch_type);
                            info.put("name", ch_title);
                            info.put("text", ch_text);
                            info.put("character", character);
                            info.put("src", src);
                            info.put("audio_name", audio_name);

                            ch.setChapter_info(info);
                            cur_story.add_chapter(ch);
                        }
                        storyUtilsArrayList.add(cur_story);
                    }
                    Log.e(TAG, "Finish building the list");
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            bt_continue.setVisibility(View.VISIBLE);
            bt_continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    intent.putExtra("storiesList", storyUtilsArrayList);
                    startActivity(intent);
                }
            });
        }
    }
}
