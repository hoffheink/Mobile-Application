package edu.uwplatt.projects1.spbmobile.Firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * This class handles Firebase message events after a token refresh.
 */

public class FirebaseMessagingHandler extends FirebaseMessagingService
{
    // Get updated InstanceID token.
    private static final String TAG = FirebaseMessagingHandler.class.getCanonicalName();


    /**
     * Fired upon message received.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        // Sender
        Log.d(TAG, "Message recieved from: " + remoteMessage.getFrom());

        // Message has payload.
        if(remoteMessage.getData().size() > 0)
        {
            Log.d(TAG, "Message payload: " + remoteMessage.getData());
        }

        // Message notification
        if(remoteMessage.getNotification() != null)
            Log.wtf(TAG, "Message Notification Body: " + remoteMessage.getNotification());
    }
}