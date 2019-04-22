package com.vchid.planner.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TaskToNotification {
    @PrimaryKey(autoGenerate = true)
    public long tnid;

    public long tid;
    public long nid;

    public TaskToNotification(long tnid, long tid, long nid){
        this.tnid = tnid;
        this.tid = tid;
        this.nid = nid;
    }
}
