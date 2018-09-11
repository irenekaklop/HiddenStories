package com.example.eirini.hiddenstories.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataOfStory implements Serializable{
    private List<UserData> userDataList;
    private StoryInfo uStory;

    public DataOfStory(List<UserData> userDataList, StoryInfo uStory) {
        this.userDataList = userDataList;
        this.uStory = uStory;
    }

    public List<UserData> getUserDataList() {
        return userDataList;
    }

    public StoryInfo getuStory() {
        return uStory;
    }
}


