package edu.uwplatt.projects1.spbmobile;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iotdata.model.GetThingShadowRequest;
import com.amazonaws.services.iotdata.model.GetThingShadowResult;
import com.amazonaws.services.iotdata.model.UpdateThingShadowRequest;
import com.amazonaws.services.iotdata.model.UpdateThingShadowResult;
import com.amazonaws.services.iotdata.AWSIotDataClient;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AwsIotShadowCommand {
    private static final String TAG = AwsIotShadowCommand.class.getCanonicalName();
    private static final String customerSpecificEP = "a121odz0gmuc20.iot.us-east-2.amazonaws.com";
    private final static TimeZone UTC = TimeZone.getTimeZone("UTC");
    private final static String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS zzz";
    private AWSIotDataClient awsIotDataClient;

    AwsIotShadowCommand(CognitoCachingCredentialsProvider cred) {
        awsIotDataClient = new AWSIotDataClient(cred);
        awsIotDataClient.setEndpoint(customerSpecificEP);
    }

    public void refreshIotClient(CognitoCachingCredentialsProvider cred) {
        awsIotDataClient = new AWSIotDataClient(cred);
        awsIotDataClient.setEndpoint(customerSpecificEP);
    }

    void updateShadow(String deviceName, String deviceType, String deviceVersion, String command, String stateChange) {
        String str = armPayload(deviceName, deviceType, deviceVersion, desiredStateSet(command, stateChange));
        UpdateShadowTask updateShadowTask = new UpdateShadowTask(deviceName, str);
        updateShadowTask.execute();
    }

    private String armPayload(String devName, String devType, String devR, String desired) {
        String str = "{\"deviceName\":\"" + devName + "\",";
        str += "\"mobileDeviceType\":\"" + devType + "\",";
        str += "\"mobileDeviceVersion\":\"" + devR + "\",";
        str += "\"desired\":" + desired + ",";
        str += "\"utcSendTime\":\"" + getUTCTime(new Date()) + "\"}";
        return str;
    }

    private String desiredStateSet(String command, String desiredState) {
        return "{\"" + command + "\":\"" + desiredState + "\"}";
    }

    private static String getUTCTime(Date date) {
        if (date == null)
            date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT, Locale.US);
        sdf.setTimeZone(UTC);
        return sdf.format(date);
    }

    private class GetShadowTask extends AsyncTask<Void, Void, String> {
        private final String thingName;
        private static final String TAG = "AwsIotGetShadowTask";

        public GetShadowTask(String name) {
            thingName = name;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                GetThingShadowRequest getThingShadowRequest = new GetThingShadowRequest().withThingName(thingName);
                GetThingShadowResult result = awsIotDataClient.getThingShadow(getThingShadowRequest);
                byte[] bytes = new byte[result.getPayload().remaining()];
                result.getPayload().get(bytes);
                String resultString = new String(bytes);
                return resultString;
            } catch (Exception e) {
                Log.e(TAG, "doInBackground:\n", e);
                return null;
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class UpdateShadowTask extends AsyncTask<Void, Void, String> {
        private static final String TAG = "AwsIotUpdateShadow";
        private String thingName;
        private String message;

        UpdateShadowTask(String thingName, String message) {
            this.thingName = thingName;
            this.message = message;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                UpdateThingShadowRequest request = new UpdateThingShadowRequest();
                request.setThingName(thingName);
                ByteBuffer payloadBuffer = ByteBuffer.wrap(message.getBytes("UTF-8"));
                request.setPayload(payloadBuffer);

                UpdateThingShadowResult result = awsIotDataClient.updateThingShadow(request);

                byte[] bytes = new byte[result.getPayload().remaining()];
                result.getPayload().get(bytes);
                return new String(bytes);
            } catch (Exception e) {
                Log.e(TAG, "doInBackground:\n", e);
                return null;
            }
        }
    }
}