package edu.uwplatt.projects1.spbmobile.Command;

import com.google.gson.annotations.SerializedName;

import java.util.PriorityQueue;
import java.util.Queue;

public class CommandQueue {

    public CommandQueue() {
    }

    CommandQueue(Command command) {
        addCommand(command);
    }

    @SerializedName("commandQueue")
    Queue<CommandModel> commandModelQueue = new PriorityQueue<>();

    void addCommand(Command command) {
        commandModelQueue.add(new CommandModel(command));
    }
}
