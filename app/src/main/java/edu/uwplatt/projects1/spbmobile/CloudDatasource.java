package edu.uwplatt.projects1.spbmobile;

import android.annotation.SuppressLint;
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

import edu.uwplatt.projects1.spbmobile.Appliance.Appliance;

/**
 * This class represents a link to our cloud datasource.
 */
public class CloudDatasource {
    private static final String US_EAST_1_IdentityPoolID =
            "us-east-1:273c20ea-e478-4c5d-8adf-8f46402a066b";
    private static final String US_EAST_2_IdentityPoolID =
            "us-east-2:1641195a-2e43-4f91-bca0-5e8e6edd6878";
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

    /**
     * This class will get an instance of our CloudDatasource.
     *
     * @param context the Application Context for the CloudDatasource.
     * @param account the GoogleSignInAccount for the CloudDatasource.
     * @param region  the RegionEnum for the CloudDatasource.
     * @return the CloudDatasource.
     */
    public static CloudDatasource getInstance(@NonNull Context context,
                                              @NonNull GoogleSignInAccount account,
                                              RegionEnum region) {
        if (ourInstance == null || !ourContext.equals(context) || !ourRegion.equals(region)) {
            ourInstance = new CloudDatasource(context, region);
            ourContext = context;
            ourRegion = region;
        }
        addLoginsFromAccount(account);
        GetCredentialProviderTask getCredentialProviderTask = new GetCredentialProviderTask();
        try {
            getCredentialProviderTask.execute(ourInstance.credentialsProvider);
        } catch (Exception e) {
            Log.e("getInstance", "Unable to load credentials:" + e.getMessage(), e);
        }
        return ourInstance;
    }

    /**
     * This constructor will create a CloudDatasource.
     *
     * @param context the Application Context used to get the CognitoCachingCredentialsProvider.
     * @param region  the RegionEnum used to get the CognitoCachingCredentialsProvider.
     */
    private CloudDatasource(@NonNull Context context, @NonNull RegionEnum region) {
        switch (region) {
            case US_EAST_1:
                credentialsProvider = new CognitoCachingCredentialsProvider(context,
                        US_EAST_1_IdentityPoolID, Regions.US_EAST_1);
                break;
            case US_EAST_2:
                credentialsProvider = new CognitoCachingCredentialsProvider(context,
                        US_EAST_2_IdentityPoolID, Regions.US_EAST_2);
                break;
        }
    }

    /**
     * This method will load the list of Appliances.
     */
    void loadAppliances() {
        Runnable getAppliancesRunnable = new GetAppliancesRunnable();
        Thread thread = new Thread(getAppliancesRunnable);
        thread.start();
    }

    /**
     * This class is used to get AWSSessionCredentials.
     */
    private class GetCredentialsRunnable implements Runnable {
        AWSSessionCredentials credentials = null;
        CognitoCachingCredentialsProvider provider;
        Exception exception = null;

        /**
         * This constructor is used to create a GetCredentialsRunnable.
         *
         * @param provider the CognitoCachingCredentialsProvider.
         */
        GetCredentialsRunnable(CognitoCachingCredentialsProvider provider) {
            this.provider = provider;
        }

        /**
         * This method will actually get the credentials.
         * Upon completion, either credentials or exception will have a value.
         */
        @Override
        public void run() {
            try {
                credentials = provider.getCredentials();
            } catch (Exception e) {
                exception = e;
                credentials = null;
            }
        }

    }

    /**
     * This method will get the AWSSessionCredentials.
     *
     * @return the AWSSessionCredentials.
     * @throws Exception just a general thrower.
     */
    public AWSSessionCredentials getCredentials() throws Exception {
        GetCredentialsRunnable getCredentialsRunnable =
                new GetCredentialsRunnable(ourInstance.credentialsProvider);
        Thread thread = new Thread(getCredentialsRunnable);
        thread.start();
        while (getCredentialsRunnable.credentials == null &&
                getCredentialsRunnable.exception == null)
            Thread.sleep(100);
        if (getCredentialsRunnable.exception != null)
            throw new Exception(getCredentialsRunnable.exception);
        return getCredentialsRunnable.credentials;
    }

