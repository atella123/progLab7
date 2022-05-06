package lab.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lab.common.commands.datacommands.User;
import lab.common.util.UserManager;
import lab.util.MessageDigestHasher;

public final class UserDBManager implements UserManager {

    private static final Logger LOGGER = LogManager.getLogger(lab.data.UserDBManager.class);

    private static final String INIT_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS Users("
            + "name VARCHAR(30) PRIMARY KEY,"
            + "password bytea NOT NULL);";

    private final Connection connection;

    public UserDBManager(Connection connection)
            throws SQLException {
        this.connection = connection;
        createTable();
    }

    public UserDBManager(String url, String user, String password)
            throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
        createTable();
    }

    private void createTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(INIT_TABLE_QUERY);
        }
    }

    @Override
    public boolean addUser(User user) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO users(name, password) VALUES (?, ?);")) {

            int i = 1;
            statement.setString(i++, user.getUsername());
            statement.setBytes(i, MessageDigestHasher.getHashFromBytes(user.getPassword().getBytes()));

            statement.execute();

            LOGGER.info("Added new user to DB: {}", user.getUsername());
            return true;

        } catch (SQLException e) {
            LOGGER.error("Inserting new user to DB failed: {}", e.getLocalizedMessage());
        }
        return false;
    }

    @Override
    public boolean removeUser(User user) {
        if (!isRegisteredUser(user)) {
            return false;
        }
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM users WHERE name = ?;")) {

            statement.setString(1, user.getUsername());

            statement.execute();

            LOGGER.info("Deleted user from DB: {}", user.getUsername());
            return true;

        } catch (SQLException e) {
            LOGGER.error("Deleting user from DB failed: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean isUsernameTaken(User user) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT EXISTS(SELECT 1 FROM users WHERE name = ?);")) {

            statement.setString(1, user.getUsername());

            statement.execute();

            ResultSet result = statement.getResultSet();

            result.next();

            return result.getBoolean(1);

        } catch (SQLException e) {
            LOGGER.error("Check if user exists in DB failed: {}", e.getMessage());
        }
        return false;

    }

    @Override
    public boolean isRegisteredUser(User user) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT EXISTS(SELECT 1 FROM users WHERE name = ? AND password = ?);")) {

            statement.setString(1, user.getUsername());
            statement.setBytes(2, MessageDigestHasher.getHashFromBytes(user.getPassword().getBytes()));

            statement.execute();

            ResultSet result = statement.getResultSet();

            result.next();

            return result.getBoolean(1);

        } catch (SQLException e) {
            LOGGER.error("Check if user exists in DB failed: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean isValidUser(User user) {

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT password FROM users WHERE name = ?;")) {

            statement.setString(1, user.getUsername());
            statement.execute();
            ResultSet result = statement.getResultSet();

            if (!result.next()) {
                return false;
            }

            return Arrays.equals(MessageDigestHasher.getHashFromBytes(user.getPassword().getBytes()),
                    result.getBytes(1));
        } catch (SQLException e) {
            LOGGER.error("Reading user failed: {}", e.getMessage());
            return false;
        }

    }

}
