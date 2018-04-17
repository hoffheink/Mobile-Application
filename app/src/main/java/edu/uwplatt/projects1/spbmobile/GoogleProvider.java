package edu.uwplatt.projects1.spbmobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Maintains data objects and methods for using Google play services as an identity provider.
 */
public class GoogleProvider {
    private static final int RC_WELCOME_SCREEN = 9002;
    @SuppressLint("StaticFieldLeak")
    private static GoogleProvider ourInstance;
    @SuppressLint("StaticFieldLeak")
    private static Context ourContext;
    private static GoogleSignInAccount account;
    @SuppressLint("StaticFieldLeak")
    private static GoogleSignInClient googleSignInClient;
    @SuppressLint("StaticFieldLeak")
    private static Activity ourActivity;

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
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
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

    private OnCompleteListener<Void> signOutOnCompleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ourContext.startActivity(homeIntent);


            FragmentManager fragmentManager = ourActivity.getFragmentManager();
            while (fragmentManager.getBackStackEntryCount() != 0)
                fragmentManager.popBackStack();
            Intent openDevicesIntent = new Intent(ourActivity, WelcomeScreenActivity.class);
            ourActivity.startActivityForResult(openDevicesIntent, RC_WELCOME_SCREEN);
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
}