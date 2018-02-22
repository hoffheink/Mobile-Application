package edu.uwplatt.projects1.spbmobile.Command;
import edu.uwplatt.projects1.spbmobile.Appliance;

public abstract class ACommand {
    /**
     * Used to execute a command
     *
     * @param appliance The Appliance to execute the command on.
     */
    abstract void execute(Appliance appliance);

    String cmdName;
    String humanName;
    boolean priority;
    Parameter[] parameters;
    State[] states;
}
