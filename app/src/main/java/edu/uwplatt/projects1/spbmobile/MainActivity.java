package edu.uwplatt.projects1.spbmobile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Timer;
import java.util.TimerTask;

import edu.uwplatt.projects1.spbmobile.Appliance.UIComponents.ApplianceListFragment;
import edu.uwplatt.projects1.spbmobile.Appliance.UIComponents.RegisterApplianceFragment;


/**
 * This class represents the main activity of the application.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static MainActivity ourInstance;
    private static boolean visible;
    private static final int RC_WELCOME_SCREEN = 9002;

    /**
     * Returns the current instance of the running main activity.
     *
     * @return the current instance of the running main activity.
     */
    public static MainActivity getOurInstance() {
        return ourInstance;
    }

    /**
     * This method will set up all the needed components of the MainActivity.
     *
     * @param savedInstanceState the Bundle (if available).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.register_appliance_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        testGooglePlayServicesAvailability();

        NavigationView navigationView;
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        visible = true;
        ourInstance = this;
    }

    /**
     * This method creates and displays the Intent to show the WelcomeScreenActivity.
     */
    private void showWelcomeScreen() {
        Intent openDevicesIntent = new Intent(this, WelcomeScreenActivity.class);
        startActivityForResult(openDevicesIntent, RC_WELCOME_SCREEN);
    }

    /**
     * This method will load up the account information.
     */
    @Override
    public void onStart() {
        visible = true;
        super.onStart();
        updateAccountInformation();
        //new Timer().scheduleAtFixedRate(new UpdateAppliancesTask(this), 0, 5000);
    }


    private class UpdateAppliancesTask extends TimerTask {
        private final Activity activity;

        public UpdateAppliancesTask(final Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            CloudDatasource.getInstance(getApplicationContext(), GoogleProvider.getInstance(getApplicationContext(), activity).getAccount()).loadAppliances(false);
        }
    }

    /**
     * This method will update the account info and make a call to display the WelcomeScreenActivity
     * if needed.
     */
    private void updateAccountInformation() {
        GoogleProvider googleProvider = GoogleProvider.getInstance(getApplicationContext(),
                this);
        googleProvider.setAccountToLastSignedIn();

        if (googleProvider.getAccount() == null)
            showWelcomeScreen();
        else {
            CloudDatasource.getInstance(this, googleProvider.getAccount()).loadAppliances(false); //Loads appliance list
            NavigationView navigationView = findViewById(R.id.nav_view);
            View header = navigationView.getHeaderView(0);
            ((TextView) header.findViewById(R.id.user_name))
                    .setText(googleProvider.getDisplayName());
            ((TextView) header.findViewById(R.id.user_email)).setText(googleProvider.getEmail());
        }
    }

    /**
     * This method is executed when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            getFragmentManager().popBackStack();
        }
    }

    /**
     * This method initializes the contents of the Fragment host's standard options menu.
     *
     * @param menu the Menu that has items in it.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO: Make this menu do something, for now I have just disabled it.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * This method is used to handle action bar item clicks here. The action bar will automatically
     * handle clicks on the Home/Up button, so long as you specify a parent activity in
     * AndroidManifest.xml.
     *
     * @param item the MenuItem selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    /**
     * This method handles navigation view item clicks here.
     *
     * @param item the MenuItem selected.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            //TODO: Remove or implement
        } else if (id == R.id.nav_settings) {
            //TODO: Remove or implement
        } else if (id == R.id.nav_invoke_aws) {
            //TODO: Remove or implement
        } else if (id == R.id.nav_logout) {
            GoogleProvider.getInstance(getApplicationContext(), this).signOut(this);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * This method checks if the appropriate version of Google Play Services is installed, and if
     * not, an error is shown to the user.
     */
    private void testGooglePlayServicesAvailability() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        Integer resultCode = googleApiAvailability
                .isGooglePlayServicesAvailable(this.getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            Dialog dialog = googleApiAvailability.getErrorDialog(this, resultCode, 0);
            if (dialog != null) {
                dialog.show();
            }
        }
    }


    /**
     * This method will handles the activity results.
     *
     * @param requestCode the requestCode.
     * @param resultCode  the resultCode.
     * @param data        the Intent of the request.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_WELCOME_SCREEN && resultCode == RESULT_OK) {
            updateAccountInformation();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        visible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        visible = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        visible = false;
    }

    /**
     * Creates a snackbar object that contains a provided message.
     *
     * @param message the message to be displayed in the snackbar.
     */
    public void createSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.drawer_layout), message,
                Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public boolean isVisible() {
        return visible;
    }
}
