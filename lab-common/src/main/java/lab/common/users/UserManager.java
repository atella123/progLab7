package lab.common.users;

public interface UserManager {

    void registerNewUser(User user);

    void deleteUser(User user);

    boolean isValidUsed(String username, String password);

}
