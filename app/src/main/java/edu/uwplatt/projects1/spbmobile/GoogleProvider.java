package edu.uwplatt.projects1.spbmobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.ExecutionException;

/**
 * Maintains data objects and methods for using Google play services as an identity provider.
 */
public class GoogleProvider
{
    private final String TAG = "GoogleProvider";

    @SuppressLint("StaticFieldLeak")
    private static GoogleProvider ourInstance;
    private static Context applicationContext;
    private static GoogleSignInAccount account;
    @SuppressLint("StaticFieldLeak")
    private static GoogleSignInClient googleSignInClient;
    private static GoogleSignInOptions gso;

    /**
     * Creates an instance of a GoogleProvider object.
     * @param context is the Android application context.
     * @param active identity for the current UI activity that is being executed.
     * @return the instance of a GoogleProvider object.
     */
    static GoogleProvider getInstance(Context context, Activity active)
    {
        if(ourInstance == null || context != applicationContext)
        {
            ourInstance = new GoogleProvider(context, active);
            applicationContext = context;
        }
        return  ourInstance;
    }

    /**
     * Default constructor for initializing a GoogleProvider object.
     * @param context is the Android application context.
     * @param active identity for the current UI activity that is being executed.
     */
    private GoogleProvider(Context context, Activity active)
    {
        applicationContext = context;
        googleSignInClient = GoogleSignIn.getClient(active, gso);
        initializeGoogleClient(active);

    }

    /**
     * Initializie Google sign in option and a Googly Api client for logging in.
     * @param active identity for the current UI activity that is being executed.
     */
    private static void initializeGoogleClient(Activity active)
    {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(applicationContext.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(active, gso);
    }

    /**
     * Get currently authorized Google account that is signed in.
     * @return currently authorized Google account that is signed in.
     */
    public static GoogleSignInAccount getAccount()
    {
        return account;
    }

    /**
     * Set the currently logged in Google account.
     * @param account authorized Google account that is loged in.
     */
    void setGoogleAccount(GoogleSignInAccount account)
    {
        GoogleProvider.account = account;
    }

    /**
     * Creates a login intent from Google that the user uses to authorize their account.
     * @return Google login intent.
     */
    Intent generateSignInIntent()
    {
        return googleSignInClient.getSignInIntent();
    }

    /**
     * Reauthenticates a currently logged-in user without prompting the user to login again.
     * @return the reauthorized user Google account.
     * @throws ExecutionException is thrown when the silentLogin was unable to execute an AsyncTask.
     * @throws InterruptedException is thrown when an AsyncTask is interrupted before finishing the
     * login.
     */
    public GoogleSignInAccount silentLogin() throws ExecutionException, InterruptedException
    {
        GoogleProvider.SilentLoginWithGoogle silentLoginWithGoogle = new GoogleProvider.SilentLoginWithGoogle();
        return silentLoginWithGoogle.execute().get();
    }

    /**
     * Signs out of activity
     *
     */
    public static void signOut(Activity activity) {
        // Build a GoogleSignInClient with the options specified by gso.

        googleSignInClient.signOut().addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                applicationContext.startActivity(homeIntent);
            }
        });
    }

    /**
     * Gets the account of the last signed in
     *
     */
    public static void getLastSignedIn(Context context) {
        account = GoogleSignIn.getLastSignedInAccount(context);
    }

    /**
     * Gets the account display name
     *
     */
    public static String getDispName() {
        return account.getDisplayName();
    }

    /**
     * Gets the account email
     *
     */
    public static String getEmail() {
        return account.getEmail();
    }

    /**
     * Asynchronous task that creates a controlled thread to connect to a Google Api
     * and attempt to login again.
     */
    @SuppressLint("StaticFieldLeak")
    private class SilentLoginWithGoogle extends AsyncTask<Void, Void, GoogleSignInAccount>
    {
        /**
         * Executable method that is done while the system attempts a silent login.
         * @param voids does nothing.
         * @return the reauthorized user Google account.
         */
        @Override
        protected GoogleSignInAccount doInBackground(Void... voids)
        {
            try
            {
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(applicationContext, gso);
                return googleSignInClient.silentSignIn().getResult();
            }
            catch (Exception e)
            {
                Log.e("SilentLoginWithGoogle", "Error with silent login", e);
                return null;
            }
        }
    }
}