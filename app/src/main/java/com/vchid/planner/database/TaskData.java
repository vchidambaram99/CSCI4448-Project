package com.vchid.planner.database;

import com.vchid.planner.DeadlineTask;
import com.vchid.planner.OneTimeTask;
import com.vchid.planner.RepeatedTask;
import com.vchid.planner.Task;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TaskData {
    @PrimaryKey(autoGenerate = true)
    public long tid;

    public String name;
    public String category;
    public int eventType;
    public long millis;
    public int weekdays;

    public TaskData(){}
    public TaskData(Task t){
        tid = t.getId();
        name = t.getName();
        category = t.getCategory();
        millis = t.getTime();
        if(t instanceof OneTimeTask){
            eventType = Task.TASK_ONETIME;
        }else if(t instanceof DeadlineTask){
            eventType = Task.TASK_DEADLINE;
        }else if(t instanceof RepeatedTask){
            eventType = Task.TASK_REPEATED;
            weekdays = ((RepeatedTask)t).getWeekdays();
        }
    }
}
