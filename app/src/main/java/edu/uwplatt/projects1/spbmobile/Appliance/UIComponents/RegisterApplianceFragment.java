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
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.UpdateThingRequest;
import com.amazonaws.services.iotdata.AWSIotData;
import com.amazonaws.services.iotdata.AWSIotDataClient;
import com.amazonaws.services.iotdata.model.UpdateThingShadowRequest;
import com.amazonaws.services.iotdata.model.UpdateThingShadowResult;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import static edu.uwplatt.projects1.spbmobile.CloudDatasource.RegionEnum.US_EAST_1;
import static edu.uwplatt.projects1.spbmobile.CloudDatasource.RegionEnum.US_EAST_2;
import static edu.uwplatt.projects1.spbmobile.MainActivity.region;

import edu.uwplatt.projects1.spbmobile.Appliance.Appliance;
import edu.uwplatt.projects1.spbmobile.CloudDatasource;
import edu.uwplatt.projects1.spbmobile.MainActivity;
import edu.uwplatt.projects1.spbmobile.R;

public class RegisterApplianceFragment extends Fragment {

    public static final String NETWORK_PREFIX = "Mon";
    public static final String SSID_KEY = "SSID";
    WifiManager wifiManager;
    ArrayList<ScanResult> filteredResults;
    ScanResult selectedNetwork;
    Appliance appliance = null;
    String token;

    Boolean config = false;

