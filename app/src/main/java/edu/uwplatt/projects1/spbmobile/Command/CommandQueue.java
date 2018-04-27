package edu.uwplatt.projects1.spbmobile.Command;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * This class represents a command queue.
 */
public class CommandQueue {

    /**
     * The default constructor.
     */
    public CommandQueue() {
    }

    /**
     * This constructor takes in the Command and the Date for execution.
     * @param command the Command to be modeled.
     * @param date    the Date for execution.
     */
    CommandQueue(Command command, Date date) {
        addCommand(command, date);
    }

    @SerializedName("commandQueue")
    private Queue<CommandModel> commandModelQueue = new PriorityQueue<>();

    public void addCommand(Command command, Date date) {
        commandModelQueue.add(new CommandModel(command, date));
    }
}
