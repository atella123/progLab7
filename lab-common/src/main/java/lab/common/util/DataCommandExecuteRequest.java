package lab.common.util;

import java.io.Serializable;

import lab.common.data.commands.DataCommand;
import lab.common.data.commands.User;

public final class DataCommandExecuteRequest implements Serializable {

    final User user;
    final Object[] arguments;
    final Class<? extends DataCommand> commandClass;

    public DataCommandExecuteRequest(User user, Class<? extends DataCommand> commandClass, Object... arguments) {
        this.user = user;
        this.commandClass = commandClass;
        this.arguments = arguments;
    }

    public User getUser() {
        return user;
    }

    public Object[] getArgumnets() {
        return arguments;
    }

    public Class<? extends DataCommand> getCommandClass() {
        return commandClass;
    }

}
