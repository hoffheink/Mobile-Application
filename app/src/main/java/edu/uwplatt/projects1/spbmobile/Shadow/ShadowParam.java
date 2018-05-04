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
     * Creates a json formatted string to send update commands to AWS IOT shadow devices.
     *
     * @param deviceType    type of appliance.
     * @param deviceVersion appliance version.
     * @param commandQueue  the CommandQueue that represents the state to change and the desired
     *                      state.
     * @param date          the date to be used when updating the shadow.
     * @return a json formatted string for invoking a command update.
     */
    public static String armCommandParams(String deviceType, String deviceVersion,
                                          CommandQueue commandQueue, Date date) {
        Gson gson = new Gson();
        Desired desired = new Desired(commandQueue);
        UpdateCommandStructure updateCommandStructure = new UpdateCommandStructure(deviceType,
                deviceVersion, desired, date);
        return gson.toJson(updateCommandStructure);
    }

    /**
     * Class used to define key and value pairs for an update shadow request.
     */
    private static class UpdateCommandStructure {
        private final String mobileDeviceType;
        private final String mobileDeviceVersion;
        private final String utcSendTime;
        private final Desired state;

        /**
         * Constructor sets the values of the class' attributes.
         *
         * @param type    the type of mobile device being used.
         * @param version the version of the mobile device being used.
         * @param command desired class that contains all state change request.
         * @param date    the date to be used in the updating of the shadow.
         */
        UpdateCommandStructure(String type, String version, Desired command, Date date) {
            state = command;
            mobileDeviceType = type;
            mobileDeviceVersion = version;
            utcSendTime = Time.getUTCTime(date);
        }
    }

    /**
     * Class is used to format update request.
     */
    private static class Desired {
        private final CommandQueue desired;

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