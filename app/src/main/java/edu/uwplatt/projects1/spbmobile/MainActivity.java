package edu.uwplatt.projects1.spbmobile;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ApplianceListFragment.OnFragmentInteractionListener {
    protected DrawerLayout mDrawer;
    public static GoogleSignInAccount account;
    private static final int RC_WELCOME_SCREEN = 9002;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private final String jsonRequestParameters = "{\"thingId\":\"charlieDevice1\",\"thingPin\":\"5000\"}";
    private final String serverClientId = "968907067223-pi8qejnm2obnpv914up57b178qrv58nf.apps.googleusercontent.com";
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.register_appliance_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        isGooglePlayServicesAvailable();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mDrawer = findViewById(R.id.drawer_layout);


    }

    /**
     * Creates an intent to show the welcome screen.
     */
    private void showWelcomeScreen() {
        Intent openDevicesIntent = new Intent(this, WelcomeScreenActivity.class);
        startActivityForResult(openDevicesIntent, RC_WELCOME_SCREEN);
    }

    @Override
    public void onStart() {
        super.onStart();

        updateAccountInformation();
    }

    private void updateAccountInformation() {
        account = GoogleSignIn.getLastSignedInAccount(this);

        if(account == null)
            showWelcomeScreen();
        else {
            NavigationView navigationView = findViewById(R.id.nav_view);
            View header = navigationView.getHeaderView(0);
            ((TextView)header.findViewById(R.id.user_name)).setText(account.getDisplayName());
            ((TextView)header.findViewById(R.id.user_email)).setText(account.getEmail());
            credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    "us-east-2:1641195a-2e43-4f91-bca0-5e8e6edd6878", // Identity pool ID
                    Regions.US_EAST_2 // Region
            );

            HashMap<String, String> logins = new HashMap<>();

            String accountID = account.getIdToken();
            Log.d("onCreate", "accountID: " + accountID);
            logins.put("accounts.google.com", accountID);
            credentialsProvider.setLogins(logins);


            task.execute(credentialsProvider);


            try {
                task2.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
    }

    private AsyncTask<CognitoCachingCredentialsProvider, Void, CognitoCachingCredentialsProvider> task =  new AsyncTask<CognitoCachingCredentialsProvider, Void, CognitoCachingCredentialsProvider>() {
        @Override
        protected CognitoCachingCredentialsProvider doInBackground(CognitoCachingCredentialsProvider... voids) {
            credentialsProvider.refresh();
            return credentialsProvider;
        }
    };

    private AsyncTask<Void, Void, String> task2 = new AsyncTask<Void, Void, String>() {
        @Override
        protected String doInBackground(Void... voids) {
            AWSLambdaClient client = (credentialsProvider == null) ? new AWSLambdaClient()
                    : new AWSLambdaClient(credentialsProvider);
            client.setRegion(Region.getRegion(Regions.US_EAST_2));
            try {
                InvokeRequest invokeRequest = new InvokeRequest();
                invokeRequest.setFunctionName("arn:aws:lambda:us-east-2:955967187114:function:iot-app-register-device");
                invokeRequest.setPayload(ByteBuffer.wrap(jsonRequestParameters.getBytes()));
                ByteBuffer b = client.invoke(invokeRequest).getPayload();
                String response = byteBufferToString(b, Charset.forName("UTF-8"));
                Log.e("Tag", response, null);
                return response;
            } catch (Exception e) {
                Log.e("Tag", "Failed to invoke nick", e);
                return null;
            }
        }
    };

    public static String byteBufferToString(ByteBuffer buffer, Charset charset) {
        byte[] bytes;
        if (buffer.hasArray()) {
            bytes = buffer.array();
        } else {
            bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
        }
        return new String(bytes, charset);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if (id == R.id.nav_appliances) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            ApplianceListFragment fragment = new ApplianceListFragment();
            fragmentTransaction.add(R.id.content_main, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_register_appliance) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            RegisterApplianceFragment fragment = new RegisterApplianceFragment();
            fragmentTransaction.add(R.id.content_main, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_account) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_invoke_aws) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.sign_in_button_main:
//                signIn();
//                break;
//        }
    }

    /**
     * Checks if the appropriate version of google play services is installed.
     *
     * If not an error is shown to the user.
     * @return
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        Integer resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this.getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            Dialog dialog = googleApiAvailability.getErrorDialog(this, resultCode, 0);
            if (dialog != null) {
                dialog.show();
            }
            return false;
        }
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_WELCOME_SCREEN && resultCode == RESULT_OK) {
            updateAccountInformation();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
