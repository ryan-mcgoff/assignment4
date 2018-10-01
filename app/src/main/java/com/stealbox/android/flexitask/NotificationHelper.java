package com.stealbox.android.flexitask;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Ryan Mcgoff (4086944), Jerry Kumar (3821971), Jaydin Mcmullan (9702973)
 * Notification helper is in charge of creating channels (a requirement for devices running software higher than
 * oreo) and creating the notification that alert receiver will broadcast.
 */
public class NotificationHelper extends ContextWrapper {
    public static final String CHANNELID = "channel1ID";
    public static final String CHANNELNAME = "channel1";

    private NotificationManager mNotificationManager;


    /**
     * Creates a notification channel for devices running on Oreo or higher
     * @param base
     */
    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannels(){
        //Creates channel
        NotificationChannel notificationChannel = new NotificationChannel(CHANNELID,CHANNELNAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(R.color.colorPrimary);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getNotificationManager().createNotificationChannel(notificationChannel);
    }

    /**
     * getter method for notification manager
     * @return notification manager
     */
    public NotificationManager getNotificationManager(){

        if (mNotificationManager==null){
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        }
        return mNotificationManager;
    }


    /**
     * Builds a notification for alter receiver to use
     * @param title of the notification
     * @param message for the getChannel() method to parse
     * @return notification Builder object for alert receiver to broadcast
     */
    public NotificationCompat.Builder getChannel(String title, ArrayList <String> message){

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, mainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),CHANNELID)
                .setContentTitle(title)
                .setContentText("expand for tasks")
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_stat_name);
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();

        //Inbox Style notification (Expandable)
        inboxStyle.setBigContentTitle("Schedule:");

        //If message is empty, set text to notify user there aren't any overdue or upcomming events
        if(message.size()==0){
            inboxStyle.addLine("No upcoming events!");
        }
        else {

            for (int i = 0; i < 10; i++) {
                if (message.get(i) != null) {
                    inboxStyle.addLine(message.get(i));
                }
            }
            if(message.size()>10){
                inboxStyle.addLine("");
                inboxStyle.addLine("More...");
            }
        }

        //Moves the expanded inbox style layout object into the notification object.
        mBuilder.setStyle(inboxStyle);

        return mBuilder;

    }
}
