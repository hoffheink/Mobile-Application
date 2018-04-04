package edu.uwplatt.projects1.spbmobile.Firebase;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Base class to handle Firebase instance ID token refresh events.
 */
public class FirebaseInstanceService extends FirebaseInstanceIdService
{
    // Get import class identifier
    private final static String TAG = FirebaseInstanceService.class.getCanonicalName();

    /**
     * Fired upon token refresh.
     */
    @Override
    public void onTokenRefresh()
    {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    // Optional
    private void sendRegistrationToServer(String refreshedToken) {}
}
