package com.vchid.planner;

import android.app.Application;
import android.content.Context;

import com.vchid.planner.database.AppDatabase;

import androidx.room.Room;

public class App extends Application {
    private static App instance;
    private static AppDatabase database;
    public static App getInstance(){
        return instance;
    }
    public static AppDatabase getDB(){
        return database;
    }
    public static Context getContext(){
        return instance;
    }
    public void onCreate() {
        instance = this;
        super.onCreate();
        database = Room.databaseBuilder(this, AppDatabase.class, "AppDatabase").allowMainThreadQueries().build();
    }
}
