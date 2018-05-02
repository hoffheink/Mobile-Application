package edu.uwplatt.projects1.spbmobile.Appliance.UIComponents;

/**
 * Created by victim on 5/1/2018.
 */

public class RegisterDeviceFormat
{
    private String thingId;
    private String thingPin;
    private String deviceToken;
    private String subscriptionArn;

    public RegisterDeviceFormat(String id, String pin, String devTok, String subArn)
    {
        thingId = id;
        thingPin = pin;
        deviceToken = devTok;
        subscriptionArn = subArn;
    }
}