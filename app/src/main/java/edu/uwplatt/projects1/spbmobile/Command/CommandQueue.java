package edu.uwplatt.projects1.spbmobile.Command;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * This class represents a command queue.
 */
public class CommandQueue {

    @SerializedName("commandQueue")
    private Queue<CommandModel> commandModelQueue = new PriorityQueue<>();

    /**
     * The default constructor.
     */
    public CommandQueue() {
    }

    /**
     * This constructor takes in the Command.
     *
     * @param command the Command to be modeled.
     */
    CommandQueue(Command command) {
        addCommand(command);
    }

    /**
     * This method adds a Command to the queue.
     *
     * @param command the Command to be added to the queue.
     * @param date    the Date the Command was created.
     */
    public void addCommand(Command command, Date date) {
        commandModelQueue.add(new CommandModel(command, date));
    }

    /**
     * This method adds a Command to the queue.
     *
     * @param command the Command to be added to the queue.
     */
    private void addCommand(Command command) {
        commandModelQueue.add(new CommandModel(command, new Date()));
    }
}
