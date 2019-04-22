package com.vchid.planner.database;

import com.vchid.planner.Category;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = "name", unique = true)})
public class CategoryData {
    @PrimaryKey(autoGenerate = true)
    public long cid;

    public String name;
    public int color;

    public CategoryData(){}
    public CategoryData(Category c){
        cid = c.getId();
        name = c.getName();
        color = c.getColor();
    }
}
