package com.vchid.planner;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.vchid.planner.database.AccessDao;
import com.vchid.planner.database.TaskData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class AddEventActivity extends ActivityWithSARCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        eventName = findViewById(R.id.eventName);
        categorySelector = findViewById(R.id.categorySelector);
        eventTypeSelector = findViewById(R.id.eventTypeSelector);
        timeLayout = findViewById(R.id.timeSelectorTR);
        timeTypeText = findViewById(R.id.eventTimeTypeText);
        eventDate = findViewById(R.id.eventDate);
        eventTime = findViewById(R.id.eventTime);
        weekdaySelection = findViewById(R.id.weekdaySelection);
        additionalReminders = findViewById(R.id.additionalReminders);
        additionalReminders.setActivity(this);

        String[] eventTypes = {"","One Time","Deadline","Repeated"};
        eventTypeSelector.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,eventTypes));
        eventTypeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = (String)((TextView)view).getText();
                if(s.equals("One Time")){
                    timeTypeText.setText("Start time");
                    timeLayout.setVisibility(View.VISIBLE);
                    eventDate.setVisibility(View.VISIBLE);
                    weekdaySelection.setVisibility(View.GONE);
                }else if(s.equals("Deadline")){
                    timeTypeText.setText("End time");
                    timeLayout.setVisibility(View.VISIBLE);
                    eventDate.setVisibility(View.VISIBLE);
                    weekdaySelection.setVisibility(View.GONE);
                }else if(s.equals("Repeated")){
                    timeTypeText.setText("Start time");
                    timeLayout.setVisibility(View.VISIBLE);
                    eventDate.setVisibility(View.GONE);
                    weekdaySelection.setVisibility(View.VISIBLE);
                }else{
                    timeLayout.setVisibility(View.GONE);
                    weekdaySelection.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        categorySelector.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,App.getDB().getCategoryStrings()));

        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePicker = new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar c = Calendar.getInstance();
                        c.set(year,month,dayOfMonth);
                        setDateFields(c);
                    }
                },year,month,day);
                datePicker.show();
            }
        });
        eventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //TODO FIX
                TimePickerDialog timePicker = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        setTimeFields(hourOfDay,minute);
                    }
                },hour,minute,false);
                timePicker.show();
            }
        });

        long oldId = getIntent().getLongExtra("oldId",0);
        if(oldId!=0){
            Task t = App.getDB().getTask(oldId);
            eventName.setText(t.getName());
            categorySelector.setSelection(((ArrayAdapter<String>)categorySelector.getAdapter()).getPosition(t.getCategory()));
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(t.getTime());
            setTimeFields(c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE));
            setDateFields(c);
            String s = "";
            if(t instanceof OneTimeTask) s = "One Time";
            else if(t instanceof DeadlineTask) s = "Deadline";
            else if(t instanceof RepeatedTask){
                s = "Repeated";
                int[] dayIds = {R.id.sundayButton,R.id.mondayButton,R.id.tuesdayButton,R.id.wednesdayButton,
                                R.id.thursdayButton,R.id.fridayButton,R.id.saturdayButton};
                weekdaysSelected = ((RepeatedTask) t).getWeekdays();
                for(int i = 0;i<7;i++){
                    TextView v = findViewById(dayIds[i]);
                    if((weekdaysSelected&(1<<i))!=0){
                        v.setTextColor(0xFFFFFFFF);
                        v.setBackgroundColor(0xFF808080);
                    }
                }
                setDateFields(Calendar.getInstance());
            }
            eventTypeSelector.setSelection(((ArrayAdapter<String>)eventTypeSelector.getAdapter()).getPosition(s));
            additionalReminders.addReminders(t.reminders);
        }else{
            Calendar now = Calendar.getInstance();
            setDateFields(now);
            setTimeFields(now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE));
        }
    }

    public void save(View v){
        long oldId = getIntent().getLongExtra("oldId",0);
        Task t = null;
        String category = ((TextView)categorySelector.getSelectedView()).getText().toString();
        switch(((TextView)eventTypeSelector.getSelectedView()).getText().toString()){
            case "One Time":
                t = new OneTimeTask(dateMillis()+timeMillis(),eventName.getText().toString(),category,oldId);
                break;
            case "Deadline":
                t = new DeadlineTask(dateMillis()+timeMillis(),eventName.getText().toString(),category,oldId);
                break;
            case "Repeated":
                t = new RepeatedTask(timeMillis(),eventName.getText().toString(),category,oldId,weekdaysSelected);
                break;
            default: return;
        }
        t.reminders = additionalReminders.getReminders();
        if(oldId==0){ //TODO set alarms
            App.getDB().insertTask(t);
            t.setNotifications();
        }else{
            //TODO update task
            Task oldTask = App.getDB().getTask(oldId);
            oldTask.cancelNotifications();
            App.getDB().updateTask(t);
            t.setNotifications();
        }
        Intent data = new Intent();
        data.putExtra("result",t.getId());
        setResult(RESULT_OK, data);
        finish();
    }

    public void weekdayButtonToggle(View v){
        int idx = 0;
        switch (v.getId()){
            case R.id.sundayButton: idx=0; break;
            case R.id.mondayButton: idx=1; break;
            case R.id.tuesdayButton: idx=2; break;
            case R.id.wednesdayButton: idx=3; break;
            case R.id.thursdayButton: idx=4; break;
            case R.id.fridayButton: idx=5; break;
            case R.id.saturdayButton: idx=6; break;
        }
        weekdaysSelected = weekdaysSelected^(1<<idx);
        if((weekdaysSelected&(1<<idx))!=0){
            ((TextView)v).setTextColor(0xFFFFFFFF);
            v.setBackgroundColor(0xFF808080);
        }else{
            ((TextView)v).setTextColor(0xFF303030);
            v.setBackgroundColor(0);
        }
    }

    private void setDateFields(Calendar c){
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        eventDate.setText(sdf.format(c.getTimeInMillis()));
    }
    private void setTimeFields(int hour, int minute){
        this.hour = hour;
        this.minute = minute;
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        eventTime.setText(sdf.format(timeMillis()));
    }
    private long dateMillis(){
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year,month,day);
        return c.getTimeInMillis();
    }
    private long timeMillis(){
        return hour*3600000+minute*60000;
    }

    private TextView eventName;
    private Spinner categorySelector;
    private Spinner eventTypeSelector;
    private LinearLayout timeLayout;
    private TextView timeTypeText;
    private TextView eventDate;
    private TextView eventTime;
    private NotificationSelectorListView additionalReminders;
    private LinearLayout weekdaySelection;

    private int weekdaysSelected = 0;
    private int year = 0;
    private int month = 0;
    private int day = 0;
    private int hour = 0;
    private int minute = 0;

}
