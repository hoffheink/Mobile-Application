package edu.uwplatt.projects1.spbmobile.Appliance.UIComponents;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import edu.uwplatt.projects1.spbmobile.Appliance.Appliance;
import edu.uwplatt.projects1.spbmobile.AsyncTaskResult;
import edu.uwplatt.projects1.spbmobile.CloudDatasource;
import edu.uwplatt.projects1.spbmobile.GoogleProvider;
import edu.uwplatt.projects1.spbmobile.Lambda.LambdaFunctionNames;
import edu.uwplatt.projects1.spbmobile.MainActivity;
import edu.uwplatt.projects1.spbmobile.R;

/**
 * This class is used to register an Appliance with the cloud side.
 */
public class RegisterApplianceFragment extends Fragment {

    final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 5555;
    public static final String NETWORK_PREFIX = "Mon";
    public static final String SSID_KEY = "SSID";
    private static String thingName;
    private List<ScanResult> unfilteredResults;
    WifiManager wifiManager;
    ArrayList<ScanResult> filteredResults;
    ScanResult selectedNetwork;
    Appliance appliance = null;
    String token;
    Boolean config = false;

    /**
     * This method will create fragment for the registering of an appliance.
     *
     * @param inflater           the LayoutInflater used to inflate the view.
     * @param container          the ViewGroup to throw the fragment in.
     * @param savedInstanceState the Bundle (if available).
     * @return The View after inflation.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container != null) {
            container.removeAllViews();
        }
        return inflater.inflate(R.layout.content_register_appliance, container, false);
    }

    /**
     * This method will start scanning WiFi and get permissions if needed.
     *
     * @param savedInstanceState the Bundle (if available).
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiManager = (WifiManager) getActivity().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            wifiManager.startScan();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity()
                .checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }
    }

    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        /**
         * This method will make a call to update the WiFi list.
         *
         * @param context the Context.
         * @param intent  the Intent.
         */
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (wifiManager != null) {
                List<ScanResult> results = wifiManager.getScanResults();
                resultsReceived(results);
            }
        }
    };

    BroadcastReceiver wifiConnectReceiver = new BroadcastReceiver() {
        /**
         * This method will make a call to show config settings for the new Appliance.
         *
         * @param context the Context.
         * @param intent  the Intent.
         */
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (wifiManager != null) {
                Log.i("wifiConnectReceiver", "Connected to network: " +
                        wifiManager.getConnectionInfo());
                if (wifiManager.getConnectionInfo() != null
                        && selectedNetwork != null
                        && wifiManager.getConnectionInfo().getSSID()
                        .equals("\"" + selectedNetwork.SSID + "\"")) {
                    showConfigurationSettings();
                }
            }
        }
    };

    private View.OnClickListener sendNetworkInfoClickListener = new View.OnClickListener() {
        /**
         * This method will initiate the registering of a Appliance.
         *
         * @param view the View clicked.
         */
        @Override
        public void onClick(View view) {
            Spinner networkName = getActivity().findViewById(R.id.network_name);
            TextView networkPassword = getActivity().findViewById(R.id.network_password);
            if (networkName != null && networkName.getSelectedItem() != null) {
                String ssid = getSSID(networkName);
                Runnable sendNetworkInfoRunnable = new SendNetworkInfoAndRegisterRunnable(ssid,
                        networkPassword.getText().toString(),
                        GoogleProvider.getInstance(getContext(), getActivity()).getAccount());
                new Thread(sendNetworkInfoRunnable).start();
            }
        }
    };

    /**
     * This method will get the SSID from the network name.
     *
     * @param networkName the Spinner.
     * @return the SSID.
     */
    @SuppressWarnings("all")
    private String getSSID(Spinner networkName) {
        return ((HashMap<String, String>) networkName.getSelectedItem()).get(SSID_KEY);
    }

    /**
     * This class is used to register the Appliance.
     */
    private class SendNetworkInfoAndRegisterRunnable implements Runnable {
        private final String networkName;
        private final String networkPassword;
        private final GoogleSignInAccount account;

        /**
         * This constructor will create the runnable.
         *
         * @param networkName     the network name.
         * @param networkPassword the network password.
         * @param account         the GoogleSignInAccount to register the Appliance with.
         */
        SendNetworkInfoAndRegisterRunnable(String networkName, String networkPassword,
                                           GoogleSignInAccount account) {
            this.networkName = networkName;
            this.networkPassword = networkPassword;
            this.account = account;
        }

        /**
         * This method will actually call the method to register the Appliance.
         */
        @Override
        public void run() {
            URL applianceURL;
            try {
                applianceURL = new URL("http://192.168.4.1/setup");
                URLConnection connection = applianceURL.openConnection();
                connection.setRequestProperty("SSID", networkName);
                connection.setRequestProperty("PASS", networkPassword);
                connection.connect();
                Scanner inputScanner = new Scanner(connection.getInputStream());
                token = inputScanner.next();
                Log.i("sendNetworkInfoRegister", "Token is: " + token);

                RegisterDeviceWithAWS registrationTask = new RegisterDeviceWithAWS(account,
                        thingName, token, getContext());

                for (int count = 0; appliance == null && count < 10; count++) {
                    registrationTask.run();
                    Thread.sleep(5000);
                }
                if (appliance == null) {
                    Log.w("sendNetworkInfoRegister", "Failed to add device!");
                    Toast.makeText(getContext(), "Failed to add device!",
                            Toast.LENGTH_LONG).show();
                } else {
                    goHome();
                }
            } catch (Exception e) {
                Log.e("sendNetworkInfoRegister", "Error:" + e.getMessage(), e);
            }
        }
    }

    /**
     * This class is used to register the device with AWS.
     */
    public class RegisterDeviceWithAWS implements Runnable {
        private final String token;
        private final String applianceName;
        private final GoogleSignInAccount account;
        private final Context context;

        /**
         * This constructor will create the runnable.
         *
         * @param account       the GoogleSignInAccount used to register the Appliance.
         * @param applianceName the Appliance name.
         * @param token         the token used to register the Appliance.
         * @param context       the Application Context.
         */
        //TODO: Refactor this so that it takes in a CloudDatasource rather than creating one every time.
        RegisterDeviceWithAWS(GoogleSignInAccount account, String applianceName, String token,
                              Context context) {
            this.account = account;
            this.applianceName = applianceName;
            this.token = token;
            this.context = context;
        }

        /**
         * This method actually registers the device with AWS.
         */
        @Override
        public void run() {
            try {
                Gson gson = new Gson();
                RegisterDeviceFormat registerDeviceFormat = new RegisterDeviceFormat(applianceName, token, FirebaseInstanceId.getInstance().getToken(), CloudDatasource.getInstance(context, account, MainActivity.region).getSubscriptionArn());
                Log.i("RegisterDeviceWithAWS", "jsonRequestParameters: " + gson.toJson(registerDeviceFormat));
                AsyncTaskResult<String> response = CloudDatasource.getInstance(context, account, MainActivity.region).invokeLambda(LambdaFunctionNames.REGISTER_DEVICE, gson.toJson(registerDeviceFormat));
                String result = response.getResult();
                if (result != null) {
                    if (!result.contains("errorMessage"))
                        appliance = new Appliance(applianceName, applianceName);
                } else
                    Log.w("RegisterDeviceWithAWS", "Failed to register");
                Log.i("RegisterDeviceWithAWS", "response: " + result);
            } catch (Exception e) {
                Log.e("RegisterDeviceWithAWS", e.getMessage(), e);
            }
        }
    }

    /**
     * This method will show the configuration settings.
     */
    private void showConfigurationSettings() {
        if (!config) {
            config = true;
            getActivity().findViewById(R.id.network_list).setVisibility(View.GONE);
            getActivity().findViewById(R.id.network_name).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.network_password).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.send_network_info_button).setVisibility(View.VISIBLE);

            (getActivity().findViewById(R.id.send_network_info_button))
                    .setOnClickListener(sendNetworkInfoClickListener);

            ArrayList<HashMap<String, String>> networkList = new ArrayList<>();
            Set<String> usedNames = new HashSet<>();
            for (ScanResult result : unfilteredResults) {
                if (!usedNames.contains(result.SSID)) {
                    usedNames.add(result.SSID);
                    HashMap<String, String> item = new HashMap<>();
                    item.put(SSID_KEY, result.SSID);
                    networkList.add(item);
                }
            }

            SimpleAdapter adapter = new SimpleAdapter(getActivity(), networkList,
                    android.R.layout.simple_list_item_1, new String[]{SSID_KEY},
                    new int[]{android.R.id.text1});
            ((Spinner) getActivity().findViewById(R.id.network_name)).setAdapter(adapter);
        }
    }

    /**
     * This method will bring the view all the way home.
     */
    private void goHome() {
        FragmentManager fragmentManager = this.getFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ApplianceListFragment fragment = new ApplianceListFragment();
        fragmentTransaction.add(R.id.content_main, fragment);
        fragmentTransaction.commit();
    }

    /**
     * This method will show all the esp devices so that they can be registered.
     *
     * @param results the list of ScanResults.
     */
    private void resultsReceived(List<ScanResult> results) {
        if (!config) {
            ProgressBar progressBar = getActivity()
                    .findViewById(R.id.register_appliance_progress_bar);
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
            ListView networkListView = getActivity().findViewById(R.id.network_list);
            if (networkListView != null)
                networkListView.setVisibility(View.VISIBLE);

            unfilteredResults = results;
            filteredResults = new ArrayList<>();

            ArrayList<HashMap<String, String>> filteredList = new ArrayList<>();
            for (ScanResult result : results) {
                if (result.SSID.startsWith(NETWORK_PREFIX)) {
                    HashMap<String, String> item = new HashMap<>();
                    item.put(SSID_KEY, result.SSID);
                    filteredList.add(item);
                    filteredResults.add(result);
                }
            }

            SimpleAdapter adapter = new SimpleAdapter(getActivity(), filteredList,
                    android.R.layout.simple_list_item_1, new String[]{SSID_KEY},
                    new int[]{android.R.id.text1});
            if (networkListView != null) {
                networkListView.setAdapter(adapter);
                networkListView.setOnItemClickListener(ssidClickListener);
            }
        }
    }

    AdapterView.OnItemClickListener ssidClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.i("ItemClick", filteredResults.get(i).SSID);
            connectTo(i);

        }
    };

    /**
     * This method will connect the android device to the wifi network at the passed in index.
     *
     * @param index the index of the selected wifi network.
     */
    private void connectTo(int index) {
        selectedNetwork = filteredResults.get(index);
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = selectedNetwork.SSID;
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        int configID = wifiManager.addNetwork(config);
        Log.i("connectTo", "configID: " + configID);
        wifiManager.enableNetwork(configID, true);
        thingName = "esp8266_" + selectedNetwork.SSID.substring(selectedNetwork.SSID.length() - 6);
        Log.i("connectTo", "thingName: " + thingName);
    }

    /**
     * This method will prevent our code from getting called if the app is not the main activity.
     */
    public void onPause() {
        getActivity().unregisterReceiver(wifiScanReceiver);
        getActivity().unregisterReceiver(wifiConnectReceiver);
        super.onPause();
    }

    /**
     * This method will allow our code to get called if the app becomes the main activity.
     */
    public void onResume() {
        getActivity().registerReceiver(
                wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        getActivity().registerReceiver(wifiConnectReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        super.onResume();
    }
}