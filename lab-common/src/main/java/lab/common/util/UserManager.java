package lab.common.util;

import lab.common.data.commands.User;

public interface UserManager {

    boolean addUser(User user);

    boolean removeUser(User user);

    boolean isUsernameTaken(User user);

    boolean isRegisteredUser(User user);

    boolean compareUsers(User u1, User u2);

}
