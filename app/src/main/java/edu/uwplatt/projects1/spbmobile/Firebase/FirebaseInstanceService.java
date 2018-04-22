package edu.uwplatt.projects1.spbmobile.Firebase;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
/**
 * Creates a firebase instance. ase class for firebase instance ID token refresh events.
 */
public class FirebaseInstanceService extends FirebaseInstanceIdService
{
    private final static String TAG = FirebaseInstanceService.class.getCanonicalName();

    /**
     * Fired when the firebase token needs to be refreshed.
     */
    @Override
    public void onTokenRefresh()
    {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.wtf(TAG, "Refreshed token: " + refreshedToken);

        //Put the thing in here

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Unused. May be used when operations on the token need to be done after updating.
     *
     * @param refreshedToken
     */
    private void sendRegistrationToServer(String refreshedToken)
    {
    }
}