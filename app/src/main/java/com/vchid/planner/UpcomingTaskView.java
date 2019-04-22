package com.vchid.planner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

public class UpcomingTaskView extends RelativeLayout {
    public UpcomingTaskView(Context context) {
        this(context,null,0);
    }

    public UpcomingTaskView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public UpcomingTaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
    }

    public UpcomingTaskView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context,attrs,defStyleAttr,defStyleRes);
    }

    public void initialize(Task task, final MainActivity ma){
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dpToPx(20,ma),dpToPx(10,ma),dpToPx(15,ma),dpToPx(10,ma));
        setLayoutParams(lp);
        inflate(getContext(),R.layout.view_upcoming_task,this);
        setTask(task);
        findViewById(R.id.editButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ma,AddEventActivity.class);
                intent.putExtra("oldId",tid);
                ma.startActivityWithCallback(intent, new ActivityWithSARCallback.Callback() {
                    @Override
                    public void callback(int resCode, Intent data) {
                        if(resCode!= Activity.RESULT_CANCELED){
                            Task t = App.getDB().getTask(tid);
                            setTask(t);
                            if(((FilterListView)ma.findViewById(R.id.filterView)).getChecked(t.getCategory())){
                                setVisibility(VISIBLE);
                            }else{
                                setVisibility(GONE);
                            }
                        }
                    }
                });
            }
        });
        findViewById(R.id.deleteButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Task t = App.getDB().getTask(tid);
                t.cancelNotifications();
                App.getDB().deleteTask(tid);
                ((UpcomingTaskViewList)getParent()).removeView(UpcomingTaskView.this);
                //TODO
            }
        });
    }

    private void setTask(Task task){
        time = task.getNotificationBaseTime();
        category = task.getCategory();
        tid = task.getId();
        Category c = App.getDB().getCategory(category);
        findViewById(R.id.colorBlock).setBackgroundColor(c.getColor());
        ((TextView)findViewById(R.id.taskName)).setText(task.getName());
        Calendar a = Calendar.getInstance();
        a.setTimeInMillis(time);
        ((TextView)findViewById(R.id.taskDate)).setText(ReminderReceiver.calendarDate(a));
    }
    public void setColor(int color){ findViewById(R.id.colorBlock).setBackgroundColor(color); }

    private int dpToPx(float dp, Context context){
        return Math.round(dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private MainActivity getMA(){
        return ((UpcomingTaskViewList)getParent()).getActivity();
    }

    public String category;
    public long time;
    public long tid;
}
