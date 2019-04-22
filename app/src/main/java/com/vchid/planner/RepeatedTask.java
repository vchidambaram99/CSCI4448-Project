package com.vchid.planner;

import java.util.Calendar;

public class RepeatedTask extends Task {

    public RepeatedTask(long millis, String n, String c, long id, int w){
        super(millis,n,c,id);
        weekdays = w;
    }

    public int getWeekdays(){
        return weekdays;
    }
    public void setWeekdays(int w){
        weekdays = w;
    }

    @Override
    public long getNotificationBaseTime() {
        Calendar now = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        int currentDay = now.get(Calendar.DAY_OF_WEEK)-1;
        c.set(Calendar.MILLISECOND,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.setTimeInMillis(c.getTimeInMillis()+milliTime);
        for(int i = 0;i<8;i++){
            int d = (currentDay+i)%7;
            if((weekdays&(1<<d))!=0 && c.after(now)){
                return c.getTimeInMillis();
            }
            c.add(Calendar.DAY_OF_WEEK,1);
        }
        return 0;
    }

    private int weekdays;
}
