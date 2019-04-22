package com.vchid.planner;

import java.util.ArrayList;
import java.util.List;

public class Category {

    public Category(String n, int c, long id){
        name = n;
        color = c;
        cid = id;
        onetime = new ArrayList<Reminder>();
        deadline = new ArrayList<Reminder>();
        repeated = new ArrayList<Reminder>();
    }


    public String getName(){
        return name;
    }
    public long getId(){
        return cid;
    }
    public int getColor(){
        return color;
    }
    public void setName(String s){
        name = s;
    }
    public void setId(long id){
        cid = id;
    }
    public void setColor(int c){
        color = c;
    }

    public void setNotifications(Task t){
        for(Reminder r : onetime) r.setNotification(t);
        for(Reminder r : deadline) r.setNotification(t);
        for(Reminder r : repeated) r.setNotification(t);
    }
    public void cancelNotifications(Task t){
        for(Reminder r : onetime) r.cancelNotification(t);
        for(Reminder r : deadline) r.cancelNotification(t);
        for(Reminder r : repeated) r.cancelNotification(t);
    }

    private long cid;
    private String name;
    private int color;
    public List<Reminder> onetime;
    public List<Reminder> deadline;
    public List<Reminder> repeated;
}
