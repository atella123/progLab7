package lab.data;

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

import lab.common.data.Color;
import lab.common.data.Coordinates;
import lab.common.data.Country;
import lab.common.data.DataManager;
import lab.common.data.Location;
import lab.common.data.Person;

public class PersonDBManager implements DataManager<Person> {

    private static final Logger LOGGER = LogManager.getLogger(lab.data.PersonDBManager.class);

    private static final String INIT_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS Users("
            + "id SERIAL PRIMARY KEY,"
            + "name VARCHAR(30) NOT NULL,"
            + "password VARCHAR(30) NOT NULL);"
            + "CREATE TABLE IF NOT EXISTS Persons("
            + "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
            + "name TEXT NOT NULL,"
            + "coordinates_x REAL NOT NULL,"
            + "coordinates_y INT NOT NULL,"
            + "creation_date DATE NOT NULL,"
            + "height INT NOT NULL,"
            + "passport_id TEXT NOT NULL,"
            + "eye_color VARCHAR(10) NOT NULL,"
            + "country VARCHAR(20) NOT NULL,"
            + "location_x REAL NOT NULL,"
            + "location_y BIGINT NOT NULL,"
            + "location_name TEXT NOT NULL,"
            + "CONSTRAINT owner_id FOREIGN KEY(id) "
            + "REFERENCES users(id));";

    private static final String PREPARED_INSERT_QUERY = "INSERT INTO persons (name,"
            + "coordinates_x,coordinates_y,creation_date,"
            + "height,passport_id,eye_color,country,"
            + "location_x,location_y,location_name)"
            + "VALUES (?,?,?,?,?,?,?,?,?,?,?);";

    private static final String PREPARED_UPDATE_QUERY = "UPDATE persons SET name = ?,"
            + "coordinates_x = ?,coordinates_y = ?,creation_date = ?,"
            + "height = ?,passport_id = ?,eye_color = ?,country = ?,"
            + "location_x = ?,location_y = ?,location_name = ?"
            + "WHERE id = ?";

    private final Connection connection;
    private final PersonCollectionManager collectionManager;
    private final LocalDate timestamp = LocalDate.now();

    public PersonDBManager(Connection connection) throws SQLException {
        this.connection = connection;
        createTable();
        this.collectionManager = new PersonCollectionManager(getCollectionFromDB());
    }

    public PersonDBManager(String url, String user, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
        createTable();
        this.collectionManager = new PersonCollectionManager(getCollectionFromDB());
    }

    private void createTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(INIT_TABLE_QUERY);
        }
    }

    private Collection<Person> getCollectionFromDB() throws SQLException {
        HashSet<Person> collection = new HashSet<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM persons");
            while (resultSet.next()) {
                collection.add(getPersonFromResult(resultSet));
            }
        }
        return collection;
    }

    private Person getPersonFromResult(ResultSet result) throws SQLException {
        Person.Builder builder = new Person.Builder();

        builder.setId(result.getInt("id"));
        builder.setName(result.getString("name"));
        builder.setHeigth(result.getInt("height"));
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

    public boolean allMatches(Predicate<Person> predicate) {
        return collectionManager.allMatches(predicate);
    }

    @Override
    public boolean add(Person person) {
        try (PreparedStatement statement = connection.prepareStatement(PREPARED_INSERT_QUERY)) {
            int i = 1;

            statement.setString(i++, person.getName());
            statement.setFloat(i++, person.getCoordinates().getX());
            statement.setInt(i++, person.getCoordinates().getY());
            statement.setDate(i++, Date.valueOf(person.getCreationDate()));
            statement.setInt(i++, person.getHeight());
            statement.setString(i++, person.getPassportID());
            statement.setString(i++, person.getEyeColor().toString());
            statement.setString(i++, person.getNationality().toString());
            statement.setFloat(i++, person.getLocation().getX());
            statement.setLong(i++, person.getLocation().getY());
            statement.setString(i++, person.getLocation().getName());

            statement.execute();
            collectionManager.add(person);

        } catch (SQLException e) {
            LOGGER.error("An error occuried while trying to write to database: {}", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean addIfAllMatches(Person person, Predicate<Person> predicate) {
        if (!allMatches(predicate) || !add(person)) {
            return false;
        }
        collectionManager.add(person);
        return true;
    }

    @Override
    public void remove(Person person) {
        removeByID(person.getID());
    }

    @Override
    public void removeAll(Collection<Person> collectionToRemove) {
        for (Person person : collectionToRemove) {
            removeByID(person.getID());
        }
    }

    @Override
    public boolean removeByID(int id) {
        if (!collectionManager.getByID(id).isPresent()) {
            return false;
        }
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM persons WHERE id = ?")) {
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        collectionManager.removeByID(id);
        return true;
    }

    @Override
    public void removeMatches(Predicate<Person> predicate) {
        removeAll(getMatches(predicate));
    }

    @Override
    public boolean removeIfAllMatches(Person person, Predicate<Person> predicate) {
        if (!allMatches(predicate)) {
            return false;
        }
        remove(person);
        return true;
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
    public boolean updateID(int id, Person person) {
        if (!collectionManager.updateID(id, person)) {
            return false;
        }
        try (PreparedStatement statement = connection.prepareStatement(PREPARED_UPDATE_QUERY)) {
            int i = 1;
            statement.setString(i++, person.getName());
            statement.setFloat(i++, person.getCoordinates().getX());
            statement.setInt(i++, person.getCoordinates().getY());
            statement.setDate(i++, Date.valueOf(person.getCreationDate()));
            statement.setInt(i++, person.getHeight());
            statement.setString(i++, person.getPassportID());
            statement.setString(i++, person.getEyeColor().toString());
            statement.setString(i++, person.getNationality().toString());
            statement.setFloat(i++, person.getLocation().getX());
            statement.setLong(i++, person.getLocation().getY());
            statement.setString(i++, person.getLocation().getName());

            statement.setInt(i++, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Collection<Person> getAsCollection() {
        return collectionManager.getAsCollection();
    }

    @Override
    public void clear() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        collectionManager.clear();
    }

    @Override
    public LocalDate getInitDate() {
        return timestamp;
    }

    @Override
    public String getDataSourceType() {
        return collectionManager.getDataSourceType();
    }

}
