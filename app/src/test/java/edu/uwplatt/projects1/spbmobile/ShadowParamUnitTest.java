package edu.uwplatt.projects1.spbmobile;

//SNS push notifications, Google
//Firesbase bro

import edu.uwplatt.projects1.spbmobile.Shadow.ShadowParam;
import org.junit.Test;
import static org.junit.Assert.assertNotEquals;

public class ShadowParamUnitTest
{
    private final static String TAG = "ShadowParamTest";


    @Test
    public void armCommandParams_ManualFormatTesting_TestPass()
    {
        String devicetype = "KidsAwesomeHat";
        String deviceVersion = "1.0.0";
        String comp = "Bow";
        String state = "on";

        String CORRECT = "{\"mobileDeviceType\":\"KidsAwesomeHat\",\"mobileDeviceVersion\":\"1.0.0\",\"state\":{\"desired\":{\"Bow\":\"on\"}},\"utcSendTime\":\"2017-12-04 20:33:39.852\"}";
        ShadowParam shadowParam = new ShadowParam();
        String result = shadowParam.armCommandParams(devicetype, deviceVersion, comp, state);
        System.out.println(TAG + "Format should be:\n" + CORRECT);
        System.out.println(TAG + "Format is:\n" + result);
        assertNotEquals(CORRECT, result);
    }
}
