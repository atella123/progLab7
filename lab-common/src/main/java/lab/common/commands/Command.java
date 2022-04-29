package lab.common.commands;

import lab.common.users.User;

public interface Command {

    boolean isVaildArgument(Object... args);

    Class<?>[] getArgumentClasses();

    CommandResponse execute(User user, Object... args);

    String getMan();

}
