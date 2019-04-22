package com.vchid.planner;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends ActivityWithSARCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNotificationChannels();
        App.getDB().timePurge();
        mainLayout = findViewById(R.id.mainLayout);
        contentLayout = findViewById(R.id.contentLayout);
        plusLayout = findViewById(R.id.plusLayout);
        hourglassBtn = findViewById(R.id.hourglassBtn);
        calendarBtn = findViewById(R.id.calendarBtn);
        filterView = findViewById(R.id.filterView);
        upcomingView = findViewById(R.id.upcomingView);

        filterView.initialize(this);
        upcomingView.initialize(this);
        hourglassBtn.performClick();
    }
    @Override
    protected void onResume(){
        super.onResume();
        SoundReceiver.stop();
    }

    public void expandDrawer(View v){
        mainLayout.openDrawer(Gravity.LEFT);
    }

    public void viewSelect(View v){
        if(v==hourglassBtn){
            ((TextView)findViewById(R.id.appbarText)).setText("Upcoming");
            findViewById(R.id.scrollContainer).setVisibility(View.VISIBLE);
        }else if(v==calendarBtn){
            ((TextView)findViewById(R.id.appbarText)).setText("Calendar");
            findViewById(R.id.scrollContainer).setVisibility(View.GONE);
        }
    }

    private void setupNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel alarmChannel = new NotificationChannel("alarms", "alarms", NotificationManager.IMPORTANCE_HIGH);
            long[] vibrationPattern = {1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000};
            alarmChannel.setVibrationPattern(vibrationPattern);
            nm.createNotificationChannel(alarmChannel);
            nm.createNotificationChannel(new NotificationChannel("notifications", "notifications", NotificationManager.IMPORTANCE_DEFAULT));
        }
    }

    public void plusButton(View v){ //handles when the plus is tapped
        plusLayout.setVisibility(RelativeLayout.VISIBLE);
    }
    public void hidePlusView(View v){
        plusLayout.setVisibility(RelativeLayout.INVISIBLE);
    }
    public void openAddCategoryActivity(View v){
        Intent intent = new Intent(this,AddCategoryActivity.class);
        startActivityWithCallback(intent, new Callback() {
            @Override
            public void callback(int resCode, Intent data) {
                if(resCode!=RESULT_CANCELED){
                    long cid = data.getLongExtra("result",0);
                    filterView.add(cid);
                    //TODO
                }
            }
        });
    }
    public void openAddEventActivity(View v){
        Intent intent = new Intent(this,AddEventActivity.class);
        startActivityWithCallback(intent, new Callback() {
            @Override
            public void callback(int resCode, Intent data) {
                if(resCode!=RESULT_CANCELED){
                    long tid = data.getLongExtra("result",0);
                    upcomingView.add(tid);
                    //TODO
                }
            }
        });
    }

    public void onBackPressed(){
        if(plusLayout.getVisibility()==RelativeLayout.VISIBLE){
            hidePlusView(null);
        }else if(mainLayout.isDrawerOpen(GravityCompat.START)){
            mainLayout.closeDrawer(Gravity.LEFT);
        } else super.onBackPressed();
    }

    private DrawerLayout mainLayout;
    private RelativeLayout contentLayout;
    private RelativeLayout plusLayout;
    private RadioButton hourglassBtn;
    private RadioButton calendarBtn;
    private FilterListView filterView;
    private UpcomingTaskViewList upcomingView;
}
