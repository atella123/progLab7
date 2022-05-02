package lab.common.util;

import lab.common.commands.datacommands.User;

public interface UserManager {

    boolean addUser(User user);

    boolean removeUser(User user);

    boolean isUsernameTaken(User user);

    boolean isRegisteredUser(User user);

    boolean isValidUser(User user);

}
