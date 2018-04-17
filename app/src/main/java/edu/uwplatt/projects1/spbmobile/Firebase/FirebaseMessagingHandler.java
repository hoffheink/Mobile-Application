package edu.uwplatt.projects1.spbmobile.Firebase;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.uwplatt.projects1.spbmobile.Appliance.Appliance;
import edu.uwplatt.projects1.spbmobile.MainActivity;
import edu.uwplatt.projects1.spbmobile.R;

/**
 * Handles firebase push notifications.
 */
public class FirebaseMessagingHandler extends FirebaseMessagingService
{
    private static final String TAG = FirebaseMessagingHandler.class.getCanonicalName();
    private static final int KEY_VALUE_OFFSET = 2;

    /**
     * Fired when application is in the foreground. Also fired when the notification is clicked.
     * @param remoteMessage
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        if(MainActivity.getOurInstance().isVisible() == false)
        {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
            nBuilder.setSmallIcon(R.drawable.pancake);
            nBuilder.setSound(uri);
            nBuilder.setContentTitle("Pancake Party Application");

            if(remoteMessage.getData() != null)
            {
                nBuilder.setContentText("This happens:" + remoteMessage.getData());
            }

            Intent resultIntent = new Intent(this, MainActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                    0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            nBuilder.setContentIntent(resultPendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, nBuilder.build());
        }
        else
        {
            if(remoteMessage.getData() != null)
                MainActivity.getOurInstance().createSnackbar(remoteMessage.getData().toString());

        }
    }


    private ArrayList<String> parsePayload(String payload)
    {
        //{"deviceName":"charlieDevice1","reported":{"ledOn":"hello Kyle no default"}}
        ArrayList<String> parsedPayload = new ArrayList<>();

        String delimiter_A = "[,]+";
        String delimiter_B = "[:]+";

        String [] initalParse = payload.split(delimiter_A);
        parsedPayload.add(initalParse[0].split(delimiter_B)[1]);

        String [] reportedRecord = initalParse[1].split(delimiter_B);
        String [] statusRecords = reportedRecord[1].split(delimiter_A);

        for(int i = 0; i < statusRecords.length; i++)
        {
            String [] str = statusRecords[i].split(delimiter_B);
            parsedPayload.add(removeBraces(str[0]));
            parsedPayload.add(removeBraces(str[1]));
        }
        return parsedPayload;
    }


    private String removeBraces(String str)
    {
        String newStr = "";
        for(int i = 0; i < str.length(); i++)
        {
            if(str.charAt(i) != '{' && str.charAt(i) != '}' )
                newStr += str.charAt(i);
        }
        return str;
    }

    private String formatMessage(ArrayList<String> msgElements)
    {
        String str = "Device Name: " + msgElements.get(0) + "\n";
        for(int i = 1; i < msgElements.size(); i += KEY_VALUE_OFFSET)
            str += "The " + msgElements.get(i) + " is in the " + msgElements.get(i + 1) + "state.\n";
        return str;
    }
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