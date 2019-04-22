package com.vchid.planner;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        setNotificationNumber(context);
        Uri uri = intent.getData();
        List<String> segments = uri.getPathSegments();
        long tid = Long.parseLong(segments.get(0));
        long nid = Long.parseLong(segments.get(1));
        Task t = App.getDB().getTask(tid);
        Category c = App.getDB().getCategory(t.getCategory());
        Reminder r = App.getDB().getReminder(nid);
        PendingIntent openMainActivity = PendingIntent.getActivity(context,0,new Intent(context,MainActivity.class),0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(t.getNotificationBaseTime());
        NotificationCompat.Builder nBuilder;
        if(r instanceof AlarmReminder){ //setup an alarm notification
            Intent soundIntent = new Intent(context,SoundReceiver.class);
            Uri alarmSound = ((AlarmReminder) r).getRingtone();
            if(alarmSound!=null)soundIntent.putExtra("Uri",alarmSound.toString());
            Intent deleteIntent = new Intent(context,SoundReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,deleteIntent,0);
            context.sendBroadcast(soundIntent);
            nBuilder = new NotificationCompat.Builder(context,"alarms")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDeleteIntent(pendingIntent);
        }else{ //setup a normal notification
            nBuilder = new NotificationCompat.Builder(context,"notifications")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }
        nBuilder.setSmallIcon(R.drawable.ic_calendar)
                .setContentTitle(t.getName()) //task name
                .setContentText(calendarDate(calendar))
                .setContentIntent(openMainActivity)
                .setAutoCancel(true);
        NotificationManagerCompat nmc = NotificationManagerCompat.from(context);
        nmc.notify(notificationNumber.getAndIncrement(),nBuilder.build());
        updateNotificationNumber(context);
        if(t.getNotificationBaseTime()<Calendar.getInstance().getTimeInMillis()){
            App.getDB().deleteTask(t.getId());
        }else{
            r.setNotification(t);
        }
    }
    private void setNotificationNumber(Context context){
        SharedPreferences sp = context.getSharedPreferences("sharedPrefs",0);
        if(!sp.contains("notificationNumber")){
            sp.edit().putInt("notificationNumber",0).commit();
        }
        notificationNumber.compareAndSet(-1,sp.getInt("notificationNumber",0));
    }
    private void updateNotificationNumber(Context context){
        SharedPreferences sp = context.getSharedPreferences("sharedPrefs",0);
        sp.edit().putInt("notificationNumber",notificationNumber.get()).commit();
    }
    public static String calendarDate(Calendar c){
        String minute = ((c.get(Calendar.MINUTE)<10)?"0":"") + c.get(Calendar.MINUTE);
        String ampm = (c.get(Calendar.AM_PM)==Calendar.AM)?"AM":"PM";
        String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
        return months[c.get(Calendar.MONTH)]+" "+c.get(Calendar.DAY_OF_MONTH)+", "+c.get(Calendar.YEAR)
                +" - "+c.get(Calendar.HOUR)+":"+minute+" "+ampm;
    }
    private final static AtomicInteger notificationNumber = new AtomicInteger(-1);
}
