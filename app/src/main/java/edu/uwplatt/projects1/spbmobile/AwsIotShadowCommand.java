package edu.uwplatt.projects1.spbmobile;

import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iotdata.model.GetThingShadowRequest;
import com.amazonaws.services.iotdata.model.GetThingShadowResult;
import com.amazonaws.services.iotdata.model.UpdateThingShadowRequest;
import com.amazonaws.services.iotdata.model.UpdateThingShadowResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.amazonaws.services.iotdata.AWSIotDataClient;

import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

import static java.time.Instant.now;

/**
 * Created by Bear on 2/18/2018.
 */

public class AwsIotShadowCommand {
    private static final String TAG = AwsIotShadowCommand.class.getCanonicalName();
    private static final Regions regions = Regions.US_EAST_2;
    private final AWSSessionCredentials credentials;
    private static final String customerSpecificEP = "a121odz0gmuc20.iot.us-east-2.amazonaws.com";

    AWSIotDataClient awsIotDataClient;


    public AwsIotShadowCommand(CognitoCachingCredentialsProvider cred) {
        awsIotDataClient = new AWSIotDataClient(cred);
        awsIotDataClient.setEndpoint(customerSpecificEP);
        this.credentials = cred.getCredentials();
    }

    public void refreshIotClient(CognitoCachingCredentialsProvider cred) {
        awsIotDataClient = new AWSIotDataClient(cred);
        awsIotDataClient.setEndpoint(customerSpecificEP);
    }

    public void updateShadow(String deviceName, String deviceType, String deviceVersion, String command, String stateChange) {
        String str = armPayload(deviceName, deviceType, deviceVersion, desiredStateSet(command, stateChange));
        UpdateShadowTask updateShadowTask = new UpdateShadowTask(deviceName, str, credentials);
        updateShadowTask.execute();
        return;
    }

    public String getShadow(String deviceName) {
        GetShadowTask getShadowTask = new GetShadowTask(deviceName, credentials);
        return getShadowTask.doInBackground();
    }

    private String armPayload(String devName, String devType, String devR, String desired) {
        String str = "{\"deviceName\":\"" + devName + "\",";
        str += "\"mobileDeviceType\":\"" + devType + "\",";
        str += "\"mobileDeviceVersion\":\"" + devR + "\",";
        str += "\"desired\":" + desired + ",";
        str += "\"utcSendTime\":\"" + getLogTime() + "\"}";
        return str;
    }


    private String desiredStateSet(String command, String desiredState) {
        return "{\"" + command + "\":\"" + desiredState + "\"}";
    }


    public String getLogTime() {
        return CloudDatasource.getUTCTime(new Date());
    }

    private class GetShadowTask extends AsyncTask<Void, Void, String> {
        private static final String TAG = "AwsIotGetShadow";
        private final String thingName;
        private final AWSSessionCredentials credentials;

        public GetShadowTask(String thingName, AWSSessionCredentials credentials) {
            this.thingName = thingName;
            this.credentials = credentials;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                GetThingShadowRequest getThingShadowRequest = new GetThingShadowRequest();
                getThingShadowRequest.setRequestCredentials(credentials);
                getThingShadowRequest.setThingName(thingName);

                //TODO: NetworkOnMainThread error. Fix me:)
                GetThingShadowResult getThingShadowResult = awsIotDataClient.getThingShadow(getThingShadowRequest);

                byte[] bytes = new byte[getThingShadowResult.getPayload().remaining()];
                getThingShadowResult.getPayload().get(bytes);
                String resStr = new String(bytes);
                Log.d("GetShadowTask:Result", resStr);
                return resStr;
            } catch (Exception e) {
                Log.e(TAG, "doInBackground:\n", e);
                return null;
            }
        }
    }
    private class UpdateShadowTask extends AsyncTask<Void, Void, String> {
        private static final String TAG = "AwsIotUpdateShadow";
        private final String thingName;
        private final String message;
        private final AWSSessionCredentials credentials;

        public UpdateShadowTask(String thingName, String message, AWSSessionCredentials credentials) {
            this.thingName = thingName;
            this.message = message;
            this.credentials = credentials;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                UpdateThingShadowRequest request = new UpdateThingShadowRequest();
                request.setRequestCredentials(credentials);
                request.setThingName(thingName);
                ByteBuffer payloadBuffer = ByteBuffer.wrap(message.getBytes("UTF-8"));
                request.setPayload(payloadBuffer);

                UpdateThingShadowResult result = awsIotDataClient.updateThingShadow(request);

                byte[] bytes = new byte[result.getPayload().remaining()];
                result.getPayload().get(bytes);
                String resStr = new String(bytes);
                Log.d("UpdateShadowTask:Result", resStr);
                return resStr;
            } catch (Exception e) {
                Log.e(TAG, "doInBackground:\n", e);
                return null;
            }
        }
    }
}