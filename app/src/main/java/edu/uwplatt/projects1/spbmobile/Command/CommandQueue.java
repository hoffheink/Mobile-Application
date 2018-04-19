package edu.uwplatt.projects1.spbmobile.Command;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.PriorityQueue;
import java.util.Queue;

public class CommandQueue {

    public CommandQueue() {
    }

    CommandQueue(Command command, Date date) {
        addCommand(command, date);
    }

    @SerializedName("commandQueue")
    private Queue<CommandModel> commandModelQueue = new PriorityQueue<>();

    public void addCommand(Command command, Date date) {
        commandModelQueue.add(new CommandModel(command, date));
    }
}
