package com.vchid.planner.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CategoryToNotification {

    @PrimaryKey(autoGenerate = true)
    public long cnid;

    public long cid;
    public long nid;
    public int eventType;

    public CategoryToNotification(long cnid, long cid, long nid, int eventType){
        this.cnid = cnid;
        this.cid = cid;
        this.nid = nid;
        this.eventType = eventType;
    }
}
