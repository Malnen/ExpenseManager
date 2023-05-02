package com.wsti.expensemanager.services;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.wsti.expensemanager.data.model.ExpenseRecord;
import com.wsti.expensemanager.receivers.NotificationReceiver;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class NotificationService {

    public static final String CHANEL_ID = "expense_notifications";

    private static NotificationService INSTANCE;

    private final NotificationManager notificationManager;
    private final AlarmManager alarmManager;

    private NotificationService(NotificationManager notificationManager, AlarmManager alarmManager) {
        this.notificationManager = notificationManager;
        this.alarmManager = alarmManager;
    }

    public static void init(NotificationManager notificationManager, AlarmManager alarmManager) {
        INSTANCE = new NotificationService(notificationManager, alarmManager);
    }

    public static NotificationService getInstance() {
        return INSTANCE;
    }

    public void scheduleOutcomeNotification(ExpenseRecord expenseRecord, Context context) {
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANEL_ID, "Expense Notifications", importance);
        notificationManager.createNotificationChannel(channel);
        Intent intent = new Intent(context, NotificationReceiver.class);
        String recordName = expenseRecord.getName();
        String guid = expenseRecord.getGuid();
        intent.putExtra("recordName", recordName);
        intent.putExtra("guid", guid);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                42,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        LocalDateTime reminderDate = expenseRecord.getReminderDate();
        if (reminderDate != null) {
            schedule(pendingIntent, reminderDate);
        }
    }

    public void cancelNotification(Context context, ExpenseRecord expenseRecord) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        String recordName = expenseRecord.getName();
        String guid = expenseRecord.getGuid();
        intent.putExtra("recordName", recordName);
        intent.putExtra("guid", guid);
        PendingIntent pending = PendingIntent.getBroadcast(
                context,
                42,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pending);
    }

    private void schedule(PendingIntent pendingIntent, LocalDateTime reminderDate) {
        ZonedDateTime zonedDateTime = reminderDate.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        long triggerTime = instant.toEpochMilli();
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }

}
