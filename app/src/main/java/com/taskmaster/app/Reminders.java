package com.taskmaster.app;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import java.util.Calendar;

final class Reminders {
    static final String CHANNEL = "daily";
    static final int[] HOURS = {9, 22}; // 9:00 and 22:00 every day

    static void createChannel(Context c) {
        NotificationManager nm = c.getSystemService(NotificationManager.class);
        nm.createNotificationChannel(new NotificationChannel(CHANNEL, "Daily reminders",
                NotificationManager.IMPORTANCE_DEFAULT));
    }

    static boolean exactOk(Context c) {
        if (Build.VERSION.SDK_INT < 31) return true;
        return c.getSystemService(AlarmManager.class).canScheduleExactAlarms();
    }

    static void scheduleAll(Context c) {
        for (int h : HOURS) schedule(c, h, 1000);
    }

    // minLeadMs stops a just-fired alarm from rescheduling itself for the same day
    static void schedule(Context c, int hour, long minLeadMs) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        if (cal.getTimeInMillis() <= System.currentTimeMillis() + minLeadMs) cal.add(Calendar.DAY_OF_YEAR, 1);

        Intent i = new Intent(c, ReminderReceiver.class).putExtra("hour", hour);
        PendingIntent pi = PendingIntent.getBroadcast(c, hour, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = c.getSystemService(AlarmManager.class);
        if (exactOk(c)) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
        else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
    }

    static void show(Context c, int hour) {
        if (Build.VERSION.SDK_INT >= 33 &&
                c.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return;
        createChannel(c);
        boolean night = hour >= 18;
        String title = night ? "Night check" : "Good morning";
        String text = night ? "Tick off what you did today and update your tasks."
                            : "Check today's tasks and plan your day.";
        Intent open = new Intent(c, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(c, 100, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification n = new Notification.Builder(c, CHANNEL)
                .setSmallIcon(R.drawable.ic_stat)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        c.getSystemService(NotificationManager.class).notify(night ? 2 : 1, n);
    }
}
