package com.vchid.planner;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NotificationSelectorListView extends LinearLayout {
    public NotificationSelectorListView(Context context) {
        this(context,null,0);
    }

    public NotificationSelectorListView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public NotificationSelectorListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public NotificationSelectorListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        setOrientation(VERTICAL);
        TypedArray attributes = getContext().obtainStyledAttributes(attrs,R.styleable.NotificationSelectorListView);
        inflate(getContext(),R.layout.view_notification_selector_list,this);
        ((TextView)findViewById(R.id.NSViewListTitle)).setText(attributes.getText(R.styleable.NotificationSelectorListView_title));
        attributes.recycle();
        findViewById(R.id.addNSViewButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addView(new NotificationSelectorView(getContext(),activity,null),1);
            }
        });
    }

    public List<Reminder> getReminders(){
        List<Reminder> ret = new ArrayList<>();
        for(int i = 0;i<getChildCount();i++){
            View v = getChildAt(i);
            if(v instanceof NotificationSelectorView){
                ret.add(((NotificationSelectorView)v).generateReminder());
            }
        }
        return ret;
    }

    public void addReminders(List<Reminder> lr){
        for(Reminder r : lr) addView(new NotificationSelectorView(getContext(),activity,r));
    }

    public void setActivity(ActivityWithSARCallback a) {
        activity = a;
    }

    private ActivityWithSARCallback activity = null;
}
