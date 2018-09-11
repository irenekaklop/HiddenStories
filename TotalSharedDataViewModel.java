package com.example.eirini.hiddenstories.Utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TotalSharedDataViewModel extends ViewModel {
    private MutableLiveData<List<UserData>> user_data = new MutableLiveData<>();

    public void addUserData(String emotion, String character, String story){
       List<UserData> temp = user_data.getValue();
       if(temp == null){
           temp = new ArrayList<>();
       }
       for(int i = 0; i < temp.size(); i++){
           if(story.equals(temp.get(i).getStory_name())){
               if (character.equals(temp.get(i).getCharacter())){
                   return;
               }
           }
       }
       temp.add(new UserData(emotion, character, story));
       user_data.setValue(temp);
    }

    public void setUserData_checked(String character){
        List<UserData> temp = user_data.getValue();
        if(temp == null){
            return;
        }
        for(int i = 0; i < temp.size(); i++){
            if(character.equals(temp.get(i).getCharacter())){
                temp.get(i).setFavorite(true);
                Log.d("Total", "checked for"+character);
                return;
            }
        }
        user_data.setValue(temp);
    }

    public void setUserData_unchecked(String character){
        List<UserData> temp = user_data.getValue();
        if(temp == null){
            return;
        }
        for(int i = 0; i < temp.size(); i++){
            if(character.equals(temp.get(i).getCharacter())){
                temp.get(i).setFavorite(false);
                Log.d("TotalSharedData", "unchecked for"+character);
                return;
            }
        }
    }

    public void sendUserData(List<UserData> userDataList){
        List<UserData> temp = user_data.getValue();
        if(temp == null){
            temp = new ArrayList<>();
        }
        boolean exist = false;
        for(int i = 0; i < userDataList.size(); i++){
            UserData userData = userDataList.get(i);
            for(int j = 0; j < temp.size(); j++){
                UserData temp_uD = temp.get(j);
                if(temp_uD.containsCharacter(userData.getCharacter())){
                    exist=true;
                    //Inform already values, just in case something changed
                    temp_uD.setFavorite(userData.isFavorite());
                    break;
                }
            }
            if(!exist){
                temp.add(userData);
            }
            exist = false;
        }
        user_data.setValue(temp);
        Log.d("TotalSharedData", "total list! "+temp);
    }

    public LiveData<List<UserData>> getUserData() {
        return user_data;
    }

    public List<UserData> getUserData_current_story(String story_name){
        List<UserData> temp = user_data.getValue();
        List<UserData> returnList = new ArrayList<>();
        if(temp==null){
            temp = new ArrayList<>();
            Log.d("Beacon", "temp is null");
        }
        for (int i = 0; i< temp.size(); i++){
            Log.d("Beacon", "temp is not null"+story_name+" "+temp.get(i).getStory_name());
            if(story_name.equals(temp.get(i).getStory_name())){
                returnList.add(temp.get(i));
            }
        }
        return returnList;
    }

}
