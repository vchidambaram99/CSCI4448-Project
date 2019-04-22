package com.vchid.planner;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NotificationSelectorView extends LinearLayout {
    public NotificationSelectorView(Context context, ActivityWithSARCallback a, Reminder r) {
        this(context,null,0,a,r);
    }

    public NotificationSelectorView(Context context, AttributeSet attrs, ActivityWithSARCallback a, Reminder r) {
        this(context,attrs,0,a,r);
    }

    public NotificationSelectorView(Context context, AttributeSet attrs, int defStyleAttr, ActivityWithSARCallback a, Reminder r) {
        super(context,attrs,defStyleAttr);
        init(a,r);
    }

    public NotificationSelectorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, ActivityWithSARCallback a, Reminder r) {
        super(context,attrs,defStyleAttr,defStyleRes);
        init(a,r);
    }

    private void init(ActivityWithSARCallback a, Reminder r){
        setOrientation(VERTICAL);
        inflate(getContext(),R.layout.view_notification_selector,this);
        selectNotificationType = findViewById(R.id.selectNotificationType);
        dayField = findViewById(R.id.notificationDays);
        hourField = findViewById(R.id.notificationHours);
        minuteField = findViewById(R.id.notificationMinutes);
        title = findViewById(R.id.selectorTitle);
        expandView = findViewById(R.id.expandView);
        expandButton = findViewById(R.id.expandButton);

        ringtoneSelector = findViewById(R.id.ringtoneSelector);
        selectedRingtone = findViewById(R.id.selectedRingtone);

        activity = a;
        ringtone = null;

        selectNotificationType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.notificationSelected) {
                    ringtoneSelector.setVisibility(GONE);
                    title.setText("Notification");
                }
                else {
                    ringtoneSelector.setVisibility(VISIBLE);
                    title.setText("Alarm");
                }
                if(expandView.getVisibility()!=GONE) {
                    final int current = expandView.getMeasuredHeight();
                    expandView.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    final int target = expandView.getMeasuredHeight();
                    ValueAnimator anim = ValueAnimator.ofInt(current, target);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int v = (Integer) animation.getAnimatedValue();
                            ViewGroup.LayoutParams lp = expandView.getLayoutParams();
                            lp.height = v;
                            expandView.setLayoutParams(lp);
                        }
                    });
                    anim.setDuration(100);
                    anim.start();
                }
            }
        });
        findViewById(R.id.deleteNotificationSelector).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup)NotificationSelectorView.this.getParent()).removeView(NotificationSelectorView.this);
            }
        });
        ringtoneSelector.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,ringtone);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT,true);
                activity.startActivityWithCallback(intent,new ActivityWithSARCallback.Callback(){
                    @Override
                    public void callback(int resCode, Intent data) {
                        if(resCode==Activity.RESULT_CANCELED)return;
                        NotificationSelectorView.this.setRingtone((Uri)data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI));
                    }
                });
            }
        });

        OnClickListener expandOnClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleExpand();
            }
        };
        expandButton.setOnClickListener(expandOnClick);
        findViewById(R.id.selectorTitleBlock).setOnClickListener(expandOnClick);

        if(r!=null){
            if(r instanceof NotificationReminder){
                selectNotificationType.check(R.id.notificationSelected);
            }else{
                selectNotificationType.check(R.id.alarmSelected);
                setRingtone(((AlarmReminder)r).getRingtone());
            }
            long millis = r.getOffset();
            dayField.setText((millis/86400000)+"");
            millis %= 86400000;
            hourField.setText((millis/3600000)+"");
            millis %= 3600000;
            minuteField.setText((millis/60000)+"");
        }
    }

    public Reminder generateReminder(){
        if(selectNotificationType.getCheckedRadioButtonId()==R.id.notificationSelected){
            return new NotificationReminder(getMillis(),0);
        }else{ //TODO update with alarm ringtone
            return new AlarmReminder(getMillis(),0,ringtone);
        }
    }

    public void setRingtone(Uri uri){
        ringtone = uri;
        if(ringtone==null){
            selectedRingtone.setText("Silent");
        }else{
            selectedRingtone.setText(RingtoneManager.getRingtone(getContext(),uri).getTitle(getContext()));
        }
    }

    private long getMillis(){
        return parseDateField(dayField)*86400000+parseDateField(hourField)*3600000+parseDateField(minuteField)*60000;
    }
    private long parseDateField(TextView v){
        String s = v.getText().toString();
        if(s.length()==0)return 0;
        else return Long.parseLong(s);
    }

    private void toggleExpand(){
        if(expandView.getVisibility()==GONE){
            expandView.setVisibility(VISIBLE);
            expandView.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            final int target = expandView.getMeasuredHeight();
            ValueAnimator anim = ValueAnimator.ofFloat(0,1);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float v = (Float)animation.getAnimatedValue();
                    ViewGroup.LayoutParams lp = expandView.getLayoutParams();
                    lp.height = (int)(target*v);
                    expandView.setLayoutParams(lp);
                    expandButton.setRotation(180*v);
                }
            });
            anim.start();
        }else{
            final int start = expandView.getMeasuredHeight();
            ValueAnimator anim = ValueAnimator.ofFloat(1,0);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float v = (Float)animation.getAnimatedValue();
                    ViewGroup.LayoutParams lp = expandView.getLayoutParams();
                    lp.height = (int)(start*v);
                    expandView.setLayoutParams(lp);
                    expandButton.setRotation(180*v);
                    if(v==0) expandView.setVisibility(GONE);
                }
            });
            anim.start();
        }
    }

    private RadioGroup selectNotificationType;
    private EditText dayField;
    private EditText hourField;
    private EditText minuteField;
    private TextView title;
    private LinearLayout expandView;
    private ImageButton expandButton;

    private RelativeLayout ringtoneSelector;
    private TextView selectedRingtone;

    private ActivityWithSARCallback activity;
    private Uri ringtone;
}
