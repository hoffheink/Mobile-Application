package edu.uwplatt.projects1.spbmobile;

import edu.uwplatt.projects1.spbmobile.Shadow.ShadowParam;
import org.junit.Test;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.Assert.assertNotEquals;

public class ShadowParamUnitTest
{
    private final static String TAG = "ShadowParamTest";


    @Test
    public void armCommandParams_SingleStateChange_ManualFormatTesting_TestPass()
    {
        String CORRECT = "{\"mobileDeviceType\":\"Hat\",\"mobileDeviceVersion\":\"1.0.0\",\"state\":{\"desired\":{\"Bow\":\"on\"}},\"utcSendTime\":\"2017-12-04 20:33:39.852\"}";
        String mType = "Hat";
        String mVer = "1.0.0";
        LinkedHashMap<String, String> command = new LinkedHashMap<>();
        String newState = "on";
        command.put("Hat", "on");
        ShadowParam shadowParam = new ShadowParam();
        String result = shadowParam.armCommandParams(mType, mVer, command);
        System.out.println(TAG + "Format should be:\n" + CORRECT);
        System.out.println(TAG + "Format is:\n" + result);
        assertNotEquals(CORRECT, result);
    }


    @Test
    public void armCommandParams_MultiStateChange_ManualFormatTesting_TestPass()
    {
        ShadowParam shadowParam = new ShadowParam();
        String deviceType = "Panzer";
        String deviceVersion = "4D";
        String comp = "SideSkirtPlate";
        boolean equiped = false;
        LinkedHashMap<String, String> command = new LinkedHashMap<>();

        for(int i = 0; i < 5; i++)
        {
            command.put(comp+i, String.valueOf(equiped));
            equiped = !equiped;
        }
        String result = shadowParam.armCommandParams(deviceType,deviceVersion, command);
        System.out.println(TAG + "Format is:\n" + result);
        assert(true);
    }

    @Test
    public void armCommandParams_MultiStateChange_ManualFormatTesting_TestPass_LARGE()
    {
        ShadowParam shadowParam = new ShadowParam();
        String deviceType = "Panzer";
        String deviceVersion = "4D";
        String comp = "SideSkirtPlate";
        boolean equiped = false;
        LinkedHashMap<String, String> command = new LinkedHashMap<>();

        for(int i = 0; i < 50; i++)
        {
            command.put(comp+i, String.valueOf(equiped));
            equiped = !equiped;
        }
        String result = shadowParam.armCommandParams(deviceType,deviceVersion, command);
        System.out.println(TAG + "Format is:\n" + result);
        assert(true);
    }
}
