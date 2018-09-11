package com.example.eirini.hiddenstories.Utils;

import java.io.Serializable;
import java.util.ArrayList;

public class UserData implements Serializable{
    private String emotion;
    private String character;
    private String story_name;
    private boolean favorite;

    public UserData(String emotion, String character, String story_name) {
       this.emotion = emotion;
       this.character = character;
       this.favorite = false;
       this.story_name = story_name;
    }

    public String getStory_name() {
        return story_name;
    }

    public void setStory_name(String story_name) {
        this.story_name = story_name;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getCharacter() {
        return character;
    }

    public String getEmotion() {
        return emotion;
    }

    public boolean containsCharacter(String string){
        return character.equals(string);
    }
}
