package edu.uwplatt.projects1.spbmobile;

/**
 * This interface represents a command to be executed.
 */
public interface ICommand {
    /**
     * Used to execute a command
     *
     * @param appliance The Appliance to execute the command on.
     */
    void execute(Appliance appliance);
}
