package edu.uwplatt.projects1.spbmobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

import edu.uwplatt.projects1.spbmobile.Appliance.UIComponents.ApplianceListFragment;

/**
 * Maintains data objects and methods for using Google play services as an identity provider.
 */
public class GoogleProvider {
    private final String TAG = "GoogleProvider";

    @SuppressLint("StaticFieldLeak")
    private static GoogleProvider ourInstance;
    private static Context ourContext;
    private static GoogleSignInAccount account;
    @SuppressLint("StaticFieldLeak")
    private static GoogleSignInClient googleSignInClient;
    private static GoogleSignInOptions gso;
    private static Activity ourActivity;
    //private OnCompleteListener<Void> signOutOnCompleteListener;

    /**
     * Creates an instance of a GoogleProvider object.
     *
     * @param context  is the Android application context.
     * @param activity identity for the current UI activity that is being executed.
     * @return the instance of a GoogleProvider object.
     */
    public static GoogleProvider getInstance(Context context, Activity activity) {
        if (ourInstance == null || context != ourContext) {
            ourActivity = activity;
            ourContext = context;
            ourInstance = new GoogleProvider();
        }
        return ourInstance;
    }

    /**
     * Default constructor for initializing a GoogleProvider object.
     */
    private GoogleProvider() {
        initializeGoogleClient();
    }

    /**
     * Initializie Google sign in option and a Googly Api client for logging in.
     */
    private void initializeGoogleClient() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(ourContext.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(ourActivity, gso);
    }

    /**
     * Get currently authorized Google account that is signed in.
     *
     * @return currently authorized Google account that is signed in.
     */
    public GoogleSignInAccount getAccount() {
        return account;
    }

    /**
     * Set the currently logged in Google account.
     *
     * @param account authorized Google account that is loged in.
     */
    void setGoogleAccount(GoogleSignInAccount account) {
        GoogleProvider.account = account;
    }

    /**
     * Creates a login intent from Google that the user uses to authorize their account.
     *
     * @return Google login intent.
     */
    Intent generateSignInIntent() {
        return googleSignInClient.getSignInIntent();
    }

    /**
     * Reauthenticates a currently logged-in user without prompting the user to login again.
     *
     * @return the reauthorized user Google account.
     * @throws ExecutionException   is thrown when the silentLogin was unable to execute an AsyncTask.
     * @throws InterruptedException is thrown when an AsyncTask is interrupted before finishing the
     *                              login.
     */
    public GoogleSignInAccount silentLogin() throws ExecutionException, InterruptedException {
        GoogleProvider.SilentLoginWithGoogle silentLoginWithGoogle = new GoogleProvider.SilentLoginWithGoogle();
        return silentLoginWithGoogle.execute().get();
    }

    OnCompleteListener signOutOnCompleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ourContext.startActivity(homeIntent);


            FragmentManager fragmentManager = ourActivity.getFragmentManager();
            while (fragmentManager.getBackStackEntryCount() != 0)
                fragmentManager.popBackStack();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ApplianceListFragment fragment = new ApplianceListFragment();
            //Get to welcome screen
            fragmentTransaction.add(R.id., fragment);
            fragmentTransaction.commit();
        }
    };

    /**
     * Signs out of activity
     */
    void signOut() {
        googleSignInClient.signOut().addOnCompleteListener(ourActivity, signOutOnCompleteListener);
    }


    /**
     * Sets the account to the last signed in
     */
    void setAccountToLastSignedIn() {
        account = GoogleSignIn.getLastSignedInAccount(ourContext);
    }

    /**
     * Gets the account display name
     */
    String getDisplayName() {
        return account.getDisplayName();
    }

    /**
     * Gets the account email
     */
    String getEmail() {
        return account.getEmail();
    }

    /**
     * Asynchronous task that creates a controlled thread to connect to a Google Api
     * and attempt to login again.
     */
    @SuppressLint("StaticFieldLeak")
    private class SilentLoginWithGoogle extends AsyncTask<Void, Void, GoogleSignInAccount> {
        /**
         * Executable method that is done while the system attempts a silent login.
         *
         * @param voids does nothing.
         * @return the reauthorized user Google account.
         */
        @Override
        protected GoogleSignInAccount doInBackground(Void... voids) {
            try {
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(ourContext, gso);
                return googleSignInClient.silentSignIn().getResult();
            } catch (Exception e) {
                Log.e("SilentLoginWithGoogle", "Error with silent login", e);
                return null;
            }
        }
    }
}