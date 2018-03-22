package edu.uwplatt.projects1.spbmobile;

import edu.uwplatt.projects1.spbmobile.Shadow.ShadowParam;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertNotEquals;

public class ShadowParamUnitTest
{
    private final static String TAG = "ShadowParamTest";

    /**
     * Test requires manual comparison.
     *
     * TEST TO BE REMOVED!!!
     */
    @Test
    public void armCommandParams_SingleStateChange_ManualFormatTesting_TestPass()
    {
        String CORRECT = "{\"mobileDeviceType\":\"Hat\",\"mobileDeviceVersion\":\"1.0.0\",\"state\":{\"desired\":{\"Bow\":\"on\"}},\"utcSendTime\":\"2017-12-04 20:33:39.852\"}";
        String mType = "Hat";
        String mVer = "1.0.0";
        String Command = "Bow";
        String newState = "on";

        ShadowParam shadowParam = new ShadowParam();
        String result = shadowParam.armCommandParams(mType, mVer, Command, newState);
        System.out.println(TAG + "Format should be:\n" + CORRECT);
        System.out.println(TAG + "Format is:\n" + result);
        assertNotEquals(CORRECT, result);
    }

    /**
     * UNIMPLEMENTED TEST. TALK TO CLOUD/EMBED FOR VALIDITY.
     *
     * Implement test with the expected format.
     */
    @Test
    public void armCommandParams_MultiStateChange_ManualFormatTesting_TestPass()
    {
        String CORRECT = "{\"mobileDeviceType\":\"timmy\",\"mobileDeviceVersion\":\"TimMy\",\"state\":{\"desired\":{\"tImmy\":\"tIMMY\"}},\"utcSendTime\":\"2017-12-04 20:33:39.852\"}";

        String devType = "hat";
        String version = "timmy";
        String component = "LED";
        boolean stateChange = true;
        Map<String, String> mCommand = new HashMap<>();

        for(int i = 1; i < 5; i++)
        {
            mCommand.put((component + i), String.valueOf(stateChange));
            stateChange = !stateChange;
        }
        assert(false);
    }
}
