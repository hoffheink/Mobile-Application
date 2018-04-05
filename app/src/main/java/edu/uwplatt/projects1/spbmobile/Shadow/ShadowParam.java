package edu.uwplatt.projects1.spbmobile.Shadow;

import com.google.gson.Gson;

import java.util.Date;
import java.util.HashMap;
import edu.uwplatt.projects1.spbmobile.Time;

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
     * @return a json formatted string for invoking a command update.
     */
    public String armCommandParams(String deviceType, String deviceVersion, HashMap<String, String> command)
    {
        Gson gson = new Gson();


        /*
        HashMap<String, String> innerHashMap = new HashMap<>();
        innerHashMap.put(component, newState);
        String result = gson.toJson(new UpdateClass(deviceType, deviceVersion, new State(innerHashMap), Time.getUTCTime(new Date())));
        */
        return gson.toJson(new UpdateCommandStructure(deviceType, deviceVersion, new Desired(command)));
    }

    private class UpdateCommandStructure
    {
        String mobileDeviceType, mobileDeviceVersion, utcSendTime;
        Desired state;

        UpdateCommandStructure(String type, String version, Desired command)
        {
            Gson gson = new Gson();
            state = command;
            mobileDeviceType = type;
            mobileDeviceVersion = version;
            utcSendTime = Time.getUTCTime(new Date());
        }
    }
    private class Desired
    {
        HashMap<String,String> desired;

        Desired(HashMap<String,String> commnad)
        {
            desired = commnad;
        }
    }
}
