package com.vchid.planner;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public abstract class ActivityWithSARCallback extends AppCompatActivity {
    public void startActivityWithCallback(Intent intent, Callback cb){
        while(reqs.containsKey(reqCode))reqCode++;
        reqs.put(reqCode,cb);
        super.startActivityForResult(intent,reqCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        reqs.get(requestCode).callback(resultCode,data);
        reqs.remove(requestCode);
    }

    protected int reqCode = 0;
    protected HashMap<Integer, Callback> reqs = new HashMap<Integer, Callback>();
    public static abstract class Callback{
        public abstract void callback(int resCode, Intent data);
    }
}
