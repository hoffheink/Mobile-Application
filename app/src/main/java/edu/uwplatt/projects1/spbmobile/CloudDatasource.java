package edu.uwplatt.projects1.spbmobile;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
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
import com.amazonaws.services.iot.model.ListPrincipalPoliciesResult;
import com.amazonaws.services.iot.model.ListThingsRequest;
import com.amazonaws.services.iot.model.Policy;
import com.amazonaws.services.iot.model.ThingAttribute;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.uwplatt.projects1.spbmobile.Appliance.Appliance;
import edu.uwplatt.projects1.spbmobile.Appliance.UIComponents.ApplianceListAdapter;
import edu.uwplatt.projects1.spbmobile.Lambda.LambdaPlatform;

/**
 * This class represents a link to our cloud datasource.
 */
public class CloudDatasource {
    private static final Regions ourRegions = Regions.US_EAST_1;
    public static final String regionString = ourRegions.toString().toLowerCase()
            .replace("_", "-");
    public static final Region ourRegion = Region.getRegion(ourRegions);
    private static final String US_EAST_1_IdentityPoolID =
            "us-east-1:273c20ea-e478-4c5d-8adf-8f46402a066b";
    private static final String US_EAST_2_IdentityPoolID =
            "us-east-2:1641195a-2e43-4f91-bca0-5e8e6edd6878";

    @SuppressLint("StaticFieldLeak")
    private static CloudDatasource ourInstance;
    @SuppressLint("StaticFieldLeak")
    private static Context ourContext;

    @SuppressWarnings("all")
    @NonNull
    private CognitoCachingCredentialsProvider credentialsProvider;
    @NonNull
    static String subscriptionArn = "";

    /**
     * Returns the subscription authentication role number as a string.
     *
     * @return a string of the subscription authentication role number.
     */
    @NonNull
    public static String getSubscriptionArn() {
        return subscriptionArn;
    }

    /**
     * Get the cognito credentials.
     *
     * @return the cognito credentials as a CognitoCachingCredentialsProvider object.
     */
    CognitoCachingCredentialsProvider getCognitoCachingCredentialsProvider() {
        return credentialsProvider;
    }

    /**
     * Calls the lambda invoker to invoke a provided function and give it a provided message.
     *
     * @param lambdaFunction a string that contains the lambda functions ARN.
     * @param message        the formatted payload to send to the lambda function.
     * @return the response provided by the lambda invoker.
     */
    public AsyncTaskResult invokeLambda(String lambdaFunction, String message) {
        LambdaPlatform lambdaPlatform = new LambdaPlatform();
        return lambdaPlatform.invokeLambdaFunction(lambdaFunction, message, credentialsProvider);
    }

    public static List<Appliance> applianceList = new ArrayList<>();

    /**
     * This class will get an instance of our CloudDatasource.
     *
     * @param context the Application Context for the CloudDatasource.
     * @param account the GoogleSignInAccount for the CloudDatasource.
     * @return the CloudDatasource.
     */
    public static CloudDatasource getInstance(@NonNull Context context,
                                              @NonNull GoogleSignInAccount account) {
        if (ourInstance == null || !ourContext.equals(context)) {
            ourInstance = new CloudDatasource(context);
            ourContext = context;
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

    private static String getIdentityPool() {
        switch (ourRegions) {
            case US_EAST_1:
                return US_EAST_1_IdentityPoolID;
            case US_EAST_2:
                return US_EAST_2_IdentityPoolID;
            default:
                return "";
        }
    }

    /**
     * This constructor will create a CloudDatasource.
     *
     * @param context the Application Context used to get the CognitoCachingCredentialsProvider.
     */
    private CloudDatasource(@NonNull Context context) {
        credentialsProvider = new CognitoCachingCredentialsProvider(context,
                getIdentityPool(), ourRegions);
    }

    /**
     * This method will load the list of Appliances.
     */
    public void loadAppliances(boolean wait) {
        Runnable getAppliancesRunnable = new GetAppliancesRunnable();
        Thread thread = new Thread(getAppliancesRunnable);
        thread.start();
        if (wait)
        {
            try {
                thread.join();
            } catch (InterruptedException ignored) {

            }
        }
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
    private class GetAppliancesRunnable implements Runnable {
        /**
         * This method will actually get the Appliances.
         */
        @Override
        public void run() {
            ArrayList<Appliance> applianceList = new ArrayList<>();
            AWSSessionCredentials credentials = null;
            try {
                credentials = ourInstance.getCredentials();
            } catch (Exception ignored) {
            }
            if (credentials != null) {
                AWSIot awsIot = new AWSIotClient(ourInstance.credentialsProvider);
                awsIot.setRegion(ourRegion);

                Log.i("GetAppliancesRunnable", "CognitoID: "
                        + ourInstance.credentialsProvider.getIdentityId());

                ListThingsRequest listThingsRequest = new ListThingsRequest();
                listThingsRequest.setRequestCredentials(credentials);
                try {

                    List<String> thingNames = getThingNames(awsIot);

                    for (ThingAttribute thingAttribute : awsIot.listThings(listThingsRequest)
                            .getThings()) {
                        if (thingNames.contains(thingAttribute.getThingName()))
                            applianceList.add(createAppliance(thingAttribute));
                    }
                } catch (Exception e) {
                    Log.e("GetAppliancesRunnable", e.getMessage(), e);
                }
            }
            boolean updateDisplay = CloudDatasource.applianceList.size() != applianceList.size();
            CloudDatasource.applianceList = applianceList;
            if (updateDisplay)
            {
                //ApplianceListAdapter.instance.notifyDataSetChanged();
            }

        }

        private List<String> getThingNames(AWSIot awsIot) {
            ListPrincipalPoliciesRequest listPrincipalPoliciesRequest =
                    new ListPrincipalPoliciesRequest();
            listPrincipalPoliciesRequest.setPrincipal(ourInstance.credentialsProvider
                    .getIdentityId());
            ListPrincipalPoliciesResult result = awsIot.listPrincipalPolicies(
                    listPrincipalPoliciesRequest);
            List<String> thingNames = new ArrayList<>();
            for (Policy policy : result.getPolicies())
                thingNames.add(policy.getPolicyName().replace("app-", ""));
            return thingNames;
        }

        private Appliance createAppliance(ThingAttribute o) {
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
            return appliance;
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
}