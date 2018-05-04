package edu.uwplatt.projects1.spbmobile.Shadow;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.services.iotdata.AWSIotDataClient;
import com.amazonaws.services.iotdata.model.GetThingShadowRequest;
import com.amazonaws.services.iotdata.model.GetThingShadowResult;
import com.amazonaws.services.iotdata.model.UpdateThingShadowRequest;
import com.amazonaws.services.iotdata.model.UpdateThingShadowResult;

import java.nio.ByteBuffer;
import java.util.Date;

import edu.uwplatt.projects1.spbmobile.AsyncTaskResult;
import edu.uwplatt.projects1.spbmobile.Command.CommandQueue;
import edu.uwplatt.projects1.spbmobile.MainActivity;

/**
 * This class handles sending and receiving shadow messages from AWS shadow client.
 */
public class AwsIotShadowClient {
    private static final String TAG = AwsIotShadowClient.class.getCanonicalName();

    private static AwsIotShadowClient ourInstance;
    private AWSIotDataClient awsIotDataClient;
    private AWSSessionCredentials credentials;

    /**
     * Creates an instance of the AwsIotShadowClient if there is no instance, otherwise
     * it returns the instance of a AwsShadowClient.
     *
     * @param credentials credentials provided by the AWS cognito authentication.
     * @return the instance of AwsShadowClient.
     */
    public static AwsIotShadowClient getInstance(@NonNull AWSSessionCredentials credentials)
            throws Exception {
        if (ourInstance == null)
            ourInstance = new AwsIotShadowClient(credentials);
        return ourInstance;
    }

    /**
     * Constructor used to initialize a AwsIotShadowClient.
     *
     * @param credentials credentials provided by the AWS cognito authentication.
     */
    private AwsIotShadowClient(@NonNull AWSSessionCredentials credentials) throws Exception {
        updateShadowAuthentication(credentials);
    }

    /**
     * Gets the customer specific endpoint of a AWS server in a given region
     * for making calls to the shadow.
     *
     * @return the customer specific endpoint as a string.
     */
    private static String getCustomerEndpoint() throws Exception {
        switch (MainActivity.region) {
            case US_EAST_1:
                return "a121odz0gmuc20.iot.us-east-1.amazonaws.com";
            case US_EAST_2:
                return "a121odz0gmuc20.iot.us-east-2.amazonaws.com";
            default:
                throw new Exception("Region " + MainActivity.region.toString());
        }
    }

    /**
     * Sets the credentialsProvider used to communicate with a shadow object and rebuilds the
     * necessary shadow client.
     *
     * @param credentials credentials provided by the AWS cognito authentication.
     */
    private void updateShadowAuthentication(@NonNull AWSSessionCredentials credentials)
            throws Exception {
        try {
            awsIotDataClient = new AWSIotDataClient(credentials);
            awsIotDataClient.setEndpoint(getCustomerEndpoint());
            this.credentials = credentials;
        } catch (Exception e) {
            Log.d(TAG, e.getMessage(), e);
            throw e;
        }
    }

    private class UpdateCommandShadowRunnable implements Runnable {
        private final String deviceName;
        private final String deviceType;
        private final String deviceVersion;
        private final CommandQueue commandQueue;
        private final AWSSessionCredentials credentials;

        UpdateCommandShadowRunnable(String deviceName, String deviceType,
                                    String deviceVersion, CommandQueue commandQueue,
                                    AWSSessionCredentials credentials) {
            this.deviceName = deviceName;
            this.deviceType = deviceType;
            this.deviceVersion = deviceVersion;
            this.commandQueue = commandQueue;
            this.credentials = credentials;
        }

        @Override
        public void run() {
            try {
                String payload = ShadowParam.armCommandParams(deviceType, deviceVersion,
                        commandQueue, new Date());
                UpdateShadowTask updateShadowTask = new UpdateShadowTask(deviceName, payload,
                        credentials, awsIotDataClient);
                updateShadowTask.execute();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                throw e;
            }
        }
    }

    /**
     * Updates shadow object with a set commands.
     *
     * @param deviceName    name of the appliance.
     * @param deviceType    appliance type.
     * @param deviceVersion appliance version.
     * @param commandQueue  set of pairs that define the state to change and the desired state;
     *                      if order is required use a LinkedHashMap.
     */
    public void updateCommandShadow(String deviceName, String deviceType,
                                    String deviceVersion, CommandQueue commandQueue) {
        Runnable updateCommandShadowRunnable = new UpdateCommandShadowRunnable(deviceName,
                deviceType, deviceVersion, commandQueue, credentials);
        Thread thread = new Thread(updateCommandShadowRunnable);
        thread.start();
    }

