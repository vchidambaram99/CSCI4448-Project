package com.vchid.planner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class UpcomingTaskViewList extends LinearLayout {
    public UpcomingTaskViewList(Context context) {
        this(context,null,0);
    }

    public UpcomingTaskViewList(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public UpcomingTaskViewList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public UpcomingTaskViewList(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void initialize(MainActivity ma){
        activity = ma;
        setOrientation(VERTICAL);
        List<Task> ts = App.getDB().getAllTasks();
        for(Task t : ts) add(t);
    }
    public void add(long tid){ add(App.getDB().getTask(tid)); }
    public void add(Task t){
        long now = Calendar.getInstance().getTimeInMillis();
        long time = t.getNotificationBaseTime();
        if(now>time)return;
        UpcomingTaskView utv = new UpcomingTaskView(activity);
        utv.initialize(t,activity);
        for(int i = 0;i<getChildCount();i++){
            UpcomingTaskView utv1 = (UpcomingTaskView) getChildAt(i);
            if(utv.time<utv1.time){
                addView(utv,i);
                return;
            }
        }
        addView(utv);
    }

    public void setActivity(MainActivity a) {
        activity = a;
    }
    public MainActivity getActivity() {
        return activity;
    }

    public void filter(String s, boolean b){
        for(int i = 0;i<getChildCount();i++){
            UpcomingTaskView utv = (UpcomingTaskView)getChildAt(i);
            if(utv.category.equals(s)){
                if(b){
                    utv.setVisibility(VISIBLE);
                }else{
                    utv.setVisibility(GONE);
                }
            }
        }
    }
    public void updateColors(String s, int color){
        for(int i = 0;i<getChildCount();i++){
            UpcomingTaskView utv = (UpcomingTaskView)getChildAt(i);
            if(utv.category.equals(s)){
                utv.setColor(color);
            }
        }
    }
    public void remove(String s){
        for(int i = 0;i<getChildCount();){
            UpcomingTaskView utv = (UpcomingTaskView)getChildAt(i);
            if(utv.category.equals(s)){
                removeViewAt(i);
            }else i++;
        }
    }
    public void remove(long tid){
        for(int i = 0;i<getChildCount();i++){
            UpcomingTaskView utv = (UpcomingTaskView)getChildAt(i);
            if(utv.tid==tid){
                removeViewAt(i);
            }
        }
    }

    private MainActivity activity = null;
}
