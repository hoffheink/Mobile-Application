package edu.uwplatt.projects1.spbmobile;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import edu.uwplatt.projects1.spbmobile.Appliance.Appliance;
import edu.uwplatt.projects1.spbmobile.Appliance.UIComponents.ApplianceListFragment;
import edu.uwplatt.projects1.spbmobile.Appliance.UIComponents.RegisterApplianceFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener
{
    private static MainActivity ourInstance;
    private static boolean visible;
    private static final int RC_WELCOME_SCREEN = 9002;
    public static final CloudDatasource.RegionEnum region = CloudDatasource.RegionEnum.US_EAST_1;

    /**
     * Returns the current instance of the running main activity.
     * @return the current instance of the running main activity.
     */
    public static MainActivity getOurInstance()
    {
        return ourInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.register_appliance_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
     * Creates an intent to show the welcome screen.
     */
    private void showWelcomeScreen() {
        Intent openDevicesIntent = new Intent(this, WelcomeScreenActivity.class);
        startActivityForResult(openDevicesIntent, RC_WELCOME_SCREEN);
    }

    @Override
    public void onStart()
    {
        visible = true;
        super.onStart();

        Appliance.setVersionNumber(getString(R.string.appVersion));
        updateAccountInformation();
        Appliance nick = new Appliance("nick", "123456");

        int i = 7;
    }

    private void updateAccountInformation()
    {
        GoogleProvider.setAccountToLastSignedIn(this);
        if (GoogleProvider.getAccount() == null)
            showWelcomeScreen();
        else
            {
            CloudDatasource.getInstance(this, GoogleProvider.getAccount(), region).loadAppliances(); //Loads appliance list
            NavigationView navigationView = findViewById(R.id.nav_view);
            View header = navigationView.getHeaderView(0);
            ((TextView) header.findViewById(R.id.user_name)).setText(GoogleProvider.getDispName());
            ((TextView) header.findViewById(R.id.user_email)).setText(GoogleProvider.getEmail());
        }

        SimpleStorageSystem simpleStorageSystem = new SimpleStorageSystem();
        simpleStorageSystem.saveSubArn(getApplicationContext(), GoogleProvider.getAccount(), region);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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

        } else if (id == R.id.nav_logout) {
            GoogleProvider.signOut(this);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) { }

    /**
     * Checks if the appropriate version of google play services is installed.
     * <p>
     * If not an error is shown to the user.
     */
    private void testGooglePlayServicesAvailability() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        Integer resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this.getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            Dialog dialog = googleApiAvailability.getErrorDialog(this, resultCode, 0);
            if (dialog != null) {
                dialog.show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RC_WELCOME_SCREEN && resultCode == RESULT_OK)
        {
            updateAccountInformation();
        }
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        visible = true;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        visible = false;
    }

    @Override
    protected void  onStop()
    {
        super.onStop();
        visible = false;
    }
    /**
     * Creates a snackbar object that contains a provided message.
     * @param message the message to be displayed in the snackbar.
     */
    public void createSnackbar(String message)
    {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.drawer_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public boolean isVisible()
    {
        return visible;
    }
}