    /**
     * Gets a shadow object.
     *
     * @param deviceName name of the appliance that is going to be changed.
     */
    public void getShadow(String deviceName) {
        try {
            GetShadowTask getShadowTask = new GetShadowTask(deviceName, credentials,
                    awsIotDataClient);
            getShadowTask.execute();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * Subclass activity to get the current message stored in a device shadow.
     */
    private static class GetShadowTask extends AsyncTask<Void, Void, AsyncTaskResult<String>> {
        private final String thingName;
        private final AWSSessionCredentials credentials;
        private final AWSIotDataClient client;

        /**
         * Constructor to set the necessary parameters for accessing a device shadow.
         *
         * @param thingName   the name of the device shadow to call.
         * @param credentials credentials to authenticate caller's permission with AWS.
         */
        GetShadowTask(String thingName, AWSSessionCredentials credentials,
                      AWSIotDataClient client) {
            this.thingName = thingName;
            this.credentials = credentials;
            this.client = client;
        }

        /**
         * Background activity to retrieve the appliance shadow from the cloud.
         *
         * @param voids nothing.
         * @return the message retrieved from the AWS IoT servers.
         */
        @Override
        protected AsyncTaskResult<String> doInBackground(Void... voids) {
            try {
                GetThingShadowRequest getThingShadowRequest = new GetThingShadowRequest();
                getThingShadowRequest.setRequestCredentials(credentials);
                getThingShadowRequest.setThingName(thingName);

                GetThingShadowResult getThingShadowResult = client.getThingShadow(
                        getThingShadowRequest);

                byte[] bytes = new byte[getThingShadowResult.getPayload().remaining()];
                getThingShadowResult.getPayload().get(bytes);
                String result = new String(bytes);
                return new AsyncTaskResult<>(result);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                return new AsyncTaskResult<>(e);
            }
        }

        /**
         * Handles post execute instructions on a separate thread from the caller.
         *
         * @param result the message retrieved from the AWS IoT servers.
         */
        @Override
        protected void onPostExecute(AsyncTaskResult<String> result) {
            if (result.getError() == null)
                Log.i(TAG, result.getResult());
            else
                Log.e(TAG, result.getError().getMessage(), result.getError());
        }
    }

    /**
     * Subclass activity to update the current message stored in a device shadow.
     */
    private static class UpdateShadowTask extends AsyncTask<Void, Void, AsyncTaskResult<String>> {
        private final String thingName;
        private final String payload;
        private final AWSSessionCredentials credentials;
        private final AWSIotDataClient client;

        /**
         * Constructor to set the necessary parameters for accessing a device shadow.
         *
         * @param thingName   the name of the device shadow to call.
         * @param payload     the request formatted message to send to the AWS IoT shadow.
         * @param credentials credentials to authenticate caller's permission with AWS.
         */
        UpdateShadowTask(String thingName, String payload,
                         AWSSessionCredentials credentials, AWSIotDataClient client) {
            this.thingName = thingName;
            this.payload = payload;
            this.credentials = credentials;
            this.client = client;
        }


        /**
         * Background activity to retrieve the appliance shadow from the cloud.
         *
         * @param voids nothing.
         * @return the message retrieved from the AWS IoT servers.
         */
        @Override
        protected AsyncTaskResult<String> doInBackground(Void... voids) {
            try {
                ByteBuffer payloadBuffer = ByteBuffer.wrap(payload.getBytes());
                UpdateThingShadowRequest updateThingShadowRequest = new UpdateThingShadowRequest();
                updateThingShadowRequest.setThingName(thingName);
                updateThingShadowRequest.setRequestCredentials(credentials);
                updateThingShadowRequest.setPayload(payloadBuffer);

                UpdateThingShadowResult updateThingShadowResult =
                        client.updateThingShadow(updateThingShadowRequest);

                byte[] bytes = new byte[updateThingShadowResult.getPayload().remaining()];
                updateThingShadowResult.getPayload().get(bytes);
                String result = new String(bytes);
                return new AsyncTaskResult<>(result);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                throw e;
            }
        }

        /**
         * Handles post execute instructions on a separate thread from the caller.
         *
         * @param result the message retrieved from the AWS IoT servers.
         */
        @Override
        protected void onPostExecute(AsyncTaskResult<String> result) {
            if (result.getError() == null)
                Log.i(TAG, result.getResult());
            else
                Log.e(TAG, result.getError().getMessage(), result.getError());
        }
    }
}