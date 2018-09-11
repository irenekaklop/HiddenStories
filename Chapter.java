package com.example.eirini.hiddenstories.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Chapter implements Serializable{

    HashMap<String, String> chapter_info;
    boolean visited;

    public void setChapter_info(HashMap<String, String> chapter_info) {
        this.chapter_info = chapter_info;
        this.visited = false;
    }

    public HashMap<String, String> getChapter_info() {
        return chapter_info;
    }

    public String getType(){
        return chapter_info.get("type");
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public String getCharacter() {  return chapter_info.get("character");}
}