    /**
     * This class is used to get Appliances.
     */
    private static class GetAppliancesRunnable implements Runnable {
        /**
         * This method will actually get the Appliances.
         */
        @Override
        public void run() {
            //TODO: refactor this method!
            ArrayList<Appliance> newApplianceList = new ArrayList<>();
            AWSSessionCredentials credentials = null;
            try {
                credentials = ourInstance.getCredentials();
            } catch (Exception ignored) {
            }
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
                ListPrincipalPoliciesRequest listPrincipalPoliciesRequest =
                        new ListPrincipalPoliciesRequest();
                listPrincipalPoliciesRequest
                        .setPrincipal(ourInstance.credentialsProvider.getIdentityId());
                Log.i("GetAppliancesRunnable", "CognitoID: "
                        + ourInstance.credentialsProvider.getIdentityId());

                ListThingsRequest listThingsRequest = new ListThingsRequest();
                listThingsRequest.setRequestCredentials(credentials);

                try {
                    /*List<String> thingNames = new ArrayList<>();
                    for (Policy policy : awsIot.listPrincipalPolicies(listPrincipalPoliciesRequest)
                            .getPolicies()) {
                        String policyName = policy.getPolicyName()
                                .replace("app-", "");
                        thingNames.add(policyName);
                    }*/

                    for (ThingAttribute o : awsIot.listThings(listThingsRequest).getThings()) {
                        //if (thingNames.contains(o.getThingName())) {
                        Appliance appliance = new Appliance(o.getThingName(),
                                o.getVersion().toString());
                        String thingType = o.getThingTypeName();
                        if (thingType != null) {
                            switch (o.getThingTypeName()) {
                                case "coffee-maker":
                                    appliance.setApplianceType(Appliance.ApplianceTypes
                                            .CoffeeMaker);
                                    break;
                                case "test":
                                    appliance.setApplianceType(Appliance.ApplianceTypes.Test);
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

    /**
     * This class is used to get the CognitoCachingCredentialsProvider.
     */
    private static class GetCredentialProviderTask extends
            AsyncTask<CognitoCachingCredentialsProvider, Void, CognitoCachingCredentialsProvider> {
        /**
         * This method is used to get the CognitoCachingCredentialsProvider.
         *
         * @return the CognitoCachingCredentialsProvider.
         */
        @Override
        protected CognitoCachingCredentialsProvider doInBackground(
                CognitoCachingCredentialsProvider... voids) {
            Log.i("GetCredProviderTask",
                    ourInstance.credentialsProvider.getLogins().toString());
            try {
                ourInstance.credentialsProvider.refresh();
            } catch (Exception e) {
                Log.e("GetCredProviderTask", e.getMessage(), e);
            }
            return ourInstance.credentialsProvider;
        }
    }

    /**
     * This method is used to invokeLambda a lambda request.
     *
     * @param account the GoogleSignInAccount to execute the lambda function with.
     * @param request the InvokeRequest to execute.
     * @return the String result.
     */
    public String invokeLambda(GoogleSignInAccount account, InvokeRequest request) {
        try {
            String functionName = request.getFunctionName();
            String newFunctionName = "arn:aws:lambda:" + ourRegion.toString().toLowerCase()
                    .replace("_", "-") + ":955967187114:function:" + functionName;
            Log.i("invokeLambda", "functionName: " + newFunctionName);
            request.setFunctionName(newFunctionName);
            //TODO: The line of code before: is it needed? If so, why is this not handled before?
            addLoginsFromAccount(account);
            return new LambdaInvoker(request).execute().get();
        } catch (Exception e) {
            Log.e("invokeLambda", e.getMessage(), e);
        }
        return null;
    }

    /**
     * This method is used to add logins to the credentialsProvider from a GoogleSignInAccount.
     *
     * @param account the GoogleSignInAccount to add logins from.
     */
    private static void addLoginsFromAccount(GoogleSignInAccount account) {
        HashMap<String, String> logins = new HashMap<>();

        String accountID = account.getIdToken();
        Log.i("addLoginsFromAccount", "accountID: " + accountID);
        logins.put("accounts.google.com", accountID);
        ourInstance.credentialsProvider.setLogins(logins);
    }

    /**
     * This class is used to invoke a lambda function.
     */
    @SuppressLint("StaticFieldLeak")
    private class LambdaInvoker extends AsyncTask<Void, Void, String> {

        private final InvokeRequest invokeRequest;

        /**
         * This constructor is used to create a LambdaInvoker.
         *
         * @param request the InvokeRequest to invoke.
         */
        LambdaInvoker(InvokeRequest request) {
            invokeRequest = request;
        }

        /**
         * This method will actually invoke the Lambda request.
         *
         * @return the invoke request's results.
         */
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
                Log.e("LambdaInvoker", "Failed to invokeLambda AWS: " + e.getMessage(), e);
                return null;
            }
        }
    }

    /**
     * Not documenting due to future removal
     */
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
