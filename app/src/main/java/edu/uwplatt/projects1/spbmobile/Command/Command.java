package edu.uwplatt.projects1.spbmobile.Command;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.UUID;

import edu.uwplatt.projects1.spbmobile.Appliance.Appliance;
import edu.uwplatt.projects1.spbmobile.Shadow.AwsIotShadowClient;

/**
 * This class is used to model Commands.
 */
public class Command {
    public static Command currentCommand;
    UUID guid;
    public String humanName;
    boolean priority;
    public Parameter[] parameters;
    State[] states;
    String cmdName;

    public Command(UUID guid)
    {
        this.guid = guid;
    }

    public static void executeCurrentCommand(@NonNull AwsIotShadowClient shadowClient,
                                             String appVersion) throws Exception {
        CommandQueue commandQueue = new CommandQueue(currentCommand, new Date());
        shadowClient.updateCommandShadow(
                Appliance.currentAppliance.getName(),
                Appliance.currentAppliance.getApplianceType().toString(),
                appVersion,
                commandQueue);
    }

    /**
     * Sets the parameters on the current command.
     *
     * @param machineName this is the name that the argument should be sent as.
     * @param value       the value being inputted.
     */
    public static void setParameterOnCurrentCommand(String machineName, Object value) {
        if (currentCommand != null)
            for (int i = 0; i < currentCommand.parameters.length; i++)
                if (currentCommand.parameters[i].machineName.equals(machineName))
                    currentCommand.parameters[i].value = value;
    }
}