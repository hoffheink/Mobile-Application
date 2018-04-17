package edu.uwplatt.projects1.spbmobile.Command;

import java.util.HashMap;

class CommandModel {
    private final String cmdName;
    private final CommandProperties properties;
    private HashMap<String, Object> arguments = new HashMap<>();

    CommandModel(Command command) {
        cmdName = command.cmdName;
        properties = new CommandProperties(command.priority);
        for (Parameter parameter : command.parameters) {
            arguments.put(parameter.machineName, parameter.value);
        }
    }
}
