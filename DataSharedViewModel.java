package com.example.eirini.hiddenstories.Utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataSharedViewModel extends ViewModel {
    private MutableLiveData<ArrayList<String>> emotions_list= new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> characters= new MutableLiveData<>();
    private MutableLiveData<List<UserData>> user_data = new MutableLiveData<>();
    private MutableLiveData<Integer> stories_collected = new MutableLiveData<>();

    public void newChapter(){
        Integer stories= stories_collected.getValue();
        if(stories == null)
            stories = 0;
        stories++;
        stories_collected.setValue(stories);
    }

    public MutableLiveData<Integer> getStories_collected(){
        return stories_collected;
    }

    public void setUserData_checked(String character){
        List<UserData> temp = user_data.getValue();
        if(temp == null){
            return;
        }
        for(int i = 0; i < temp.size(); i++){
            if(character.equals(temp.get(i).getCharacter())){
                temp.get(i).setFavorite(true);
                Log.d("SharedData", "checked for"+character);
                return;
            }
        }
    }

    public void setUserData_unchecked(String character){
        List<UserData> temp = user_data.getValue();
        if(temp == null){
            return;
        }
        for(int i = 0; i < temp.size(); i++){
            if(character.equals(temp.get(i).getCharacter())){
                temp.get(i).setFavorite(false);
                Log.d("SharedData", "checked for"+character);
                return;
            }
        }
    }

    public void setUserDataList(List<UserData> new_list){
        List<UserData> temp = user_data.getValue();
        if(temp==null){
            temp = new ArrayList<>();
        }
        temp.addAll(new_list);
        user_data.setValue(temp);
    }

    public void sendUserData(String new_emotion, String new_character, String story_name){
        List<UserData> temp = user_data.getValue();
        if(temp==null) {
            temp = new ArrayList<>();
        }
        boolean exists = false;
        for (int i =0 ; i < temp.size(); i++){
            UserData temp_userData = temp.get(i);
            if(temp_userData.containsCharacter(new_character)){
                exists = true;
                break;
            }
        }
        if(!exists){
            temp.add(new UserData(new_emotion, new_character, story_name));
        }
        user_data.setValue(temp);
    }

    public LiveData<ArrayList<String>> get_characters_data() {
        return characters;
    }

    public LiveData<List<UserData>> getUserdata() {
        return user_data;
    }

    public LiveData<ArrayList<String>> get_data() {
        return emotions_list;
    }

}
