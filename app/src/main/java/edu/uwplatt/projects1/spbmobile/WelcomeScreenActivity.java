package edu.uwplatt.projects1.spbmobile;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WelcomeScreenActivity extends AppCompatActivity
{
    private View mContentView;
    private GoogleProvider mGoogleProvider;


    /**
     * Used when logging an operation occurring in this activity.
     */
    private final String TAG = "WelcomeScreenActivity";


	/*
     * Used to identify the output of the google sign in task.
     */
    private static final int RC_SIGN_IN = 9001;

    /**
     * Initializes the data values used in the activity.
     * @param savedInstanceState the state the system was saved in on the last usage.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mGoogleProvider.getInstance(this, this);

        setContentView(R.layout.activity_welcome_screen);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        View mContentView = findViewById(android.R.id.content);

        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                signIn();
            }
        });
    }

    /**
     * Post creation of current system state.
     * @param savedInstanceState the state the system was saved in on the last usage.
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles return data from the google sign in activity
     *
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data the data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /**
     * Handles the sign in result from teh google sign in activity.
     * @param completedTask the completed task
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try
        {
            mGoogleProvider.getInstance(this, this).setGoogleAccount(completedTask.getResult(ApiException.class));
            Log.d("handleSignInResult", mGoogleProvider.getInstance(this, this).getAccount().getIdToken());
        }
        catch (ApiException e)
        {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            /*
      Used when logging with a Log.d method.
     */
            String TAG = "WelcomeScreenActivity";
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
        finish();
    }

    private void signIn()
    {
        Intent signInIntent = mGoogleProvider.getInstance(this, this).generateSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
