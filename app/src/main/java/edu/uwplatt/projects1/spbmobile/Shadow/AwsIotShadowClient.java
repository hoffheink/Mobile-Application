package edu.uwplatt.projects1.spbmobile.Shadow;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.services.iotdata.model.GetThingShadowRequest;
import com.amazonaws.services.iotdata.model.GetThingShadowResult;
import com.amazonaws.services.iotdata.model.UpdateThingShadowRequest;
import com.amazonaws.services.iotdata.model.UpdateThingShadowResult;
import com.amazonaws.services.iotdata.AWSIotDataClient;
import java.nio.ByteBuffer;
import edu.uwplatt.projects1.spbmobile.AsyncTaskResult;
import edu.uwplatt.projects1.spbmobile.MainActivity;


public class AwsIotShadowClient
{
    private static final String TAG = AwsIotShadowClient.class.getCanonicalName();
    //private static final String customerSpecificEP = "a121odz0gmuc20.iot.us-east-1.amazonaws.com";
    private static AwsIotShadowClient ourInstance;

    private CognitoCachingCredentialsProvider credentialsProvider;
    private AWSIotDataClient awsIotDataClient;

    /**
     * Creates an instance of the AwsIotShadowClient is there is no instance, otherwise
     * it returns the instance of a AwsShadowClient.
     *
     * @param credentials credentials provided by the AWS cognito authentication.
     * @return the instance of AwsShadowClient.
     */
    public static AwsIotShadowClient getInstance(@NonNull CognitoCachingCredentialsProvider credentials)
    {
        if (ourInstance == null)
            ourInstance = new AwsIotShadowClient(credentials);
        return ourInstance;
    }

    /**
     * Constructor used to initialize a AwsIotShadowClient.
     *
     * @param credentials credentials provided by the AWS cognito authentication.
     */
    private AwsIotShadowClient(@NonNull CognitoCachingCredentialsProvider credentials) {
        updateShadowAuthentication(credentials);
    }

    private static String getCustomerEndpoint()
    {
        switch (MainActivity.region)
        {
            case US_EAST_1:
                return "a121odz0gmuc20.iot.us-east-1.amazonaws.com";
            default:
                return "a121odz0gmuc20.iot.us-east-2.amazonaws.com";
        }
    }

    /**
     * Sets the credentials used to communicate with a shadow object and rebuilds the
     * necessary shadow client.
     *
     * @param credentials credentials provided by the AWS cognito authentication.
     */
    public void updateShadowAuthentication(@NonNull CognitoCachingCredentialsProvider credentials) {
        awsIotDataClient = new AWSIotDataClient(credentials);
        awsIotDataClient.setEndpoint(getCustomerEndpoint());
        this.credentialsProvider = credentials;
    }

    /**
     * Updates a shadow object.
     *
     * @param deviceName    name of the appliance.
     * @param deviceType    appliance type.
     * @param deviceVersion appliance version.
     * @param command       object in the appliance that is going to be changed.
     * @param stateChange   state of object in appliance that is being requested.
     */
    public void updateCommandShadow(String deviceName, String deviceType, String deviceVersion, String command, String stateChange)
    {
        try
        {
            getShadow(deviceName);
            ShadowParam sp = new ShadowParam();
            String payload = sp.armCommandParams(deviceName, deviceType, deviceVersion, command, stateChange);
            UpdateShadowTask updateShadowTask = new UpdateShadowTask(deviceName, payload, credentialsProvider.getCredentials());
            updateShadowTask.execute();
        }
        catch(Exception e)
        {
            Log.e(TAG, "UpdateShadowCall", e);
        }
    }

    /**
     * Gets a shadow object.
     *
     * @param deviceName name of the appliance that is going to be changed.
     */
    public void getShadow(String deviceName)
    {
        try
        {
            GetShadowTask getShadowTask = new GetShadowTask(deviceName, credentialsProvider.getCredentials());
            getShadowTask.execute();
        }
        catch (Exception e)
        {
            Log.e(TAG, "GetShadowCaller", e);
        }
    }

    /**
     * Subclass activity to get the shadow object.
     */
    private class GetShadowTask extends AsyncTask<Void, Void, AsyncTaskResult<String>>
    {
        private final String thingName;
        private final AWSSessionCredentials credentials;

        protected GetShadowTask(String thingName, AWSSessionCredentials credentials) {
            this.thingName = thingName;
            this.credentials = credentials;
        }

        @Override
        protected AsyncTaskResult<String> doInBackground(Void... voids)
        {
            try
            {
                GetThingShadowRequest getThingShadowRequest = new GetThingShadowRequest();
                getThingShadowRequest.setRequestCredentials(credentials);
                getThingShadowRequest.setThingName(thingName);

                GetThingShadowResult getThingShadowResult = awsIotDataClient.getThingShadow(getThingShadowRequest);

                byte[] bytes = new byte[getThingShadowResult.getPayload().remaining()];
                getThingShadowResult.getPayload().get(bytes);
                String result = new String(bytes);
                return new AsyncTaskResult<>(result);
            } catch (Exception e) {
                Log.e(TAG, "***GetShadow***", e);
                return new AsyncTaskResult<>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<String> result)
        {
            if(result.getError() == null)
                Log.i(TAG, result.getResult());
            else
                Log.e(TAG, "GetShadow", result.getError());
        }
    }

    /**
     * Subclass activity to update shadow object.
     */
    private class UpdateShadowTask extends AsyncTask<Void, Void, AsyncTaskResult<String>> {
        private String thingName;
        private String payload;
        AWSSessionCredentials credentials;

        /**
         *
         * @param thingName
         * @param message
         * @param credentials
         */
        protected UpdateShadowTask(String thingName, String message, AWSSessionCredentials credentials)
        {
            this.thingName = thingName;
            this.payload = message;
            this.credentials = credentials;
        }

        /**
         *
         * @param voids
         * @return
         */
        @Override
        protected AsyncTaskResult<String> doInBackground(Void... voids)
        {
            try {
                ByteBuffer payloadBuffer = ByteBuffer.wrap(payload.getBytes());
                UpdateThingShadowRequest updateThingShadowRequest = new UpdateThingShadowRequest();
                updateThingShadowRequest.setThingName(thingName);
                updateThingShadowRequest.setRequestCredentials(credentials);
                updateThingShadowRequest.setPayload(payloadBuffer);

                UpdateThingShadowResult updateThingShadowResult = awsIotDataClient.updateThingShadow(updateThingShadowRequest);
                byte[] bytes = new byte[updateThingShadowResult.getPayload().remaining()];
                updateThingShadowResult.getPayload().get(bytes);
                String result = new String(bytes);
                return new AsyncTaskResult<>(result);
            }
            catch (Exception e)
            {
                Log.e(TAG, "***UpdateShadow***", e);
                return new AsyncTaskResult<>(e);
            }
        }

        /**
         *
         * @param result
         */
        @Override
        protected void onPostExecute(AsyncTaskResult<String> result)
        {
            if(result.getError() == null)
                Log.i(TAG, result.getResult());
            else
                Log.e(TAG, "UpdateShadow", result.getError());
        }
    }
}