    final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 5555;
    private List<ScanResult> unfilteredResults;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container != null) {
            container.removeAllViews();
        }
        return inflater.inflate(R.layout.content_register_appliance, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            wifiManager.startScan();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overridden method
        }
    }

    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
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
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (wifiManager != null) {
                Log.i("wifiConnectReceiver", "Connected to network: " + wifiManager.getConnectionInfo());
                if (wifiManager.getConnectionInfo() != null && selectedNetwork != null && wifiManager.getConnectionInfo().getSSID().equals("\"" + selectedNetwork.SSID + "\"")) {
                    showConfigurationSettings();
                }
            }
        }
    };

    private View.OnClickListener sendNetworkInfoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Spinner networkName = getActivity().findViewById(R.id.network_name);
            TextView networkPassword = getActivity().findViewById(R.id.network_password);
            if (networkName != null && networkName.getSelectedItem() != null) {
                String ssid = getSSID(networkName);
                Runnable sendNetworkInfoRunnable = new SendNetworkInfoRunnable(ssid, networkPassword.getText().toString());
                new Thread(sendNetworkInfoRunnable).start();
            }
        }
    };

    @SuppressWarnings("all")
    private String getSSID(Spinner networkName) {
        return ((HashMap<String, String>) networkName.getSelectedItem()).get(SSID_KEY);
    }

    private class SendNetworkInfoRunnable implements Runnable {
        private final String networkName;
        private final String networkPassword;

        SendNetworkInfoRunnable(String networkName, String networkPassword) {
            this.networkName = networkName;
            this.networkPassword = networkPassword;
        }

        @Override
        public void run() {
            sendNetworkInfo(networkName, networkPassword);
            if (appliance != null) {
                CloudDatasource.getInstance(getContext(), MainActivity.account, region).loadAppliances(); //Reloading the appliance list
            }
        }
    }

    private void sendNetworkInfo(String networkName, String networkPassword) {
        URL applianceURL;
        try {
            applianceURL = new URL("http://192.168.4.1/setup");

            URLConnection connection = applianceURL.openConnection();
            connection.setRequestProperty("SSID", networkName);
            connection.setRequestProperty("PASS", networkPassword);

            connection.connect();

            Scanner inputScanner = new Scanner(connection.getInputStream());
            token = inputScanner.next();
            Log.i("sendNetworkInfo", "Token is: " + token);
            //TODO: Fix this damn name!
            RegisterDeviceWithAWS registrationTask = new RegisterDeviceWithAWS(MainActivity.account, thingName, token, getContext());

            for (int count = 0; appliance == null && count < 10; count++) {
                registrationTask.run();
                Thread.sleep(5000);
            }
            if (appliance == null) {
                Log.w("sendNetworkInfo", "Failed to add device!");
                Toast.makeText(getContext(), "Failed to add device!", Toast.LENGTH_LONG).show();
            } else {
                goHome();
            }
        } catch (IOException e) {
            Log.e("sendNetworkInfo", "IOException: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Log.e("sendNetworkInfo", "InterruptedException: " + e.getMessage(), e);
        }
    }

    public class RegisterDeviceWithAWS implements Runnable {
        private final String token;
        private final String deviceName;
        private final GoogleSignInAccount account;
        private final Context context;

        RegisterDeviceWithAWS(GoogleSignInAccount inAccount, String inDeviceName, String inToken, Context inContext) {
            account = inAccount;
            deviceName = inDeviceName;
            token = inToken;
            context = inContext;
        }

        @Override
        public void run() {
            try {
                InvokeRequest invokeRequest = new InvokeRequest();
                invokeRequest.setFunctionName("arn:aws:lambda:us-east-1:955967187114:function:iot-app-register-device");
                String jsonRequestParameters = "{\"thingId\":\"" + deviceName + "\",\"thingPin\":\"" + token + "\"}";
                Log.i("RegisterDeviceWithAWS", "jsonRequestParameters: " + jsonRequestParameters);
                invokeRequest.setPayload(ByteBuffer.wrap(jsonRequestParameters.getBytes()));

                String response = CloudDatasource.getInstance(context, account, region).invoke(account, invokeRequest);
                Log.i("RegisterDeviceWithAWS", "response: " + response);

                if (response != null) {
                    if (!response.contains("errorMessage")) {
                        appliance = new Appliance(deviceName, deviceName);
                    }
                } else
                    Log.w("RegisterDeviceWithAWS", "Failed to register:");
            } catch (Exception e) {
                Log.e("RegisterDeviceWithAWS", e.getMessage(), e);
            }

            CloudDatasource.getInstance(getActivity(), account, US_EAST_2).shadowUpdate();
        }
    }

    private void showConfigurationSettings() {
        if (!config) {
            config = true;
            getActivity().findViewById(R.id.network_list).setVisibility(View.GONE);
            getActivity().findViewById(R.id.network_name).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.network_password).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.send_network_info_button).setVisibility(View.VISIBLE);

            (getActivity().findViewById(R.id.send_network_info_button)).setOnClickListener(sendNetworkInfoClickListener);

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

            SimpleAdapter adapter = new SimpleAdapter(getActivity(), networkList, android.R.layout.simple_list_item_1, new String[]{SSID_KEY}, new int[]{android.R.id.text1});
            ((Spinner) getActivity().findViewById(R.id.network_name)).setAdapter(adapter);
        }
    }

    private void goHome() {
        FragmentManager fragmentManager = this.getFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ApplianceListFragment fragment = new ApplianceListFragment();
        fragmentTransaction.add(R.id.content_main, fragment);
        fragmentTransaction.commit();
    }

    private void resultsReceived(List<ScanResult> results) {
        if (!config) {
            ProgressBar progressBar = getActivity().findViewById(R.id.register_appliance_progress_bar);
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

            SimpleAdapter adapter = new SimpleAdapter(getActivity(), filteredList, android.R.layout.simple_list_item_1, new String[]{SSID_KEY}, new int[]{android.R.id.text1});
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

    private static String thingName;

    private void connectTo(int index) {
        selectedNetwork = filteredResults.get(index);
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = selectedNetwork.SSID;
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        int configID = wifiManager.addNetwork(config);
        Log.i("connectTo", "configID: " + configID);
        //TODO: Log errors so the user can see them
        wifiManager.enableNetwork(configID, true);
        thingName = "esp8266_" + selectedNetwork.SSID.substring(selectedNetwork.SSID.length() - 6);
        Log.i("connectTo", "thingName: " + thingName);
    }

    public void onPause() {
        getActivity().unregisterReceiver(wifiScanReceiver);
        getActivity().unregisterReceiver(wifiConnectReceiver);
        super.onPause();
    }

    public void onResume() {
        getActivity().registerReceiver(
                wifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        );

        getActivity().registerReceiver(wifiConnectReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        super.onResume();
    }

    @Override
    @SuppressWarnings("all")
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}