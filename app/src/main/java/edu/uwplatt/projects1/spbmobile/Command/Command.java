package edu.uwplatt.projects1.spbmobile.Command;

import edu.uwplatt.projects1.spbmobile.Appliance;

public class Command {
    public static Command[] commandsForList;
    public String humanName;
    boolean priority;
    public Parameter[] parameters;
    State[] states;
    String cmdName;
    /**
     * Used to execute a command
     *
     * @param appliance The Appliance to execute the command on.
     */
    void execute(Appliance appliance) {

    }
}
