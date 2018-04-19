package edu.uwplatt.projects1.spbmobile;

import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import edu.uwplatt.projects1.spbmobile.Command.Command;
import edu.uwplatt.projects1.spbmobile.Command.CommandQueue;
import edu.uwplatt.projects1.spbmobile.Command.Parameter;
import edu.uwplatt.projects1.spbmobile.Shadow.ShadowParam;

import static org.junit.Assert.assertEquals;

/**
 * Test class for testing the methods in ShadowParam.java class.
 * Test naming format is as follows:
 * <method Name>_<comparison type>_<what is being tested>_<expected result>
 */
public class ShadowParamUnitTest {
    /**
     * This test is used to manually check the format of a payload to be sent to a shadow
     * object with a single requested state change.
     */
    @Test
    public void armCommandParams_manualComparison_singleStateChange_testAutoPass() {
        String result;
        Date date = new Date();
        UUID guid = UUID.randomUUID();
        String CORRECT = "{\"mobileDeviceType\":\"Hat\",\"mobileDeviceVersion\":\"5\",\"utcSendTime\":\"" + Time.getUTCTime(date) + "\",\"state\":{\"desired\":{\"commandQueue\":[{\"properties\":{\"priority\":0,\"guid\":\"" + guid.toString() + "\",\"timestamp\":\"" + Time.getUTCTime(date) + "\"},\"arguments\":{\"param0\":\"value0\",\"param1\":\"value1\",\"param2\":\"value2\"}}]}}}";
        String mType = "Hat";
        String mVer = "5";

        CommandQueue commandQueue = new CommandQueue();
        Parameter[] parameters = new Parameter[3];
        parameters[0] = new Parameter();
        parameters[0].machineName = "param0";
        parameters[0].value = "value0";
        parameters[1] = new Parameter();
        parameters[1].machineName = "param1";
        parameters[1].value = "value1";
        parameters[2] = new Parameter();
        parameters[2].machineName = "param2";
        parameters[2].value = "value2";
        Command command = new Command(guid);
        command.parameters = parameters;
        commandQueue.addCommand(command, date);
        result = ShadowParam.armCommandParams(mType, mVer, commandQueue, date);
        System.out.println(result);
        assertEquals("These should be the same", result, CORRECT);
    }
}