package com.vchid.planner.database;

import android.app.Notification;
import android.net.Uri;

import com.vchid.planner.AlarmReminder;
import com.vchid.planner.NotificationReminder;
import com.vchid.planner.Reminder;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NotificationData {
    @PrimaryKey(autoGenerate = true)
    public long nid;

    public int notifType;
    public long millis;
    public String ringtone;

    public NotificationData(){}
    public NotificationData(Reminder nr){
        nid = nr.getId();
        millis = nr.getOffset();
        if(nr instanceof AlarmReminder){
            Uri uri = ((AlarmReminder) nr).getRingtone();
            if(uri!=null)ringtone = uri.toString();
            else ringtone = null;
            notifType = Reminder.REMINDER_ALARM;
        } else if (nr instanceof NotificationReminder){
            notifType = Reminder.REMINDER_NOTIFICATION;
        }
    }

    public Reminder toReminder(){
        switch (notifType){
            case Reminder.REMINDER_NOTIFICATION:
                return new NotificationReminder(millis,nid);
            case Reminder.REMINDER_ALARM:
                if(ringtone==null)return new AlarmReminder(millis,nid,null);
                return new AlarmReminder(millis,nid, Uri.parse(ringtone));
        }
        return null;
    }
}
