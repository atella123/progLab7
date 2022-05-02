package lab.data;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lab.common.commands.datacommands.DataManagerResponse;
import lab.common.commands.datacommands.OwnedDataManager;
import lab.common.commands.datacommands.User;
import lab.common.data.Color;
import lab.common.data.Coordinates;
import lab.common.data.Country;
import lab.common.data.Location;
import lab.common.data.Person;

public class PersonDBManager implements OwnedDataManager<Person> {

    private static final Logger LOGGER = LogManager.getLogger(lab.data.PersonDBManager.class);

    private static final String INIT_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS Persons("
            + "id INT PRIMARY KEY, owner_name VARCHAR(30),"
            + "name TEXT, coordinates_x REAL NOT NULL,"
            + "coordinates_y INT NOT NULL, creation_date DATE NOT NULL,"
            + "height INT NOT NULL, passport_id TEXT NOT NULL,"
            + "eye_color VARCHAR(10) NOT NULL, country VARCHAR(20) NOT NULL,"
            + "location_x REAL NOT NULL, location_y BIGINT NOT NULL,"
            + "location_name TEXT NOT NULL,"
            + "CONSTRAINT owner FOREIGN KEY(owner_name) REFERENCES users(name) ON DELETE SET NULL);"
            + "CREATE SEQUENCE IF NOT EXISTS personID OWNED BY persons.id;";

    private static final String PREPARED_INSERT_QUERY = "INSERT INTO persons (name,"
            + "coordinates_x,coordinates_y,creation_date,"
            + "height,passport_id,eye_color,country,"
            + "location_x,location_y,location_name, id, owner_name)"
            + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);";

    private static final String PREPARED_UPDATE_QUERY = "UPDATE persons SET name = ?,"
            + "coordinates_x = ?,coordinates_y = ?,creation_date = ?,"
            + "height = ?,passport_id = ?,eye_color = ?,country = ?,"
            + "location_x = ?,location_y = ?,location_name = ?"
            + "WHERE id = ?;";

    private static final String INVALID_USER_MESSAGE = "Invalid username or password";

    private static final int NAME_INDEX = 1;
    private static final int COORD_X_INDEX = 2;
    private static final int COORD_Y_INDEX = 3;
    private static final int CREATION_DATE_INDEX = 4;
    private static final int HEIGHT_INDEX = 5;
    private static final int PASSPORT_ID_INDEX = 6;
    private static final int EYE_COLOR_INDEX = 7;
    private static final int NATIONALITY_INDEX = 8;
    private static final int LOCATION_X_INDEX = 9;
    private static final int LOCATION_Y_INDEX = 10;
    private static final int LOCATION_NAME_INDEX = 11;
    private static final int ID_INDEX = 12;
    private static final int OWNER_NAME_INDEX = 13;

    private final Connection connection;
    private final UserDBManager userManager;
    private final PersonCollectionManager collectionManager;
    private final LocalDate timestamp = LocalDate.now();

    public PersonDBManager(Connection connection, MessageDigest hashFunction) throws SQLException {
        this.connection = connection;
        this.userManager = new UserDBManager(connection, hashFunction);
        this.collectionManager = new PersonCollectionManager();
        createTable();
        setCollectionFromDB();
    }

