package edu.uwplatt.projects1.spbmobile.Shadow;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Date;
import java.util.HashMap;
import edu.uwplatt.projects1.spbmobile.TimeHandle;

/**
 * Generic class used to centralize the construction of parameters for shadow invoking with
 * AWS. Constants correspond to the name of variable on the AWS servers.
 */
public class ShadowParam {
    private final String DESIRED_STATE = "state";

    /**
     * Default constructor, does nothing.
     */
    public ShadowParam() {
    }

    /**
     * Creates a json formatted string to send update commands to AWS IOT shadow devices.
     *
     * @param deviceName    name of the appliance.
     * @param deviceType    type of appliance.
     * @param deviceVersion appliance version.
     * @param component     the specific component of the device that is to be changed.
     * @param newState      the desired state the component should be in.
     * @return a json formatted string for invoking a command update.
     */
    public String armCommandParams(String deviceName, String deviceType, String deviceVersion, String component, String newState) {
        Gson gson = new Gson();
        HashMap<String, String> innerHashMap = new HashMap<>();
        innerHashMap.put(component, newState);
        String result = gson.toJson(new UpdateClass(deviceName, deviceType, deviceVersion, new State(innerHashMap), TimeHandle.getUTCTime(new Date())));
        Log.e("penis", result);
        return result;
    }

    private class UpdateClass {
        UpdateClass(String deviceName, String mobileDeviceType, String mobileDeviceVersion, State state, String utcSendTime) {
            this.deviceName = deviceName;
            this.mobileDeviceType = mobileDeviceType;
            this.mobileDeviceVersion = mobileDeviceVersion;
            this.state = state;
            this.utcSendTime = utcSendTime;
        }

        String deviceName;
        String mobileDeviceType;
        String mobileDeviceVersion;
        State state;
        String utcSendTime;
    }

    public class State {
        State(HashMap<String, String> desired) {
            this.desired = desired;
        }
        HashMap<String, String> desired;
    }

    private String getState(String component, String newState) {
        Gson gson = new GsonBuilder().create();
        HashMap<String, String> hashMap = new HashMap<>();
        HashMap<String, String> innerHashMap = new HashMap<>();
        String str = gson.toJson(innerHashMap.put(component, newState));
        hashMap.put(DESIRED_STATE, str);
        return gson.toJson(hashMap);
    }
}