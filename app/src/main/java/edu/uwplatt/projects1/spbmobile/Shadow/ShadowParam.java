package edu.uwplatt.projects1.spbmobile.Shadow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uwplatt.projects1.spbmobile.CloudDatasource;

/**
 * Generic class used to centralize the construction of parameters for shadow invoking with
 * AWS. Constants correspond to the name of variable on the AWS servers.
 */
public class ShadowParam
{
    private final String DEV_NAME = "deviceName";
    private final String DEV_TYPE = "mobileDeviceType";
    private final String DEV_VERSION = "mobileDeviceVersion";
    private final String DESIRED_STATE = "state";
    private final String UTC_TIME = "utcSendTime";


    /**
     * Default constructor, does nothing.
     */
    public ShadowParam()
    {
    }

    /**
     * Creates a json formatted string to send update commands to AWS IOT shadow devices.
     * @param deviceName name of the appliance.
     * @param deviceType type of appliance.
     * @param deviceVersion appliance version.
     * @param command the specific component of the device that is to be changed.
     * @param stateChange the desired state the component should be in.
     * @return a json formatted string for invoking a command update.
     */
    public String armCommandParams(String deviceName, String deviceType, String deviceVersion, String command, String stateChange)
    {
        Gson gson = new GsonBuilder().create();
        Map<String, String> commandParam = new HashMap<String, String>();

        commandParam.put(DEV_NAME, deviceName);
        commandParam.put(DEV_TYPE, deviceType);
        commandParam.put(DEV_VERSION, deviceVersion);
        commandParam.put(DESIRED_STATE, new HashMap<String, String>().put(command, stateChange));
        commandParam.put(UTC_TIME, CloudDatasource.getUTCTime(new Date()));

        return gson.toJson(commandParam);
    }
}