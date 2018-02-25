package edu.uwplatt.projects1.spbmobile.Command;

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

    public static void executeCurrentCommand() {

    }

    /**
     * Sets the parameters on th current command.
     * @param machineName this is the name that the argument should be sent as.
     * @param value the value being inputted.
     */
    public static void setParameterOnCurrentCommand(String machineName, Object value) {
        if (currentCommand != null)
            for (int i = 0; i < currentCommand.parameters.length; i++)
                if (currentCommand.parameters[i].machineName.equals(machineName))
                    currentCommand.parameters[i].value = value;
    }
}
