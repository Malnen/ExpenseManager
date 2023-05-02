package com.wsti.expensemanager.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.wsti.expensemanager.R;
import com.wsti.expensemanager.services.NotificationService;
import com.wsti.expensemanager.ui.login.LoginActivity;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String recordName = intent.getStringExtra("recordName");
        String guid = intent.getStringExtra("guid");
        Intent loginIntent = new Intent(context, LoginActivity.class);
        loginIntent.putExtra("guid", guid);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                42,
                loginIntent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationService.CHANEL_ID)
                .setSmallIcon(R.drawable.ic_menu_expenses)
                .setContentTitle(recordName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText("Remember to pay!")
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification);
    }
}