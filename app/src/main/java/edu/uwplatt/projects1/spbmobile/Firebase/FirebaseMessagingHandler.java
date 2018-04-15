package edu.uwplatt.projects1.spbmobile.Firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Handles firebase push notifications.
 */
public class FirebaseMessagingHandler extends FirebaseMessagingService
{
    private static final String TAG = FirebaseMessagingHandler.class.getCanonicalName();

    /**
     * Fired when application is in the foreground. Also fired when the notification is clicked.
     * @param remoteMessage
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        Log.d(TAG, "Message recieved from: " + remoteMessage.getFrom());

        // If there was data
        if(remoteMessage.getData().size() > 0)
            Log.d(TAG, "Message payload: " + remoteMessage.getData());      // Get the data payload

        // If there was a notification
        if(remoteMessage.getNotification() != null)
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification());    // Get the notification payload
    }
}
/*
Todo:
    1. Make default icon for notifications
 */