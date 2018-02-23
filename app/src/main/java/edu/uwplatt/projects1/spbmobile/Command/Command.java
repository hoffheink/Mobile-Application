package edu.uwplatt.projects1.spbmobile.Command;

import edu.uwplatt.projects1.spbmobile.Appliance;

public class Command {
    String humanName;
    boolean priority;
    Parameter[] parameters;
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
