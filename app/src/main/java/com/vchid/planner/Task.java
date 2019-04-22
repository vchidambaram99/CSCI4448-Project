package com.vchid.planner;

import java.util.List;

public abstract class Task {
    public Task(long millis, String n, String c, long id){
        milliTime = millis;
        name = n;
        category = c;
        tid = id;
    }
    public long getNotificationBaseTime(){
        return milliTime;
    }
    public String getName(){
        return name;
    }
    public String getCategory(){
        return category;
    }
    public long getTime(){
        return milliTime;
    }
    public long getId(){
        return tid;
    }
    public void setName(String s){
        name = s;
    }
    public void setCategory(String c){
        category = c;
    }
    public void setTime(long millis){
        milliTime = millis;
    }
    public void setId(long id){
        tid = id;
    }

    protected long milliTime;
    private String name;
    private String category;
    private long tid;
    public List<Reminder> reminders;

    public void setNotifications(){
        Category c = App.getDB().getCategory(category);
        c.setNotifications(this);
        for(Reminder r: reminders) r.setNotification(this);
    }
    public void cancelNotifications(){
        Category c = App.getDB().getCategory(category);
        c.cancelNotifications(this);
        for(Reminder r: reminders) r.cancelNotification(this);
    }

    public static final int TASK_ONETIME = 0;
    public static final int TASK_DEADLINE = 1;
    public static final int TASK_REPEATED = 2;
}
