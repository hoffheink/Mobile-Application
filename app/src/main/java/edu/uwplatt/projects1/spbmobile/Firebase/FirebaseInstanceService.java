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
        Log.d(TAG, "Refreshed token: " + refreshedToken);
    }
}