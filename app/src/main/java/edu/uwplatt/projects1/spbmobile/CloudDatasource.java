package edu.uwplatt.projects1.spbmobile;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.DescribeThingRequest;
import com.amazonaws.services.iot.model.DescribeThingResult;
import com.amazonaws.services.iot.model.ListThingsRequest;
import com.amazonaws.services.iot.model.ListThingsResult;
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

import com.google.gson.Gson;

/**
 * Created by dowster on 12/9/2017.
 */

class CloudDatasource {
    private static CloudDatasource ourInstance;
    private static Context ourContext;


    private List<Appliance> applianceList = new ArrayList<Appliance>();
    private Appliance[] appliances = {new Appliance("One", "One").setStatus("NotOK"), new Appliance("Two", "Two"), new Appliance("Three", "Three"), new Appliance("Four", "Four"), new Appliance("Five", "Five"), new Appliance("Six", "Six"), new Appliance("Seven", "Seven"), new Appliance("Eight", "Eight"), new Appliance("Nine", "Nine"), new Appliance("Ten", "Ten"), new Appliance("Eleven", "Eleven"), new Appliance("Twelve", "Twelve"), new Appliance("Thirteen", "Thirteen")};


    static CloudDatasource getInstance(Context inContext, GoogleSignInAccount account) {
        if(ourInstance == null || !ourContext.equals(inContext)) {
            ourInstance = new CloudDatasource(inContext);
            ourContext = inContext;
        }
        addLoginsFromAccount(account);
        task t = new task();
        t.execute(ourInstance.credentialsProvider);
        return ourInstance;
    }

    private CloudDatasource(Context inContext) {
        credentialsProvider = new CognitoCachingCredentialsProvider(
                inContext,
                "us-east-2:1641195a-2e43-4f91-bca0-5e8e6edd6878", // Identity pool ID
                Regions.US_EAST_2 // Region
        );
    }

    private class GetAppliancesRunnable implements Runnable
    {
        @Override
        public void run() {
            applianceList = new ArrayList<>();
            if (credentialsProvider != null && credentialsProvider.getCredentials()!= null)
            {
                AWSIot awsIot = new AWSIotClient(credentialsProvider);
                awsIot.setRegion(Region.getRegion(Regions.US_EAST_2));
                ListThingsRequest listThingsRequest = new ListThingsRequest();
                listThingsRequest.setRequestCredentials(credentialsProvider.getCredentials());
                ListThingsResult listThingsResult = awsIot.listThings(listThingsRequest);
                for (ThingAttribute o : listThingsResult.getThings()) {
                    Appliance appliance = new Appliance(o.getThingName(),o.getVersion().toString());
                    applianceList.add(appliance);
                }
            }
        }
    }

    public List<Appliance> getAppliances() {
        Runnable getAppliancesRunnable = new GetAppliancesRunnable();
        Thread t = new Thread(getAppliancesRunnable);
        t.start();
        while (t.isAlive())
        {

        }
        return applianceList;
    }

    public CognitoCachingCredentialsProvider credentialsProvider;

    private static class task extends AsyncTask<CognitoCachingCredentialsProvider, Void, CognitoCachingCredentialsProvider> {
        @Override
        protected CognitoCachingCredentialsProvider doInBackground(CognitoCachingCredentialsProvider... voids) {
            Log.d("task", ourInstance.credentialsProvider.getLogins().toString());
            try
            {
                ourInstance.credentialsProvider.refresh();
            }
            catch (Exception e)
            {
                Log.d("invoke", "Exception: " + e.getMessage(), e);
            }
            return ourInstance.credentialsProvider;
        }
    }

    public String invoke(GoogleSignInAccount account, InvokeRequest request) {
        try {
            addLoginsFromAccount(account);
            return new Invoker(request).execute().get();
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

    private class Invoker extends AsyncTask<Void, Void, String> {

        private final InvokeRequest invokeRequest;
        public Invoker(InvokeRequest request)
        {
            invokeRequest = request;
        }
        @Override
        protected String doInBackground(Void... voids) {
            AWSLambdaClient client = (credentialsProvider == null) ? new AWSLambdaClient()
                    : new AWSLambdaClient(credentialsProvider);
            client.setRegion(Region.getRegion(Regions.US_EAST_2));
            try {
                ByteBuffer b = client.invoke(invokeRequest).getPayload();
                String response = byteBufferToString(b, Charset.forName("UTF-8"));
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
