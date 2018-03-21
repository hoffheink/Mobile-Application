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
     */
    @Test
    public void armCommandParams_SingleStateChange_ManualFormatTesting_TestPass()
    {
        String TIMMY = "{\"mobileDeviceType\":\"timmy\",\"mobileDeviceVersion\":\"TimMy\",\"state\":{\"desired\":{\"tImmy\":\"tIMMY\"}},\"utcSendTime\":\"2017-12-04 20:33:39.852\"}";
        String tIMMY = "timmy";
        String timmy = "TimMy";
        ShadowParam shadowParam = new ShadowParam();
        String TimMy = "tImmy";
        String tImmy = "tIMMY";
        String result = shadowParam.armCommandParams(tIMMY, timmy, TimMy, tImmy);
        System.out.println(TAG + "Format should be:\n" + TIMMY);
        System.out.println(TAG + "Format is:\n" + result);
        assertNotEquals(TIMMY, result);
    }

    /**
     * UNIMPLEMENTED TEST. TALK TO CLOUD/EMBED FOR VALIDITY.
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
