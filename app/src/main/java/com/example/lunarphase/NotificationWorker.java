package com.example.lunarphase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.Calendar;
import java.util.Random;

public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();

        SharedPreferences pref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean notificationsEnabled = pref.getBoolean("notifications_enabled", true);
        if (!notificationsEnabled) {
            return Result.success(); // Notifications are off
        }

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        // Full Moon Notification at 8 PM
        if (hour == 20) {
            String todayPhase = MoonPhaseUtil.getTodayMoonPhaseName();
            if ("Full Moon".equals(todayPhase)) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("fragment", "home");
                NotificationUtils.showNotification(
                        context,
                        "🌕 Full Moon Tonight!",
                        "Look up and enjoy the Full Moon.",
                        intent,
                        100
                );
            }
        }

        // Next Moon Phase Notification at 12 PM
        if (hour == 12) {
            Calendar nextMajorPhaseDate = MoonPhaseUtil.getNextMajorPhaseDate();
            String nextPhaseName = MoonPhaseUtil.getNextMajorPhaseName();
            if (nextMajorPhaseDate != null && nextPhaseName != null) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("fragment", "calendar");
                NotificationUtils.showNotification(
                        context,
                        "🌙 Upcoming Phase: " + nextPhaseName,
                        "Tap to see when it occurs.",
                        intent,
                        101
                );
            }
        }

        // Universe Fact Notification at 4 PM
        if (hour == 16) {
            int random = new Random().nextInt(3); // 0, 1, 2
            if (random == 1) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("fragment", "facts");
                boolean suggestAdd = new Random().nextBoolean();

                String title = "✨ Did You Know?";
                String message = suggestAdd
                        ? "Got a space fact? Add one now!"
                        : "Learn something new about the universe!";
                NotificationUtils.showNotification(context, title, message, intent, 102);
            }
        }

        return Result.success();
    }

}
