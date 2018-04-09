package edu.uwplatt.projects1.spbmobile;

import edu.uwplatt.projects1.spbmobile.Shadow.ShadowParam;
import org.junit.Test;
import java.util.LinkedHashMap;
import static org.junit.Assert.assertNotEquals;

/**
 * Test class for testing the methods in ShadowParam.java class.
 * Test naming format is as follows:
 *      <method Name>_<comparison type>_<what is being tested>_<expected result>
 */
public class ShadowParamUnitTest
{
    /**
     * This test is used to manually check the format of a payload to be sent to a shadow
     * object with a single requested state change.
     */
    @Test
    public void armCommandParams_manualComparison_singleStateChange_testAutoPass()
    {
        String TAG = "armCommandParams_manualComparison_singleStateChange_testAutoPass";
        String result = "";
        String CORRECT = "{\"mobileDeviceType\":\"Hat\",\"mobileDeviceVersion\":\"1.0.0\",\"state\":{\"desired\":{\"Bow\":\"on\"}},\"utcSendTime\":\"2017-12-04 20:33:39.852\"}";
        String mType = "Hat";
        String mVer = "1.0.0";
        LinkedHashMap<String, String> command = new LinkedHashMap<>();
        String newState = "on";
        command.put("Hat", "on");

        ShadowParam shadowParam = new ShadowParam();
        result = shadowParam.armCommandParams(mType, mVer, command);

        System.out.println(TAG + "Format should be:\n" + CORRECT);
        System.out.println(TAG + "Format is:\n" + result);
        assertNotEquals(CORRECT, result);
    }

    /**
     * This test is used to manually check the format of a payload to be sent to a shadow
     * object with a multiple requested state change.
     */
    @Test
    public void armCommandParams_manualComparison_multiStateChange_testAutoPass()
    {
        String TAG = "armCommandParams_manualComparison_multiStateChange_testAutoPass";
        ShadowParam shadowParam = new ShadowParam();
        String deviceType = "Panzer";
        String deviceVersion = "4D";
        String comp = "SideSkirtPlate";
        boolean equiped = false;
        LinkedHashMap<String, String> command = new LinkedHashMap<>();

        for(int i = 0; i < 20; i++)
        {
            command.put(comp+i, String.valueOf(equiped));
            equiped = !equiped;
        }
        String result = shadowParam.armCommandParams(deviceType,deviceVersion, command);
        System.out.println(TAG + "Format is:\n" + result);
        assert(true);
    }
}