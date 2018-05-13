package edu.uwplatt.projects1.spbmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WelcomeScreenActivity extends AppCompatActivity {
    /*
     * Used to identify the output of the google sign in task.
     */
    private static final int RC_SIGN_IN = 9001;

    /**
     * Initializes the data values used in the activity.
     *
     * @param savedInstanceState the state the system was saved in on the last usage.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleProvider.getInstance(this, this);

        setContentView(R.layout.activity_welcome_screen);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
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
            public void onClick(View view) {
                signIn();
            }
        });
    }

    /**
     * Post creation of current system state.
     *
     * @param savedInstanceState the state the system was saved in on the last usage.
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
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
     * @param resultCode  the result code
     * @param data        the data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            } catch (Exception e) {
                Log.e("onActivityResult", e.getMessage(), e);
            }
        }
    }

    /**
     * Handles the sign in result from teh google sign in activity.
     *
     * @param signInTask the task
     */
    private void handleSignInResult(Task<GoogleSignInAccount> signInTask) {
        try {
            GoogleProvider.getInstance(this, this)
                    .setGoogleAccount(signInTask.getResult(ApiException.class));
            Log.i("handleSignInResult", GoogleProvider.getInstance(this, this)
                    .getAccount().getIdToken());
        } catch (ApiException e) {

            Toast.makeText(this, "Failed to sign in\nPlease try again\nReason:\n" +
                            GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()),
                    Toast.LENGTH_LONG).show();
            Log.e("handleSignInResult", GoogleSignInStatusCodes
                    .getStatusCodeString(e.getStatusCode()), e);
        }
        android.app.FragmentManager fragmentManager = getFragmentManager();
        while (fragmentManager.getBackStackEntryCount() != 0)
            fragmentManager.popBackStack();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void signIn() {
        Intent signInIntent = GoogleProvider.getInstance(this, this)
                .generateSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
