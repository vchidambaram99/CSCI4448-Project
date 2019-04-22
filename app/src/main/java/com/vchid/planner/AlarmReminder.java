package com.vchid.planner;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.vchid.planner.database.NotificationData;

public class AlarmReminder extends Reminder {
    public AlarmReminder(long om, long id, Uri r){
        super(om,id);
        ringtone = r;
    }

    public Uri getRingtone(){
        return ringtone;
    }
    public void setRingtone(Uri r){
        ringtone = r;
    }

    private Uri ringtone;
}
