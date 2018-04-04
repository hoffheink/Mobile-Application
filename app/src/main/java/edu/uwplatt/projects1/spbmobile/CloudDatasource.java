package edu.uwplatt.projects1.spbmobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.config.AWSConfigurable;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
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

import edu.uwplatt.projects1.spbmobile.Appliance.Appliance;

import static android.content.Context.USER_SERVICE;


public class CloudDatasource {
    private static final String US_EAST_1_IdentityPoolID = "us-east-1:273c20ea-e478-4c5d-8adf-8f46402a066b";
    private static final String US_EAST_2_IdentityPoolID = "us-east-2:1641195a-2e43-4f91-bca0-5e8e6edd6878";
    @SuppressLint("StaticFieldLeak")
    private static CloudDatasource ourInstance;
    @SuppressLint("StaticFieldLeak")
    private static Context ourContext;
    @SuppressLint("StaticFieldLeak")
    private static RegionEnum ourRegion;

    @SuppressWarnings("all")
    @NonNull
    private CognitoCachingCredentialsProvider credentialsProvider;

    public enum RegionEnum {
        US_EAST_1,
        US_EAST_2
    }

    public static List<Appliance> applianceList = new ArrayList<>();

    public static CloudDatasource getInstance(@NonNull Context inContext, @NonNull GoogleSignInAccount account, RegionEnum inRegion) {
        if (ourInstance == null || !ourContext.equals(inContext) || !ourRegion.equals(inRegion)) {
            ourInstance = new CloudDatasource(inContext, inRegion);
            ourContext = inContext;
            ourRegion = inRegion;
        }
        addLoginsFromAccount(account);
        LoadCredentialsTask loadCredentialsTask = new LoadCredentialsTask();
        try {
            loadCredentialsTask.execute(ourInstance.credentialsProvider);
        } catch (Exception e) {
            Log.e("getInstance", "Unable to load credentials", e);
        }
        return ourInstance;
    }

    public void loadAppliances() {
        Runnable getAppliancesRunnable = new GetAppliancesRunnable();
        Thread thread = new Thread(getAppliancesRunnable);
        thread.start();
    }

    private CloudDatasource(@NonNull Context inContext, @NonNull RegionEnum inRegion) {
        switch (inRegion) {
            case US_EAST_1:
                credentialsProvider = new CognitoCachingCredentialsProvider(inContext,
                        US_EAST_1_IdentityPoolID, Regions.US_EAST_1);
                break;
            case US_EAST_2:
                credentialsProvider = new CognitoCachingCredentialsProvider(inContext,
                        US_EAST_2_IdentityPoolID, Regions.US_EAST_2);
                break;
        }
    }

    public void updateCognitoSync() throws Exception {
        Regions awsRegion;
        switch (ourRegion) {
            case US_EAST_1:
                awsRegion = Regions.US_EAST_1;
                break;
            case US_EAST_2:
                awsRegion = Regions.US_EAST_2;
                break;
            default:
                throw new Exception("unhandled region");
        }
        AWSConfiguration awsConfiguration = new AWSConfiguration(ourContext);
        CognitoSyncManager cognitoSyncManager = new CognitoSyncManager(ourContext, credentialsProvider, awsConfiguration);
        Dataset dataset = cognitoSyncManager.openOrCreateDataset("user");
        int i = 0;
    }

    private AWSSessionCredentials getCredentials() {
        try {
            return ourInstance.credentialsProvider.getCredentials();
        } catch (Exception e) {
            Log.e("getCredentials", e.getMessage(), e);
            return null;
        }
    }

