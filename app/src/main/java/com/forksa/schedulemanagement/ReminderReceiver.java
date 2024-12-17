package com.forksa.schedulemanagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String taskName = intent.getStringExtra("task_name");
        Toast.makeText(context, "Reminder: " + taskName, Toast.LENGTH_LONG).show();
    }
}
