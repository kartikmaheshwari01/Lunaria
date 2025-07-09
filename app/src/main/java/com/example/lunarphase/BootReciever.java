package com.example.lunarphase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class BootReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            PeriodicWorkRequest notificationWork =
                    new PeriodicWorkRequest.Builder(NotificationWorker.class, 1, TimeUnit.HOURS)
                            .build();

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    "daily_moon_notification",
                    ExistingPeriodicWorkPolicy.KEEP,
                    notificationWork
            );
        }
    }
}