    private static class GetAppliancesRunnable implements Runnable {
        @Override
        public void run() {
            ArrayList<Appliance> newApplianceList = new ArrayList<>();
            AWSSessionCredentials credentials = ourInstance.getCredentials();
            if (credentials != null) {
                AWSIot awsIot = new AWSIotClient(ourInstance.credentialsProvider);
                switch (ourRegion) {
                    case US_EAST_1:
                        awsIot.setRegion(Region.getRegion(Regions.US_EAST_1));
                        break;
                    case US_EAST_2:
                        awsIot.setRegion(Region.getRegion(Regions.US_EAST_2));
                        break;
                }
                ListPrincipalPoliciesRequest listPrincipalPoliciesRequest = new ListPrincipalPoliciesRequest();
                listPrincipalPoliciesRequest.setPrincipal(ourInstance.credentialsProvider.getIdentityId());
                Log.i("GetAppliancesRunnable", "CognitoID: " + ourInstance.credentialsProvider.getIdentityId());

                ListThingsRequest listThingsRequest = new ListThingsRequest();
                listThingsRequest.setRequestCredentials(credentials);

                try {
                    /*List<String> thingNames = new ArrayList<>();
                    for (Policy policy : awsIot.listPrincipalPolicies(listPrincipalPoliciesRequest).getPolicies()) {
                        String policyName = policy.getPolicyName().replace("app-", "");
                        thingNames.add(policyName);
                    }*/

                    for (ThingAttribute o : awsIot.listThings(listThingsRequest).getThings()) {
                        //if (thingNames.contains(o.getThingName())) {
                        Appliance appliance = new Appliance(o.getThingName(), o.getVersion().toString());
                        String thingType = o.getThingTypeName();
                        if (thingType != null) {
                            switch (o.getThingTypeName()) {
                                case "coffee-maker":
                                    appliance.setApplianceType(Appliance.ApplianceType.CoffeeMaker);
                                    break;
                            }
                        }
                        newApplianceList.add(appliance);
                        //}
                    }
                } catch (Exception e) {
                    Log.e("GetAppliancesRunnable", e.getMessage(), e);
                }
            }
            applianceList = newApplianceList;
        }
    }

    private static class LoadCredentialsTask extends AsyncTask<CognitoCachingCredentialsProvider, Void, CognitoCachingCredentialsProvider> {
        @Override
        protected CognitoCachingCredentialsProvider doInBackground(CognitoCachingCredentialsProvider... voids) {
            Log.i("LoadCredentialsTask", ourInstance.credentialsProvider.getLogins().toString());
            try {
                ourInstance.credentialsProvider.refresh();
            } catch (Exception e) {
                Log.e("LoadCredentialsTask", "Exception: " + e.getMessage(), e);
            }
            return ourInstance.credentialsProvider;
        }
    }

    public String invoke(GoogleSignInAccount account, InvokeRequest request) {
        try {
            String functionName = request.getFunctionName();
            String newFunctionName = "arn:aws:lambda:" + ourRegion.toString().toLowerCase().replace("_", "-") + ":955967187114:function:" + functionName;
            Log.i("invoke", "functionName: " + newFunctionName);
            request.setFunctionName(newFunctionName);
            addLoginsFromAccount(account);
            return new LambdaInvoker(request).execute().get();
        } catch (InterruptedException e) {
            Log.e("invoke", "InterruptedException: " + e.getMessage(), e);
        } catch (ExecutionException e) {
            Log.e("invoke", "ExecutionException: " + e.getMessage(), e);
        }
        return null;
    }

    private static void addLoginsFromAccount(GoogleSignInAccount account) {
        HashMap<String, String> logins = new HashMap<>();

        String accountID = account.getIdToken();
        Log.i("addLoginsFromAccount", "accountID: " + accountID);
        logins.put("accounts.google.com", accountID);
        ourInstance.credentialsProvider.setLogins(logins);
    }

    @SuppressLint("StaticFieldLeak")
    private class LambdaInvoker extends AsyncTask<Void, Void, String> {

        private final InvokeRequest invokeRequest;

        LambdaInvoker(InvokeRequest request) {
            invokeRequest = request;
        }

        @Override
        protected String doInBackground(Void... voids) {
            AWSLambdaClient client = new AWSLambdaClient(credentialsProvider);
            switch (ourRegion) {
                case US_EAST_1:
                    client.setRegion(Region.getRegion(Regions.US_EAST_1));
                    break;
                case US_EAST_2:
                    client.setRegion(Region.getRegion(Regions.US_EAST_2));
                    break;
            }
            try {
                ByteBuffer buffer = client.invoke(invokeRequest).getPayload();
                String response = byteBufferToString(buffer, Charset.forName("UTF-8"));
                Log.i("LambdaInvoker", response, null);
                return response;
            } catch (Exception e) {
                Log.e("LambdaInvoker", "Failed to invoke AWS: " + e.getMessage(), e);
                return null;
            }
        }
    }

    private static String byteBufferToString(ByteBuffer buffer, Charset charset) {
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