    public PersonDBManager(String url, String user, String password, MessageDigest hashFunction) throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
        this.userManager = new UserDBManager(connection, hashFunction);
        this.collectionManager = new PersonCollectionManager();
        createTable();
        setCollectionFromDB();
    }

    private void createTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(INIT_TABLE_QUERY);
        }
    }

    private void setCollectionFromDB() throws SQLException {
        HashSet<Person> collection = new HashSet<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM persons");
            while (resultSet.next()) {
                collection.add(getPersonFromResult(resultSet));
            }
        }
        collectionManager.setCollection(collection);
    }

    private Person getPersonFromResult(ResultSet result) throws SQLException {
        Person.Builder builder = new Person.Builder();

        builder.setId(result.getInt("id"));
        builder.setName(result.getString("name"));
        builder.setHeight(result.getInt("height"));
        builder.setCreationDate(result.getDate("creation_date").toLocalDate());
        builder.setPassportID(result.getString("passport_id"));
        builder.setEyeColor(Color.valueOf(result.getString("eye_color").toUpperCase()));
        builder.setNationality(Country.valueOf(result.getString("country").toUpperCase()));

        float coordinatesX = result.getFloat("coordinates_x");
        int coordinatesY = result.getInt("coordinates_y");
        builder.setCoordinates(new Coordinates(coordinatesX, coordinatesY));

        float locationX = result.getFloat("location_x");
        long locationY = result.getLong("location_y");
        String locationName = result.getString("location_name");
        builder.setLocation(new Location(locationX, locationY, locationName));

        return builder.build();
    }

    private boolean isPresent(int id) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT EXISTS(SELECT 1 FROM persons WHERE id = ?);")) {

            statement.setInt(1, id);

            statement.execute();

            ResultSet result = statement.getResultSet();

            result.next();

            return result.getBoolean(1);

        } catch (SQLException e) {
            LOGGER.error("Check if person exists in DB failed: {}", e.getMessage());
        }
        return false;
    }

    private boolean isOwner(User user, int id) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM users WHERE name = (SELECT owner_name FROM persons WHERE id = ?)")) {

            statement.setInt(1, id);
            statement.execute();

            ResultSet result = statement.getResultSet();
            result.next();

            return userManager.compareUsers(user, new User(result.getString(1), result.getString(2)));

        } catch (SQLException e) {
            LOGGER.error("Couldn't check if {} is owner of Person with id {}: {}", user.getUsername(), id,
                    e.getMessage());
        }
        return false;
    }

    public boolean allMatches(Predicate<Person> predicate) {
        return collectionManager.allMatches(predicate);
    }

    @Override
    public DataManagerResponse add(User user, Person person) {

        if (!userManager.isRegisteredUser(user)) {
            return new DataManagerResponse(false, "Couldn't add person to DB, user doens't exist");
        }

        try (Statement statement = connection.createStatement();
                PreparedStatement insertStatement = connection.prepareStatement(PREPARED_INSERT_QUERY)) {

            ResultSet result = statement.executeQuery("SELECT nextval('personID');");

            result.next();

            insertStatement.setInt(ID_INDEX, result.getInt(1));
            insertStatement.setString(NAME_INDEX, person.getName());
            insertStatement.setString(OWNER_NAME_INDEX, user.getUsername());
            insertStatement.setFloat(COORD_X_INDEX, person.getCoordinates().getX());
            insertStatement.setInt(COORD_Y_INDEX, person.getCoordinates().getY());
            insertStatement.setDate(CREATION_DATE_INDEX, Date.valueOf(person.getCreationDate()));
            insertStatement.setInt(HEIGHT_INDEX, person.getHeight());
            insertStatement.setString(PASSPORT_ID_INDEX, person.getPassportID());
            insertStatement.setString(EYE_COLOR_INDEX, person.getEyeColor().toString());
            insertStatement.setString(NATIONALITY_INDEX, person.getNationality().toString());
            insertStatement.setFloat(LOCATION_X_INDEX, person.getLocation().getX());
            insertStatement.setLong(LOCATION_Y_INDEX, person.getLocation().getY());
            insertStatement.setString(LOCATION_NAME_INDEX, person.getLocation().getName());

            insertStatement.execute();

            collectionManager.add(new Person.Builder(person).setId(result.getInt(1)).build());
        } catch (SQLException e) {
            LOGGER.error("An error occurred while trying to write to database: {}", e.getMessage());
            return new DataManagerResponse(false, "An error occurred while trying to add person");
        }

        return new DataManagerResponse();
    }

    @Override
    public DataManagerResponse addIfAllMatches(User user, Person person, Predicate<Person> predicate) {
        if (!allMatches(predicate)) {
            return new DataManagerResponse();
        }
        return add(user, person);
    }

    @Override
    public DataManagerResponse remove(User user, Person person) {
        return removeByID(user, person.getID());
    }

    @Override
    public DataManagerResponse removeAll(User user, Collection<Person> collectionToRemove) {
        for (Person person : collectionToRemove) {
            removeByID(user, person.getID());
        }
        return new DataManagerResponse();
    }

    // Здесь пришлось редактировать конфиг чекстайла, ибо как небольно сделать 4
    // точки выхода придумать не получилось
    @Override
    public DataManagerResponse removeByID(User user, int id) {
        if (!isPresent(id)) {
            return new DataManagerResponse(false, String.format("No element with id %d is present", id));
        }
        if (!userManager.isRegisteredUser(user)) {
            return new DataManagerResponse(false, INVALID_USER_MESSAGE);
        }
        if (!isOwner(user, id)) {
            return new DataManagerResponse(false, "Person is owned by another user");
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM persons WHERE id = ? RETURNING id")) {
            statement.setInt(1, id);
            statement.execute();

            ResultSet result = statement.getResultSet();

            if (result.next()) {
                collectionManager.removeByID(id);
            }

        } catch (SQLException e) {
            LOGGER.error("Failed to delete person: {}", e.getMessage());
            return new DataManagerResponse(false, "An error occured when trying to delete person");
        }
        return new DataManagerResponse();

    }

    @Override
    public DataManagerResponse removeMatches(User user, Predicate<Person> predicate) {
        return removeAll(user, getMatches(predicate));
    }

    @Override
    public DataManagerResponse removeIfAllMatches(User user, Person person, Predicate<Person> predicate) {
        if (allMatches(predicate)) {
            return remove(user, person);
        }
        return new DataManagerResponse();
    }

    @Override
    public Optional<Person> getByID(int id) {
        return collectionManager.getByID(id);
    }

    @Override
    public Collection<Person> getMatches(Predicate<Person> predicate) {
        return collectionManager.getMatches(predicate);
    }

    @Override
    public DataManagerResponse updateID(User user, int id, Person person) {
        if (!isPresent(id)) {
            return new DataManagerResponse(false, String.format("No element with id %d is present", id));
        }
        if (!userManager.isRegisteredUser(user)) {
            return new DataManagerResponse(false, INVALID_USER_MESSAGE);
        }
        if (!isOwner(user, id)) {
            return new DataManagerResponse(false, "Person is owned by another user");
        }
        try (PreparedStatement statement = connection.prepareStatement(PREPARED_UPDATE_QUERY)) {

            statement.setString(NAME_INDEX, person.getName());
            statement.setFloat(COORD_X_INDEX, person.getCoordinates().getX());
            statement.setInt(COORD_Y_INDEX, person.getCoordinates().getY());
            statement.setDate(CREATION_DATE_INDEX, Date.valueOf(person.getCreationDate()));
            statement.setInt(HEIGHT_INDEX, person.getHeight());
            statement.setString(PASSPORT_ID_INDEX, person.getPassportID());
            statement.setString(EYE_COLOR_INDEX, person.getEyeColor().toString());
            statement.setString(NATIONALITY_INDEX, person.getNationality().toString());
            statement.setFloat(LOCATION_X_INDEX, person.getLocation().getX());
            statement.setLong(LOCATION_Y_INDEX, person.getLocation().getY());
            statement.setString(LOCATION_NAME_INDEX, person.getLocation().getName());
            statement.setInt(ID_INDEX, id);

            statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Failed to update person: {}", e.getMessage());
            return new DataManagerResponse(false, "An error occured when trying to update person");
        }
        collectionManager.updateID(id, person);
        return new DataManagerResponse();
    }

    @Override
    public Collection<Person> getAsCollection() {
        return collectionManager.getCollection();
    }

    @Override
    public DataManagerResponse clear(User user) {
        if (!userManager.isRegisteredUser(user)) {
            return new DataManagerResponse(false, INVALID_USER_MESSAGE);
        }
        try (PreparedStatement statement = connection
                .prepareStatement("DELETE FROM persons WHERE owner_name = ? RETURNING id;")) {
            statement.setString(1, user.getUsername());

            statement.execute();

            ResultSet result = statement.getResultSet();

            while (result.next()) {
                collectionManager.removeByID(result.getInt(1));
            }

        } catch (SQLException e) {
            LOGGER.error("Failed to delete person: {}", e.getMessage());
            return new DataManagerResponse(false, "An error occured when trying to delete person");
        }
        return new DataManagerResponse();
    }

    @Override
    public LocalDate getInitDate() {
        return timestamp;
    }

    @Override
    public String getDataSourceType() {
        return collectionManager.getDataSourceType();
    }

    public UserDBManager getUserManager() {
        return userManager;
    }

}
