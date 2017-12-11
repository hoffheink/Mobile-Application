package edu.uwplatt.projects1.spbmobile;

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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class RegisterApplianceFragment extends Fragment {

    public static final String NETWORK_PREFIX = "Mongoose";
    public static final String SSID_KEY = "SSID";
    WifiManager wifiManager;
    ArrayList<ScanResult> filteredResults;
    ScanResult selectedNetwork;

    String token;

    int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 5555;
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
        wifiManager.startScan();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

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
                Log.d("wifiConnectReceiver", "Connected to network: " + wifiManager.getConnectionInfo());
                if (wifiManager.getConnectionInfo() != null && selectedNetwork != null && wifiManager.getConnectionInfo().getSSID().equals("\"" + selectedNetwork.SSID + "\"")) {
                    showConfigurationSettings();
                }
            }
        }
    };

    private View.OnClickListener sendNetworkInfoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Spinner networkName = (Spinner) getActivity().findViewById(R.id.network_name);
            TextView networkPassword = (TextView) getActivity().findViewById(R.id.network_password);

            Runnable sendNetworkInfoRunnable = new SendNetworkInfoRunnable(((HashMap<String, String>) networkName.getSelectedItem()).get(SSID_KEY), networkPassword.getText().toString());
            new Thread(sendNetworkInfoRunnable).start();
        }
    };

    private class SendNetworkInfoRunnable implements Runnable {

        private final String networkName;
        private final String networkPassword;

        public SendNetworkInfoRunnable(String networkName, String networkPassword) {
            this.networkName = networkName;
            this.networkPassword = networkPassword;
        }

        @Override
        public void run() {
            sendNetworkInfo(networkName, networkPassword);
        }
    };

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
            Log.d("sendNetworkInfo", "Token is: " + token);

        } catch (MalformedURLException e) {
            Log.e("sendNetworkInfo", e.getMessage());
        } catch (IOException e) {
            Log.e("sendNetworkInfo", e.getMessage());
        }
    }

    private void showConfigurationSettings() {
        getActivity().findViewById(R.id.network_list).setVisibility(View.GONE);
        getActivity().findViewById(R.id.network_name).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.network_password).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.send_network_info_button).setVisibility(View.VISIBLE);

        ((Button) getActivity().findViewById(R.id.send_network_info_button)).setOnClickListener(sendNetworkInfoClickListener);

        ArrayList<HashMap<String, String>> networkList = new ArrayList<>();
        Set<String> usedNames = new HashSet<>();
        for (ScanResult result : unfilteredResults) {
            if(!usedNames.contains(result.SSID)) {
                usedNames.add(result.SSID);
                HashMap item = new HashMap<String, String>();
                item.put(SSID_KEY, result.SSID);
                networkList.add(item);
            }
        }

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), networkList, android.R.layout.simple_list_item_1, new String[]{SSID_KEY}, new int[]{android.R.id.text1});
        ((Spinner) getActivity().findViewById(R.id.network_name)).setAdapter(adapter);


    }

    private void resultsReceived(List<ScanResult> results) {

        getActivity().findViewById(R.id.register_appliance_progress_bar).setVisibility(View.GONE);
        getActivity().findViewById(R.id.network_list).setVisibility(View.VISIBLE);

        unfilteredResults = results;
        filteredResults = new ArrayList<>();

        ArrayList<HashMap<String, String>> filteredList = new ArrayList<>();

        for (ScanResult result : results) {
            if (result.SSID.startsWith(NETWORK_PREFIX)) {
                HashMap item = new HashMap<String, String>();
                item.put(SSID_KEY, result.SSID);
                filteredList.add(item);
                filteredResults.add(result);
            }
        }

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), filteredList, android.R.layout.simple_list_item_1, new String[]{SSID_KEY}, new int[]{android.R.id.text1});
        ((ListView) getActivity().findViewById(R.id.network_list)).setAdapter(adapter);
        ((ListView) getActivity().findViewById(R.id.network_list)).setOnItemClickListener(ssidClickListener);
    }

    AdapterView.OnItemClickListener ssidClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.d("ItemClick", filteredResults.get(i).SSID);
            connectTo(i);

        }
    };

    private void connectTo(int index) {

        selectedNetwork = filteredResults.get(index);

        WifiConfiguration config = new WifiConfiguration();
        config.SSID = selectedNetwork.SSID;
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        int configID = wifiManager.addNetwork(config);

        wifiManager.enableNetwork(configID, true);


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

}