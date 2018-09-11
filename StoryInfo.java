package com.example.eirini.hiddenstories.Utils;

import java.io.Serializable;
import java.util.ArrayList;

public class StoryInfo implements Serializable {
    private int id;
    private String s_name;
    private boolean visited;
    private boolean done;
    //Total stories are 6
    private int size;
    private int remaining_sub_stories;

    //Each story has a list of chapters
    private ArrayList<Chapter> chapterArrayList;

    public StoryInfo(String s_name, int id) {
        this.s_name = s_name;
        this.chapterArrayList = new ArrayList<>();
        this.visited = false;
        this.done = false;
        this.id = id;
        this.remaining_sub_stories = 0;
        this.size = 0;
    }

    public ArrayList<Chapter> getChapterArrayList(String name) {
        if(name.equals(s_name)) {
            return chapterArrayList;
        }
        else{
            return null;
        }
    }

    public ArrayList<Chapter> getChapterArrayList() {
        return chapterArrayList;
    }

    public boolean isVisited() {
        return visited;
    }
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isDone() {
        return done;
    }
    public void setDone(boolean done) {
        this.done = done;
    }

    public int getRemaining_sub_stories(){return remaining_sub_stories;}

    public String getS_name() {
        return s_name;
    }

    public int getId(){ return id;  }

    public void add_chapter(Chapter chapter){
        chapterArrayList.add(chapter);
        size++;
    }

    public void subRemaining_sub_stories(){
        if(remaining_sub_stories >= 6){
            done = true;   
        }
        else{
            remaining_sub_stories++;
        }
    }

    public int getSize() {
        return size;
    }

    public Chapter chapter_search(String name){
        Chapter chapter;
        for(int i=0; i < this.getSize(); i++){
            chapter = this.chapterArrayList.get(i);
            if(chapter.getType().equals(name)){
                //Found my chapter
                return chapter;
            }
        }
        return null;
    }



}
