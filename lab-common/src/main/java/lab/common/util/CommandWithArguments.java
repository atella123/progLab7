package lab.common.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lab.common.commands.AbstractCommand;

public final class CommandWithArguments implements Serializable {

    final Class<? extends AbstractCommand> commandClass;
    final ArrayList<Object> arguments;

    public CommandWithArguments(Class<? extends AbstractCommand> commandClass, Object... arguments) {
        this.commandClass = commandClass;
        this.arguments = new ArrayList<>(Arrays.asList(arguments));
    }

    public Class<? extends AbstractCommand> getCommandClass() {
        return commandClass;
    }

    public List<Object> getArgumnets() {
        return arguments;
    }

}
