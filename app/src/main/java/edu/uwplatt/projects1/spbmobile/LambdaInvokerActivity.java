package edu.uwplatt.projects1.spbmobile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.lambdainvoker.*;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvokeRequest;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;

public class LambdaInvokerActivity extends AppCompatActivity {
    private TextView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lambda_invoker);

        view = (TextView) findViewById(R.id.tf);
        final String jsonRequestParameters = "{\"thingId\":\"charlieDevice1\",\"thingPin\":\"5000\"}";

        final CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-2:1641195a-2e43-4f91-bca0-5e8e6edd6878", // Identity pool ID
                Regions.US_EAST_2 // Region
        );

        HashMap<String, String> logins = new HashMap<>();
        String accountID = MainActivity.account.getId();
        Log.d("onCreate", "accountID: " + accountID);
        logins.put("accounts.google.com", accountID);
        credentialsProvider.setLogins(logins);


        new AsyncTask<CognitoCachingCredentialsProvider, Void, CognitoCachingCredentialsProvider>() {
            @Override
            protected CognitoCachingCredentialsProvider doInBackground(CognitoCachingCredentialsProvider... voids) {
                credentialsProvider.refresh();
                return credentialsProvider;
            }
        }.execute(credentialsProvider);


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                AWSLambdaClient client = (credentialsProvider == null) ? new AWSLambdaClient()
                        : new AWSLambdaClient(credentialsProvider);
                client.setRegion(Region.getRegion(Regions.US_EAST_2));
                try {
                    InvokeRequest invokeRequest = new InvokeRequest();
                    invokeRequest.setFunctionName("arn:aws:lambda:us-east-2:955967187114:function:iot-app-register-device");
                    invokeRequest.setPayload(ByteBuffer.wrap(jsonRequestParameters.getBytes()));
                    ByteBuffer b = client.invoke(invokeRequest).getPayload();
                    Log.e("Tag", byteBufferToString(b, Charset.forName("UTF-8")), null);
                } catch (Exception e) {
                    Log.e("Tag", "Failed to invoke nick", e);
                }
                return null;
            }
        }.execute();


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
