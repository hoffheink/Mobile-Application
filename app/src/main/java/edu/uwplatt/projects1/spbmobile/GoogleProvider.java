package edu.uwplatt.projects1.spbmobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import edu.uwplatt.projects1.spbmobile.Lambda.FirebaseTokenLambdaFormat;
import edu.uwplatt.projects1.spbmobile.Lambda.LambdaFunctionNames;
import edu.uwplatt.projects1.spbmobile.Lambda.LambdaPlatform;

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

    /**
     * Creates an instance of a GoogleProvider object.
     *
     * @param context  the Application Context used to create the GoogleProvider.
     * @param activity the current UI Activity that is being executed.
     * @return the GoogleProvider.
     */
    public static GoogleProvider getInstance(@NonNull Context context, @NonNull Activity activity) {
        if (ourInstance == null || !context.equals(ourContext)) {
            ourContext = context;
            ourInstance = new GoogleProvider(activity);
        }
        return ourInstance;
    }

    /**
     * This constructor creates a GoogleProvider.
     *
     * @param activity the current UI Activity that is being executed.
     */
    private GoogleProvider(@NonNull Activity activity) {
        initializeGoogleClient(activity);
    }

    /**
     * This method will initialize the googleSignInClient.
     *
     * @param activity the current UI Activity that is being executed.
     */
    private void initializeGoogleClient(@NonNull Activity activity) {
        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(ourContext.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        googleSignInClient = GoogleSignIn.getClient(activity, googleSignInOptions);
    }

    /**
     * This method gets the currently authorized Google account that is signed in.
     *
     * @return the currently GoogleSignInAccount.
     */
    public GoogleSignInAccount getAccount() {
        return account;
    }

    /**
     * This method sets the currently logged in Google account.
     *
     * @param account the GoogleSignInAccount that is logged in.
     */
    void setGoogleAccount(GoogleSignInAccount account) {
        if (account != null) {
            SimpleStorageSystem simpleStorageSystem = new SimpleStorageSystem();
            simpleStorageSystem.saveSubArn(ourContext, account, MainActivity.region);

            Gson gson = new Gson();
            CognitoSyncFormat cognitoSyncFormat = new CognitoSyncFormat(account.getEmail());

            LambdaPlatform lambdaPlatform = new LambdaPlatform();
            AsyncTaskResult<String> response = lambdaPlatform.invokeLambdaFunction(LambdaFunctionNames.REMOVE_NOTIFICATION, gson.toJson(cognitoSyncFormat), CloudDatasource.getInstance(ourContext, account, MainActivity.region).getCognitoCachingCredentialsProvider());
            Log.d("CognitoSync: ", response.getResult());
        }
        GoogleProvider.account = account;
    }

    /**
     * This method creates a login intent from Google that the user uses to authorize their account.
     *
     * @return the Intent used for signing in.
     */
    Intent generateSignInIntent() {
        return googleSignInClient.getSignInIntent();
    }

    /**
     * This method is used to sign a user out.
     *
     * @param activity the current UI Activity that is being executed.
     */
    void signOut(final Activity activity) {
        googleSignInClient.signOut().addOnCompleteListener(activity,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory(Intent.CATEGORY_HOME);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        ourContext.startActivity(homeIntent);

                        FragmentManager fragmentManager = activity.getFragmentManager();
                        while (fragmentManager.getBackStackEntryCount() != 0)
                            fragmentManager.popBackStack();
                        Intent openDevicesIntent =
                                new Intent(activity, WelcomeScreenActivity.class);
                        activity.startActivityForResult(openDevicesIntent, RC_WELCOME_SCREEN);
                    }
                });
    }


    /**
     * This method sets the account to the last signed in account.
     */
    void setAccountToLastSignedIn() {
        account = GoogleSignIn.getLastSignedInAccount(ourContext);
    }

    /**
     * This method gets the account's display name.
     *
     * @return the display name of the account.
     */
    String getDisplayName() {
        return account.getDisplayName();
    }

    /**
     * This method gets the account's email.
     *
     * @return the email of the account.
     */
    String getEmail() {
        return account.getEmail();
    }
}