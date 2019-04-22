package com.vchid.planner.database;

import com.vchid.planner.App;
import com.vchid.planner.Category;
import com.vchid.planner.DeadlineTask;
import com.vchid.planner.OneTimeTask;
import com.vchid.planner.Reminder;
import com.vchid.planner.RepeatedTask;
import com.vchid.planner.Task;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {TaskData.class,CategoryData.class, NotificationData.class,TaskToNotification.class,
        CategoryToNotification.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public static AppDatabase instance;

    public static synchronized AppDatabase getInstance(){
        if(instance==null){
            instance = Room.databaseBuilder(App.getContext(),AppDatabase.class,"database").build();
        }
        return instance;
    }
    public abstract AccessDao accessDao();

    public List<Reminder> getReminders(long[] nids){
        List<NotificationData> data = accessDao().getNotifications(nids);
        List<Reminder> reminders = new ArrayList<Reminder>(nids.length);
        for(NotificationData nd : data){
            reminders.add(nd.toReminder());
        }
        return reminders;
    }
    public Reminder getReminder(long nid){
        return accessDao().getNotification(nid).toReminder();
    }

    public String[] getCategoryStrings(){
        return accessDao().getCategoryStrings();
    }
    public Category getCategory(CategoryData cd){
        AccessDao ad = accessDao();
        Category ret = new Category(cd.name,cd.color,cd.cid);
        ret.onetime = getReminders(ad.getNidsFromCid(cd.cid,Task.TASK_ONETIME));
        ret.deadline = getReminders(ad.getNidsFromCid(cd.cid,Task.TASK_DEADLINE));
        ret.repeated = getReminders(ad.getNidsFromCid(cd.cid,Task.TASK_REPEATED));
        return ret;
    }
    public Category getCategory(String n){
        return getCategory(accessDao().getCategoryData(n));
    }
    public Category getCategory(long cid){
        return getCategory(accessDao().getCategoryData(cid));
    }
    public void insertCategory(Category c){
        AccessDao ad = accessDao();
        CategoryData cd = new CategoryData(c);
        long id = ad.insertCategoryData(cd);
        c.setId(id);
        List<Long> onetime = insertReminderList(c.onetime);
        List<Long> deadline = insertReminderList(c.deadline);
        List<Long> repeated = insertReminderList(c.repeated);
        ArrayList<CategoryToNotification> ctn = new ArrayList<>(onetime.size()+deadline.size()+repeated.size());
        for(Long l : onetime){ ctn.add(new CategoryToNotification(0,id,l,Task.TASK_ONETIME)); }
        for(Long l : deadline){ ctn.add(new CategoryToNotification(0,id,l,Task.TASK_DEADLINE)); }
        for(Long l : repeated){ ctn.add(new CategoryToNotification(0,id,l,Task.TASK_REPEATED)); }
        ad.insertCTNs(ctn);
    }
    public void updateCategory(Category c){
        AccessDao ad = accessDao();
        CategoryData cd = new CategoryData(c);
        ad.updateCategoryData(cd);
        ad.deleteNotifications(ad.getNidsFromCid(cd.cid, Task.TASK_ONETIME));
        ad.deleteNotifications(ad.getNidsFromCid(cd.cid, Task.TASK_DEADLINE));
        ad.deleteNotifications(ad.getNidsFromCid(cd.cid, Task.TASK_REPEATED));
        ad.deleteCTNs(cd.cid);
        List<Long> onetime = insertReminderList(c.onetime);
        List<Long> deadline = insertReminderList(c.deadline);
        List<Long> repeated = insertReminderList(c.repeated);
        ArrayList<CategoryToNotification> ctn = new ArrayList<>(onetime.size()+deadline.size()+repeated.size());
        for(Long l : onetime){ ctn.add(new CategoryToNotification(0,cd.cid,l,Task.TASK_ONETIME)); }
        for(Long l : deadline){ ctn.add(new CategoryToNotification(0,cd.cid,l,Task.TASK_DEADLINE)); }
        for(Long l : repeated){ ctn.add(new CategoryToNotification(0,cd.cid,l,Task.TASK_REPEATED)); }
        ad.insertCTNs(ctn);
    }
    public void deleteCategory(String c){
        AccessDao ad = accessDao();
        long cid = ad.cidFromString(c);
        ad.deleteCategoryData(c);
        ad.deleteCTNs(cid);
        long[] tids = ad.getTidsFromCategory(c);
        ad.deleteTaskData(tids);
        ad.deleteTTNsByTid(tids);
        ad.deleteNotifications(ad.getNidsFromCid(cid,Task.TASK_ONETIME));
        ad.deleteNotifications(ad.getNidsFromCid(cid,Task.TASK_DEADLINE));
        ad.deleteNotifications(ad.getNidsFromCid(cid,Task.TASK_REPEATED));
        ad.deleteNotifications(ad.getNidsFromTids(tids));
    }

    public List<Long> insertReminderList(List<Reminder> lr){
        ArrayList<NotificationData> nd  = new ArrayList<>(lr.size());
        for(Reminder r : lr){
            nd.add(new NotificationData(r));
        }
        List<Long> nids = accessDao().insertNotifications(nd);
        ListIterator<Reminder> rIter = lr.listIterator();
        ListIterator<Long> nIter = nids.listIterator();
        while(rIter.hasNext()) rIter.next().setId(nIter.next());
        return nids;
    }

    public Task dataToTask(TaskData td, boolean loadReminders){
        Task task = null;
        switch (td.eventType){
            case Task.TASK_ONETIME:
                task = new OneTimeTask(td.millis,td.name,td.category,td.tid);
                break;
            case Task.TASK_DEADLINE:
                task = new DeadlineTask(td.millis,td.name,td.category,td.tid);
                break;
            case Task.TASK_REPEATED:
                task = new RepeatedTask(td.millis,td.name,td.category,td.tid,td.weekdays);
                break;
        }
        if(loadReminders) task.reminders = getReminders(accessDao().getNidsFromTid(td.tid));
        return task;
    }
    public List<Task> dataToTask(List<TaskData> tds, boolean loadReminders){
        List<Task> ret = new ArrayList<Task>(tds.size());
        for(TaskData td : tds) ret.add(dataToTask(td,loadReminders));
        return ret;
    }
    public Task getTask(long tid){
        TaskData td = accessDao().getTaskData(tid);
        return dataToTask(td,true);
    }
    public List<Task> getAllTasks(){
        List<TaskData> td = accessDao().getAllTasks();
        return dataToTask(td,true);
    }
    public List<Task> getTasksByCategory(String category, boolean loadReminders){
        List<TaskData> tds = accessDao().getTasksFromCategory(category);
        return dataToTask(tds,loadReminders);
    }
    public void insertTask(Task t){
        AccessDao ad = accessDao();
        TaskData td = new TaskData(t);
        long id = ad.insertTaskData(td);
        t.setId(id);
        List<Long> reminders = insertReminderList(t.reminders);
        ArrayList<TaskToNotification> ttn = new ArrayList<>(reminders.size());
        for(Long l : reminders){ ttn.add(new TaskToNotification(0,id,l)); }
        ad.insertTTNs(ttn);
    }
    public void updateTask(Task t){
        AccessDao ad = accessDao();
        TaskData td = new TaskData(t);
        ad.updateTaskData(td);
        ad.deleteNotifications(ad.getNidsFromTid(td.tid));
        ad.deleteTTNs(td.tid);
        List<Long> reminders = insertReminderList(t.reminders);
        ArrayList<TaskToNotification> ttn = new ArrayList<>(reminders.size());
        for(Long l : reminders){ ttn.add(new TaskToNotification(0,td.tid,l)); }
        ad.insertTTNs(ttn);
    }
    public void updateTaskCategories(String o, String n){
        if(!o.equals(n))accessDao().updateTaskCategories(o,n);
    }
    public void deleteTask(long tid){
        AccessDao ad = accessDao();
        ad.deleteTaskData(tid);
        ad.deleteNotifications(ad.getNidsFromTid(tid));
        ad.deleteTTNs(tid);
    }
    public void timePurge(){
        AccessDao ad = accessDao();
        List<Long> tids = ad.getTasksOlderThan(Calendar.getInstance().getTimeInMillis());
        long[] nids = ad.getNidsFromTids(tids);
        ad.deleteTaskData(tids);
        ad.deleteTTNsByNid(nids);
        ad.deleteNotifications(nids);
    }
}
