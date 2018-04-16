package edu.uwplatt.projects1.spbmobile.Firebase;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import edu.uwplatt.projects1.spbmobile.MainActivity;
import edu.uwplatt.projects1.spbmobile.R;

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
        super.onMessageReceived(remoteMessage);
        Map<String, String> payload = remoteMessage.getData();
        String sender = remoteMessage.getFrom();
        RemoteMessage.Notification notification = remoteMessage.getNotification();


        if(true)
        {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            parsePayload(payload.get("default"));
            Log.d(TAG, "***Payload***"  + remoteMessage.getData()); //Keep for cloud

            NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
            nBuilder.setSmallIcon(R.drawable.pancake);
            nBuilder.setSound(uri);
            nBuilder.setContentTitle("Pancake Party Application");
            nBuilder.setContentText("BULLSHIT");//Todo: parse data string

            Intent resultIntent = new Intent(this, MainActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                    0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            nBuilder.setContentIntent(resultPendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, nBuilder.build());
        }
        else
        {
            //Snackbar
        }

        /*
        Log.d(TAG, "Message recieved from: " + remoteMessage.getFrom());

        if(remoteMessage.getData().size() > 0)
        {
            Log.d(TAG, "Message payload: " + remoteMessage.getData());      // Get the data payload
            Map<String, String> map = remoteMessage.getData(); //handle with message pop-up

        }

        // If there was a notification
        if(remoteMessage.getNotification() != null)
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification());    // Get the notification payload
        else
            System.out.println("***No notification found***");
            */
    }

    @Override
    public void onDeletedMessages()
    {

    }

    private Map<String, String> parsePayload(String payload)
    {
        Map<String, String> map = new HashMap<>();

        String delimterA = "[,]";
        String [] SplitA = payload.split(delimterA);

        //First pair is always the device name
        //Second set is the reported (check with embedded)

        return null;
    }
    //{"deviceName":"charlieDevice1","reported":{"ledOn":"hello Kyle no default"}}
}