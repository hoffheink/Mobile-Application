package edu.uwplatt.projects1.spbmobile;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by dowster on 12/9/2017.
 */

class CloudDatasource {
    private static CloudDatasource ourInstance;
    private static Context ourContext;

    private String[] appliances = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen"};


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

    public String[] getDevices() {
        return appliances.clone();
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
