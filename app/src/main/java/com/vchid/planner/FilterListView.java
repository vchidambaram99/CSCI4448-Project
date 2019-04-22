package com.vchid.planner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FilterListView extends LinearLayout {
    public FilterListView(Context context) {
        this(context,null,0);
    }

    public FilterListView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public FilterListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FilterListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void initialize(MainActivity ma){
        activity = ma;
        setOrientation(VERTICAL);
        String[] cs = App.getDB().getCategoryStrings();
        for(String c : cs) add(c);
    }
    public void add(String s){ add(App.getDB().getCategory(s)); }
    public void add(long cid){ add(App.getDB().getCategory(cid)); }
    public void add(Category c){
        final long cid = c.getId();
        RelativeLayout rl  = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.view_filter,null);
        final CheckBox cb = rl.findViewById(R.id.checkbox);
        int states[][] = {{android.R.attr.state_checked}, {}};
        int colors[] = {c.getColor(),c.getColor()};
        cb.setButtonTintList(new ColorStateList(states,colors));
        cb.setText(c.getName());
        cb.setChecked(true);
        cb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox c = (CheckBox)v;
                getUTVL().filter(c.getText().toString(),c.isChecked());
            }
        });
        rl.findViewById(R.id.editButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity,AddCategoryActivity.class);
                intent.putExtra("oldId",cid);
                activity.startActivityWithCallback(intent, new ActivityWithSARCallback.Callback() {
                    @Override
                    public void callback(int resCode, Intent data) {
                        if(resCode!=Activity.RESULT_CANCELED){
                            Category c = App.getDB().getCategory(cid);
                            cb.setText(c.getName());
                            int states[][] = {{android.R.attr.state_checked}, {}};
                            int colors[] = {c.getColor(),c.getColor()};
                            cb.setButtonTintList(new ColorStateList(states,colors));
                            getUTVL().updateColors(c.getName(),c.getColor());
                        }
                    }
                });
            }
        });
        rl.findViewById(R.id.deleteButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView((View)v.getParent());
                String c = ((CheckBox)(((RelativeLayout)v.getParent()).findViewById(R.id.checkbox))).getText().toString();
                List<Task> tasks = App.getDB().getTasksByCategory(c,true);
                for(Task t : tasks) t.cancelNotifications();
                App.getDB().deleteCategory(c);
                getUTVL().remove(c);
            }
        });
        addView(rl);
    }

    public void setActivity(MainActivity a) {
        activity = a;
    }

    private UpcomingTaskViewList getUTVL(){ return (UpcomingTaskViewList)activity.findViewById(R.id.upcomingView); }

    public boolean getChecked(String s){
        for(int i = 0;i<getChildCount();i++){
            CheckBox cb = getChildAt(i).findViewById(R.id.checkbox);
            if(cb.getText().toString().equals(s)){
                return cb.isChecked();
            }
        }
        return false;
    }

    private MainActivity activity = null;
}
