package edu.towson.cosc435.ijoma.timewise;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;


import edu.towson.cosc435.ijoma.timewise.sampledata.AppRepository;
import edu.towson.cosc435.ijoma.timewise.database.DatabaseHelper;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

import edu.towson.cosc435.ijoma.timewise.EmailSenderWorker;

public class MyApplication extends Application {
    private DatabaseHelper databaseHelper;
    private AppRepository appRepository;

    String CHANNEL_ID = "ch-1";
    String CHANNEL_NAME = "reminder notif";


    @Override
    public void onCreate() {
        super.onCreate();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //call functions on app start up
        databaseHelper = new DatabaseHelper(getApplicationContext());
        appRepository = new AppRepository(databaseHelper);
        scheduleEmailSendingTask();

    }

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public AppRepository getAppRepository() {
        return appRepository;
    }
    private void scheduleEmailSendingTask() {
        // Defines the email sender to send emails every 15 minutes
        PeriodicWorkRequest emailWorkRequest = new PeriodicWorkRequest.Builder(
                EmailSenderWorker.class, 15, TimeUnit.MINUTES)

                .build();

        //add the work request
        WorkManager.getInstance(this).enqueue(emailWorkRequest);
    }

}

