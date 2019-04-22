package com.vchid.planner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.vchid.planner.database.NotificationData;

import java.util.Calendar;

public abstract class Reminder {
    public Reminder(long om, long id){
        offsetMillis = om;
        nid = id;
    }
    public long getNotificationTime(Task task){
        return task.getNotificationBaseTime()-offsetMillis;
    }
    public long getOffset(){
        return offsetMillis;
    }
    public void setOffset(long om){
        offsetMillis = om;
    }
    public long getId(){
        return nid;
    }
    public void setId(long id){
        nid = id;
    }
    public PendingIntent buildNotificationIntent(Task task) {
        Intent intent = new Intent(App.getContext(),ReminderReceiver.class);
        Uri uri = new Uri.Builder().scheme("content").authority("notification").path(task.getId()+"/"+nid).build();
        intent.setDataAndNormalize(uri);
        return PendingIntent.getBroadcast(App.getContext(),0,intent,0);
    }
    public void setNotification(Task task){
        long nt = getNotificationTime(task);
        if(Calendar.getInstance().getTimeInMillis()>nt)return;
        AlarmManager alarmManager = (AlarmManager) App.getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,nt,buildNotificationIntent(task));
    }
    public void cancelNotification(Task task){
        AlarmManager alarmManager = (AlarmManager) App.getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(buildNotificationIntent(task));
    }
    private long offsetMillis;
    private long nid;

    public static final int REMINDER_NOTIFICATION = 0;
    public static final int REMINDER_ALARM = 1;
}
