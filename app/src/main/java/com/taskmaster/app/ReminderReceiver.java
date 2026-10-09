package com.taskmaster.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent i) {
        if (i.hasExtra("hour")) {
            int h = i.getIntExtra("hour", 9);
            Reminders.show(c, h);
            Reminders.schedule(c, h, 30 * 60 * 1000L);
        } else {
            Reminders.scheduleAll(c); // after reboot or clock/timezone change
        }
    }
}
