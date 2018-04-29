package edu.uwplatt.projects1.spbmobile.Firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import edu.uwplatt.projects1.spbmobile.MainActivity;
import edu.uwplatt.projects1.spbmobile.R;

/**
 * Handles firebase push notifications.
 */
public class FirebaseMessagingHandler extends FirebaseMessagingService
{
    private static final String TAG = FirebaseMessagingHandler.class.getCanonicalName();

    /**
     * Handles notifications when the application is running in the foreground.
     * @param remoteMessage
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);
        //Log.d("Firebase", "token "+ FirebaseInstanceId.getInstance().getToken());
        if(!MainActivity.getOurInstance().isVisible())
        {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
            nBuilder.setSmallIcon(R.drawable.pancake);
            nBuilder.setSound(uri);
            nBuilder.setContentTitle("Pancake Party Application");
            if(remoteMessage.getData() != null)
                nBuilder.setContentText("This happens:" + remoteMessage.getData());
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
            {
                Gson gson = new Gson();
                NotificationPayload notificationPayload;
                Map<String, String> map = remoteMessage.getData();
                Log.d(TAG, map.toString());
                if(map.containsKey("default"))
                    notificationPayload = gson.fromJson(map.get("default").toString(), NotificationPayload.class);
                else
                    notificationPayload = gson.fromJson(gson.toJson(map).toString(), NotificationPayload.class);
                MainActivity.getOurInstance().createSnackbar(formatMessage(notificationPayload));
            }
        }
    }

    /**
     * Formats a message with data that is sent to the device.
     * @param payload the notification sent.
     * @return the formatted string.
     */
    private String formatMessage(NotificationPayload payload)
    {
        String str = "Device Name: " + payload.getDeviceName() + "\n";
        LinkedHashMap<String, String> linkedHashMap = payload.getReportedChanges();
        Set<String> set = linkedHashMap.keySet();
        for(int i = 0; i < set.size(); i++)
            str += "The " + set.toArray()[i] + " is currently " + linkedHashMap.get(set.toArray()[i]) + "\n";
        return str;
    }
}