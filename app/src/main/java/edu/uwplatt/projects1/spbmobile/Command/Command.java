package edu.uwplatt.projects1.spbmobile.Command;

import android.content.Context;
import android.os.StrictMode;
import android.support.annotation.NonNull;

import com.amazonaws.auth.AWSSessionCredentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.UUID;

import edu.uwplatt.projects1.spbmobile.Appliance.Appliance;
import edu.uwplatt.projects1.spbmobile.CloudDatasource;
import edu.uwplatt.projects1.spbmobile.JsonHelpers.RangeTypeAdapter;
import edu.uwplatt.projects1.spbmobile.MainActivity;
import edu.uwplatt.projects1.spbmobile.R;
import edu.uwplatt.projects1.spbmobile.Shadow.AwsIotShadowClient;
import edu.uwplatt.projects1.spbmobile.Time;

/**
 * This class is used to model Commands.
 */
public class Command {
    public static Command currentCommand;
    public String humanName;
    private boolean priority;
    public Parameter[] parameters;
    State[] states;
    private String cmdName;

    private static UUID getRandomUUID() {
        return UUID.randomUUID();
    }

    public static class CommandQueue {
        public CommandQueue() {

        }

        CommandQueue(Command command) {
            addCommand(command);
        }

        private class CommandModel {
            private class Properties {
                private int priority;
                private UUID guid;
                private String timestamp;

                Properties(boolean isPriority) {
                    if (isPriority)
                        priority = 1;
                    else
                        priority = 0;
                    guid = getRandomUUID();
                    timestamp = Time.getUTCTime(new Date());
                }

                public Properties(boolean isPriority, UUID inGuid) {
                    this(isPriority);
                    guid = inGuid;
                }
            }

            private final String cmdName;
            private final Properties properties;
            private HashMap<String, Object> arguments = new HashMap<>();

            CommandModel(Command command) {
                cmdName = command.cmdName;
                properties = new Properties(command.priority);
                for (Parameter parameter : command.parameters) {
                    arguments.put(parameter.machineName, parameter.value);
                }
            }
        }

        @SerializedName("commandQueue")
        Queue<CommandModel> commandModelQueue = new PriorityQueue<>();

        void addCommand(Command command) {
            commandModelQueue.add(new CommandModel(command));
        }
    }

    public static void executeCurrentCommand(@NonNull Context context) throws Exception {
        CommandQueue commandQueue = new CommandQueue(currentCommand);
        CloudDatasource datasource = CloudDatasource.getInstance(context, MainActivity.account, MainActivity.region);
        AWSSessionCredentials credentials = datasource.getCredentials();
        if (credentials != null)
        {
            AwsIotShadowClient client = AwsIotShadowClient.getInstance(credentials);
            client.updateCommandShadow(
                    Appliance.currentAppliance.getName(),
                    Appliance.currentAppliance.getApplianceType().toString(),
                    (String) context.getText(R.string.appVersion),
                    commandQueue);
        }
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
