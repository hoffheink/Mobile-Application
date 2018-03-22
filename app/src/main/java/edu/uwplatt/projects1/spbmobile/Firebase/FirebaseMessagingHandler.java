package edu.uwplatt.projects1.spbmobile.Firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by victim on 3/21/2018.
 */

public class FirebaseMessagingHandler extends FirebaseMessagingService
{
    private static final String TAG = FirebaseMessagingHandler.class.getCanonicalName();


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        Log.d(TAG, "Message recieved from: " + remoteMessage.getFrom());
        if(remoteMessage.getData().size() > 0)
        {
            Log.d(TAG, "Message payload: " + remoteMessage.getData());
        }

        if(remoteMessage.getNotification() != null)
            Log.wtf(TAG, "Message Notification Body: " + remoteMessage.getNotification());
    }
}