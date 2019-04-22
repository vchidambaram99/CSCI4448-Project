package com.vchid.planner.database;

import com.vchid.planner.NotificationSelectorListView;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface AccessDao { //TODO add more based on needs of facade
    @Query("select * from taskdata")
    List<TaskData> getAllTasks();
    @Query("select * from taskdata where tid=(:tid)")
    TaskData getTaskData(long tid);
    @Query("select * from taskdata where category=(:category)")
    List<TaskData> getTasksFromCategory(String category);
    @Query("select tid from taskdata where category=(:category)")
    long[] getTidsFromCategory(String category);
    @Insert
    long insertTaskData(TaskData td);
    @Update
    void updateTaskData(TaskData td);
    @Query("update taskdata set category=(:n) where category=(:o)")
    void updateTaskCategories(String o, String n);
    @Query("delete from taskdata where tid=(:tid)")
    void deleteTaskData(long tid);
    @Query("delete from taskdata where tid in (:tids)")
    void deleteTaskData(long[] tids);
    @Query("select tid from taskdata where millis<(:now) and eventType!=2")
    List<Long> getTasksOlderThan(long now);
    @Query("delete from taskdata where tid in (:tids)")
    void deleteTaskData(List<Long> tids);


    @Query("select * from categorydata")
    List<CategoryData> getAllCategories();
    @Query("select name from categorydata")
    String[] getCategoryStrings();
    @Query("select * from categorydata where name=(:name)")
    CategoryData getCategoryData(String name);
    @Query("select * from categorydata where cid=(:cid)")
    CategoryData getCategoryData(long cid);
    @Insert
    long insertCategoryData(CategoryData cd);
    @Update
    void updateCategoryData(CategoryData cd);
    @Query("delete from categorydata where name=(:name)")
    void deleteCategoryData(String name);
    @Query("select cid from categorydata where name=(:name)")
    long cidFromString(String name);

    @Query("select nid from tasktonotification where tid=(:tid)")
    long[] getNidsFromTid(long tid);
    @Query("select nid from categorytonotification where cid=(:cid) and eventType=(:eventType)")
    long[] getNidsFromCid(long cid, int eventType);
    @Query("select nid from tasktonotification where tid in (:tids)")
    long[] getNidsFromTids(List<Long> tids);
    @Query("select nid from tasktonotification where tid in (:tids)")
    long[] getNidsFromTids(long[] tids);

    @Query("select * from notificationdata where nid in (:nids)")
    List<NotificationData> getNotifications(long[] nids);
    @Query("select * from notificationdata where nid=(:nid)")
    NotificationData getNotification(long nid);
    @Query("delete from notificationdata where nid in (:nids)")
    void deleteNotifications(long[] nids);
    @Insert
    List<Long> insertNotifications(List<NotificationData> list);

    @Insert
    void insertCTNs(List<CategoryToNotification> list);
    @Query("delete from categorytonotification where cid=(:cid)")
    void deleteCTNs(long cid);
    @Insert
    void insertTTNs(List<TaskToNotification> list);
    @Query("delete from tasktonotification where tid=(:tid)")
    void deleteTTNs(long tid);
    @Query("delete from tasktonotification where nid in (:nids)")
    void deleteTTNsByNid(long[] nids);
    @Query("delete from tasktonotification where tid in (:tids)")
    void deleteTTNsByTid(long[] tids);
}
