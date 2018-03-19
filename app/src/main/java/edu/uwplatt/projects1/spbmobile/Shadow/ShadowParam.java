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
public class ShadowParam
{
    /**
     * Default constructor, does nothing.
     */
    public ShadowParam()
    {
    }

    /**
     * Creates a json formatted string to send update commands to AWS IOT shadow devices.
     *
     * @param deviceType    type of appliance.
     * @param deviceVersion appliance version.
     * @param component     the specific component of the device that is to be changed.
     * @param newState      the desired state the component should be in.
     * @return a json formatted string for invoking a command update.
     */
    public String armCommandParams(String deviceType, String deviceVersion, String component, String newState) {
        Gson gson = new Gson();
        HashMap<String, String> innerHashMap = new HashMap<>();
        innerHashMap.put(component, newState);
        String result = gson.toJson(new UpdateClass(deviceType, deviceVersion, new State(innerHashMap), TimeHandle.getUTCTime(new Date())));
        return result;
    }

    /**
     * Maintains a set of data members related to updating the current state of a shadow object.
     */
    private class UpdateClass
    {
        String mobileDeviceType;
        String mobileDeviceVersion;
        State state;
        String utcSendTime;

        /**
         * Constructor to set the values of the data members in the update class.
         * @param mobileDeviceType type of appliance.
         * @param mobileDeviceVersion appliance version.
         * @param state the requested state change of the shadow object.
         * @param utcSendTime the time the update shadow request was sent.
         */
        UpdateClass(String mobileDeviceType, String mobileDeviceVersion, State state, String utcSendTime)
        {
            this.mobileDeviceType = mobileDeviceType;
            this.mobileDeviceVersion = mobileDeviceVersion;
            this.state = state;
            this.utcSendTime = utcSendTime;
        }
    }

    /**
     * Maintains a set of date related to the state change.
     */
    private class State
    {
        /**
         * Constructor to set the values of the data members in the state class.
         * @param desired the desired component to be changed and the state to be changed to.
         */
        State(HashMap<String, String> desired) {
            this.desired = desired;
        }
        HashMap<String, String> desired;
    }
}