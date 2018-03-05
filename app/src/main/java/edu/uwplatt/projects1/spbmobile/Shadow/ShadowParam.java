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
 * Created by Bear on 3/4/2018.
 */

public class ShadowParam
{
    private final String DEV_NAME = "deviceName";
    private final String DEV_TYPE = "mobileDeviceType";
    private final String DEV_VERSION = "mobileDeviceVersion";
    private final String DESIRED_STATE = "desired";
    private final String UTC_TIME = "utcSendTime";


    public ShadowParam()
    {
    }

    public String armCommandParams(String deviceName, String deviceType, String deviceVersion, String command, String stateChange)
    {
        Gson gson = new GsonBuilder().create();
        Map<String, String> commandParam = new HashMap<String, String>();
        Map<String, String> chom = new HashMap<String, String>();

        commandParam.put(DEV_NAME, deviceName);
        commandParam.put(DEV_TYPE, deviceType);
        commandParam.put(DEV_VERSION, deviceVersion);
        commandParam.put(DESIRED_STATE, chom.put(command, stateChange));
        commandParam.put(UTC_TIME, CloudDatasource.getUTCTime(new Date()));

        return gson.toJson(commandParam);
    }
}