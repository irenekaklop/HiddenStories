package com.example.eirini.hiddenstories.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class UserProfile implements Serializable{
    //For current story
    //ex.[(Ksanthipi, Thymos)]
    protected ArrayList<HashMap<String,String>> current_emotions;

    //For total experience

    //ex.[Ksanthipi, ... ]
    protected ArrayList<String> my_stories;
    protected ArrayList<String> characters_collected;
    protected ArrayList<String> favorites;
    protected HashMap<String, Integer> total_emotion_map;

    public UserProfile() {
        this.current_emotions = new ArrayList<>();
    }

    public ArrayList<HashMap<String, String>> getCurrent_emotions() {
        return current_emotions;
    }

    public ArrayList<String> getCharacters_collected() {
        return characters_collected;
    }

    public ArrayList<String> getMy_stories() {
        return my_stories;
    }

    public ArrayList<String> getFavorites() {
        return favorites;
    }

    public HashMap<String, Integer> getTotal_emotion_map() {
        return total_emotion_map;
    }

    public void add_story(String story_name){
        my_stories.add(story_name);
    }

    public void add_character(String name){
        characters_collected.add(name);
    }

    public void add_favorite(String favorite_name){
        favorites.add(favorite_name);
    }

    public void remove_favorite(String name){
        favorites.remove(name);
    }

    public void add_emotion(String emotion){
        total_emotion_map.put(emotion, total_emotion_map.get(emotion)+1);
    }

}
