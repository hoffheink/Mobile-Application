package edu.uwplatt.projects1.spbmobile.Shadow;

import com.google.gson.Gson;

import java.util.Date;

import edu.uwplatt.projects1.spbmobile.Command.CommandQueue;
import edu.uwplatt.projects1.spbmobile.Time;

/**
 * Generic class used to centralize the construction of parameters for shadow invoking with
 * AWS. Constants correspond to the name of variable on the AWS servers.
 */
public class ShadowParam {
    /**
     * Default constructor, does nothing.
     */
    public ShadowParam() {
    }

    /**
     * Creates a json formatted string to send update commands to AWS IOT shadow devices.
     *
     * @param deviceType    type of appliance.
     * @param deviceVersion appliance version.
     * @param commandQueue  set of pairs that define the state to change and the desired state.
     * @return a json formatted string for invoking a command update.
     */
    public String armCommandParams(String deviceType, String deviceVersion,
                                   CommandQueue commandQueue) {
        Gson gson = new Gson();
        return gson.toJson(new UpdateCommandStructure(deviceType, deviceVersion,
                new Desired(commandQueue)));
    }

    /**
     * Class used to define key and value pairs for an update shadow request.
     */
    private class UpdateCommandStructure {
        String mobileDeviceType;
        String mobileDeviceVersion;
        String utcSendTime;
        Desired state;

        /**
         * Constructor sets teh values of the class' attributes.
         *
         * @param type    the type of mobile device being used.
         * @param version the version of the mobile device being used.
         * @param command desired class that contains all state change request.
         */
        UpdateCommandStructure(String type, String version, Desired command) {
            state = command;
            mobileDeviceType = type;
            mobileDeviceVersion = version;
            utcSendTime = Time.getUTCTime(new Date());
        }
    }

    /**
     * Class is used to format update request.
     */
    private class Desired {
        CommandQueue desired;

        /**
         * Constructor sets the values of the class' attributes.
         *
         * @param commandQueue The CommandQueue to be sent
         */
        Desired(CommandQueue commandQueue) {
            desired = commandQueue;
        }
    }
}