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
import com.amazonaws.services.iot.model.ThingAttribute;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import edu.uwplatt.projects1.spbmobile.Appliance.Appliance;
import edu.uwplatt.projects1.spbmobile.Lambda.LambdaPlatform;

/**
 * Centralizes data and method invokers to communicate with the cloud.
 */
public class CloudDatasource
{
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
    private String subscriptionArn;

    /**
     * Sets the subscription authentication role number to the value of the string provided.
     * @param str the subscription authentication role number.
     */
    public void setSubscriptionArn(String str)
    {
        subscriptionArn = str;
    }

    /**
     * Returns the subscription authentication role number as a string.
     * @return a string of the subscription authentication role number.
     * @throws Exception an exception if no subscription authentication role number has been set.
     */
    public String getSubscriptionArn() throws Exception
    {
        if (subscriptionArn != null)
            return subscriptionArn;
        else
            throw new Exception("No subscriptionArn");
    }

    /**
     * Get the cognito credentials.
     * @return the cognito credentials as a CognitoCachingCredentialsProvider object.
     */
    public CognitoCachingCredentialsProvider getCognitoCachingCredentialsProvider()
    {
        return credentialsProvider;
    }

    /**
     * Calls the lambda invoker to invoke a provided function and give it a provided message.
     * @param lambdaFunction a string that contains the lambda functions ARN.
     * @param message the formatted payload to send to the lambda function.
     * @return the response provided by the lambda invoker.
     */
    public AsyncTaskResult<String> invokeLambda(String lambdaFunction, String message)
    {
        LambdaPlatform lambdaPlatform = new LambdaPlatform();
        AsyncTaskResult<String> response = lambdaPlatform.invokeLambdaFunction(lambdaFunction, message, credentialsProvider);
        return response;
    }


    public enum RegionEnum
    {
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

                try
                {
                    for (ThingAttribute o : awsIot.listThings(listThingsRequest).getThings())
                    {
                        Appliance appliance = new Appliance(o.getThingName(), o.getVersion().toString());
                        String thingType = o.getThingTypeName();
                        if (thingType != null)
                        {
                            switch (o.getThingTypeName()) {
                                case "coffee-maker":
                                    appliance.setApplianceType(Appliance.ApplianceType.CoffeeMaker);
                                    break;
                                case "test":
                                    appliance.setApplianceType(Appliance.ApplianceType.Test);
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

    private static void addLoginsFromAccount(GoogleSignInAccount account) {
        HashMap<String, String> logins = new HashMap<>();

        String accountID = account.getIdToken();
        Log.i("addLoginsFromAccount", "accountID: " + accountID);
        logins.put("accounts.google.com", accountID);
        ourInstance.credentialsProvider.setLogins(logins);
    }
}
