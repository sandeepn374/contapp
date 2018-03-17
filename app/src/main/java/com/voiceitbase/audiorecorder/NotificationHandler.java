package com.voiceitbase.audiorecorder;

/**
 * Created by kshravi on 13/03/2018 AD.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import java.util.Random;

public class NotificationHandler {
    // Notification handler singleton
    private static NotificationHandler nHandler;
    private static NotificationManager mNotificationManager;


    public NotificationHandler() {}


    /**
     * Singleton pattern implementation
     * @return
     */
    public static  NotificationHandler getInstance(Context context) {
        if(nHandler == null) {
            nHandler = new NotificationHandler();
            mNotificationManager =
                    (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return nHandler;
    }







    /**
     * Show a determinate and undeterminate progress notification
     * @param context, activity context
     */
    public void createProgressNotification (final Context context) {

        // used to update the progress notification
        final int progresID = new Random().nextInt(1000);

        // building the notification
        final NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_refresh_button_enable)
                .setContentTitle("Progres notification")
                .setContentText("Now waiting")
                .setTicker("Progress notification created")
                .setUsesChronometer(true)
                .setProgress(100, 0, true);



        AsyncTask<Integer, Integer, Integer> downloadTask = new AsyncTask<Integer, Integer, Integer>() {
            @Override
            protected void onPreExecute () {
                super.onPreExecute();
                mNotificationManager.notify(progresID, nBuilder.build());
            }

            @Override
            protected Integer doInBackground (Integer... params) {
                try {
                    // Sleeps 2 seconds to show the undeterminated progress
                    //Thread.sleep(5000);

                    // update the progress
                    for (int i = 0; i < 101; i+=5) {
                        nBuilder
                                .setContentTitle("Progress running...")
                                .setContentText("Now running...")
                                .setProgress(100, i, false)
                                .setSmallIcon(R.drawable.ic_refresh_button_enable)
                                .setContentInfo(i + " %");

                        // use the same id for update instead created another one
                        mNotificationManager.notify(progresID, nBuilder.build());
                        Thread.sleep(500);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return null;
            }


            @Override
            protected void onPostExecute (Integer integer) {
                super.onPostExecute(integer);

                nBuilder.setContentText("Upload finished :D")
                        .setContentTitle("Upload finished !!")
                        .setTicker("Upload finished !!!")
                        .setSmallIcon(R.drawable.ic_refresh_button_enable)
                        .setUsesChronometer(false);

                mNotificationManager.notify(progresID, nBuilder.build());
            }
        };

        // Executes the progress task
        downloadTask.execute();
    }


}

