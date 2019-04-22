package com.vchid.planner;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class AddCategoryActivity extends ActivityWithSARCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        nameField = (EditText)findViewById(R.id.categoryName);
        redField = (EditText)findViewById(R.id.colorR);
        greenField = (EditText)findViewById(R.id.colorG);
        blueField = (EditText)findViewById(R.id.colorB);
        colorPreview = (TextView)findViewById(R.id.colorPreview);

        TextWatcher editColorPreview = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void afterTextChanged(Editable s){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){
                colorPreview.setBackgroundColor(calcColor());
            }
        };
        redField.addTextChangedListener(editColorPreview);
        greenField.addTextChangedListener(editColorPreview);
        blueField.addTextChangedListener(editColorPreview);

        oneTimeReminders = (NotificationSelectorListView)findViewById(R.id.oneTimeReminders);
        deadlineReminders = (NotificationSelectorListView)findViewById(R.id.deadlineReminders);
        repeatedReminders = (NotificationSelectorListView)findViewById(R.id.repeatedReminders);
        oneTimeReminders.setActivity(this);
        repeatedReminders.setActivity(this);
        deadlineReminders.setActivity(this);

        long oldId = getIntent().getLongExtra("oldId",0);
        if(oldId!=0){
            Category old = App.getDB().getCategory(oldId);
            nameField.setText(old.getName());
            redField.setText((0xFF&(old.getColor()>>16))+"");
            greenField.setText((0xFF&(old.getColor()>>8))+"");
            blueField.setText((0xFF&old.getColor())+"");
            oneTimeReminders.addReminders(old.onetime);
            deadlineReminders.addReminders(old.deadline);
            repeatedReminders.addReminders(old.repeated);
        }
    }

    public void save(View v){
        long oldId = getIntent().getLongExtra("oldId",0);
        Category nc = new Category(nameField.getText().toString(),calcColor(),oldId);
        nc.onetime = oneTimeReminders.getReminders();
        nc.deadline = deadlineReminders.getReminders();
        nc.repeated = repeatedReminders.getReminders();
        if(oldId==0){
            App.getDB().insertCategory(nc);
        }else{
            Category oldCategory = App.getDB().getCategory(oldId);
            List<Task> tasks = App.getDB().getTasksByCategory(oldCategory.getName(),false);
            for(Task t : tasks) oldCategory.cancelNotifications(t);
            App.getDB().updateTaskCategories(oldCategory.getName(),nc.getName());
            App.getDB().updateCategory(nc);
            for(Task t : tasks) nc.setNotifications(t);
        }
        Intent data = new Intent();
        data.putExtra("result",nc.getId());
        setResult(RESULT_OK, data);
        finish();
    }

    private int calcColor(){
        try{
            String rText = redField.getText().toString();
            String gText = greenField.getText().toString();
            String bText = blueField.getText().toString();
            return 0xFF000000|(parseColorText(rText)<<16)|(parseColorText(gText)<<8)|parseColorText(bText);
        }catch(Exception e){
            return 0xFF000000;
        }
    }
    int parseColorText(String s){
        if(s.length()==0) return 0;
        int ret;
        if(s.matches("[0-9]+")){
            ret = Integer.parseInt(s);
        }else{
            ret = Integer.parseInt(s,16);
        }
        return Math.max(Math.min(ret,255),0);
    }

    private EditText nameField;
    private EditText redField;
    private EditText greenField;
    private EditText blueField;
    private TextView colorPreview;

    private NotificationSelectorListView oneTimeReminders;
    private NotificationSelectorListView deadlineReminders;
    private NotificationSelectorListView repeatedReminders;
}
