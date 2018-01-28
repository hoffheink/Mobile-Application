package edu.uwplatt.projects1.spbmobile;

/**
 * Created by sosinskin on 1/28/2018.
 */

/**
 * This interface represents a command to be executed.
 */
public interface ICommand {
    /**
     * Used to execute a command
     * @param appliance The Appliance to execute the command on.
     */
    void execute(Appliance appliance);
}
