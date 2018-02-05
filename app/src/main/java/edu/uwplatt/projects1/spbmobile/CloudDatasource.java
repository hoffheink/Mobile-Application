package edu.uwplatt.projects1.spbmobile;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.ListPrincipalPoliciesRequest;
import com.amazonaws.services.iot.model.ListThingsRequest;
import com.amazonaws.services.iot.model.Policy;
import com.amazonaws.services.iot.model.ThingAttribute;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by dowster on 12/9/2017.
 */

class CloudDatasource {
    private static CloudDatasource ourInstance;
    private static Context ourContext;
    @NonNull
    private CognitoCachingCredentialsProvider credentialsProvider;


    public static List<Appliance> applianceList = new ArrayList<>();

    static CloudDatasource getInstance(@NonNull Context inContext, @NonNull GoogleSignInAccount account) {
        if (ourInstance == null || !ourContext.equals(inContext)) {
            ourInstance = new CloudDatasource(inContext);
            ourContext = inContext;
        }
        addLoginsFromAccount(account);
        LoadCredentialsTask loadCredentialsTask = new LoadCredentialsTask();
        try
        {
            loadCredentialsTask.execute(ourInstance.credentialsProvider);
        }
        catch (Exception e)
        {
            Log.e("getInstance", "Unable to load credentials", e);
        }
        return ourInstance;
    }

    public void loadAppliances() {
        Runnable getAppliancesRunnable = new GetAppliancesRunnable();
        Thread thread = new Thread(getAppliancesRunnable);
        thread.start();
    }

    private CloudDatasource(@NonNull Context inContext) {
        credentialsProvider = new CognitoCachingCredentialsProvider(
                inContext,
                "us-east-2:1641195a-2e43-4f91-bca0-5e8e6edd6878", // Identity pool ID
                Regions.US_EAST_2 // Region
        );
    }

    private static class GetAppliancesRunnable implements Runnable {
        @Override
        public void run() {
            ArrayList<Appliance> newApplianceList = new ArrayList<>();
            AWSSessionCredentials credentials = ourInstance.credentialsProvider.getCredentials();
            if (ourInstance.credentialsProvider != null && credentials != null) {
                AWSIot awsIot = new AWSIotClient(ourInstance.credentialsProvider);
                awsIot.setRegion(Region.getRegion(Regions.US_EAST_2));
                ListPrincipalPoliciesRequest listPrincipalPoliciesRequest = new ListPrincipalPoliciesRequest();
                listPrincipalPoliciesRequest.setPrincipal(ourInstance.credentialsProvider.getIdentityId());
                Log.d("cognitoIDID", ourInstance.credentialsProvider.getIdentityId());

                ListThingsRequest listThingsRequest = new ListThingsRequest();
                listThingsRequest.setRequestCredentials(credentials);

                try {
                    List<String> thingNames = new ArrayList<>();
                    for (Policy policy : awsIot.listPrincipalPolicies(listPrincipalPoliciesRequest).getPolicies()) {
                        String policyName =  policy.getPolicyName().replace("app-", "");
                        thingNames.add(policyName);
                    }
                    for (ThingAttribute o : awsIot.listThings(listThingsRequest).getThings()) {
                        if (thingNames.contains(o.getThingName()))
                        {
                            Appliance appliance = new Appliance(o.getThingName(), o.getVersion().toString());
                            newApplianceList.add(appliance);
                        }
                    }
                } catch (Exception e) {
                    Log.d("GetAppliancesRunnable", e.getMessage(), e);
                }
            }
            applianceList = newApplianceList;
        }
    }

    private static class LoadCredentialsTask extends AsyncTask<CognitoCachingCredentialsProvider, Void, CognitoCachingCredentialsProvider> {
        @Override
        protected CognitoCachingCredentialsProvider doInBackground(CognitoCachingCredentialsProvider... voids) {
            if (ourInstance.credentialsProvider != null)
            {
                Log.d("task", ourInstance.credentialsProvider.getLogins().toString());
                try {
                    ourInstance.credentialsProvider.refresh();
                } catch (Exception e) {
                    Log.d("LoadCredentialsTask", "Exception: " + e.getMessage(), e);
                }
            }
            else
            {
                Log.d("LoadCredentialsTask", "credentialsProvider was null");
            }
            return ourInstance.credentialsProvider;
        }
    }

    public String invoke(GoogleSignInAccount account, InvokeRequest request) {
        try {
            addLoginsFromAccount(account);
            return new LambdaInvoker(request).execute().get();
        } catch (InterruptedException e) {
            Log.d("invoke", "InterruptedException: " + e.getMessage(), e);
        } catch (ExecutionException e) {
            Log.d("invoke", "ExecutionException: " + e.getMessage(), e);
        }
        return null;
    }

    private static void addLoginsFromAccount(GoogleSignInAccount account) {
        HashMap<String, String> logins = new HashMap<>();

        String accountID = account.getIdToken();
        Log.d("onCreate", "accountID: " + accountID);
        logins.put("accounts.google.com", accountID);
        ourInstance.credentialsProvider.setLogins(logins);
    }

    private class LambdaInvoker extends AsyncTask<Void, Void, String> {

        private final InvokeRequest invokeRequest;

        public LambdaInvoker(InvokeRequest request) {
            invokeRequest = request;
        }

        @Override
        protected String doInBackground(Void... voids) {
            AWSLambdaClient client = (credentialsProvider == null) ? new AWSLambdaClient()
                    : new AWSLambdaClient(credentialsProvider);
            client.setRegion(Region.getRegion(Regions.US_EAST_2));
            try {
                ByteBuffer buffer = client.invoke(invokeRequest).getPayload();
                String response = byteBufferToString(buffer, Charset.forName("UTF-8"));
                Log.e("Tag", response, null);
                return response;
            } catch (Exception e) {
                Log.e("Tag", "Failed to invoke AWS: " + e.getMessage(), e);
                return null;
            }

        }
    }

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
}
