package edu.uwplatt.projects1.spbmobile.Command;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import edu.uwplatt.projects1.spbmobile.Appliance.Appliance;
import edu.uwplatt.projects1.spbmobile.CloudDatasource;
import edu.uwplatt.projects1.spbmobile.MainActivity;
import edu.uwplatt.projects1.spbmobile.R;
import edu.uwplatt.projects1.spbmobile.Shadow.AwsIotShadowClient;

/**
 * This class is used to model Commands.
 */
public class Command {
    public static Command currentCommand;
    public String humanName;
    boolean priority;
    public Parameter[] parameters;
    State[] states;
    String cmdName;

    static boolean onOff = true;

    public static void executeCurrentCommand(@NonNull Context context)
    {
        AwsIotShadowClient.getInstance(CloudDatasource.getInstance(context,MainActivity.account,
                MainActivity.region).credentialsProvider).updateCommandShadow(Appliance.currentAppliance.getName(),
                Appliance.currentAppliance.getApplianceType().toString(),
                context.getString(R.string.appVersion), "ledOn", String.valueOf(onOff)); //Todo: Change from hardcode
        onOff = !onOff;
    }

    /**
     * Sets the parameters on the current command.
     * @param machineName this is the name that the argument should be sent as.
     * @param value the value being inputted.
     */
    public static void setParameterOnCurrentCommand(String machineName, Object value)
    {
        if (currentCommand != null)
            for (int i = 0; i < currentCommand.parameters.length; i++)
                if (currentCommand.parameters[i].machineName.equals(machineName))
                    currentCommand.parameters[i].value = value;
    }
}
