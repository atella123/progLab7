package lab.parsers;

import java.time.LocalDate;

import lab.common.data.Color;
import lab.common.data.Coordinates;
import lab.common.data.Country;
import lab.common.data.Location;
import lab.common.data.Person;
import lab.common.io.IOManager;

public final class PersonParser {

    private PersonParser() {
        throw new UnsupportedOperationException();
    }

    public static Person parsePerson(IOManager<String, String> io) {
        Person.Builder builder = new Person.Builder();
        builder.setName(parseName(io));
        builder.setCoordinates(parseCoordinates(io));
        builder.setCreationDate(LocalDate.now());
        builder.setHeight(parseHeight(io));
        builder.setPassportID(parsePassportID(io));
        builder.setEyeColor(parseEyeColor(io));
        builder.setNationality(parseNationality(io));
        builder.setLocation(parseLocation(io));
        return builder.build();
    }

    public static String parseName(IOManager<String, String> io) {
        io.write("Enter person name");
        return DataReader.readValidString(io, Person.Validator::isValidName, "Person name can't be empty");
    }

    public static Coordinates parseCoordinates(IOManager<String, String> io) {
        io.write("Enter person coordinates");
        return CoordinatesParser.parseCoordinates(io);
    }

    public static int parseHeight(IOManager<String, String> io) {
        io.write("Enter person height");
        return DataReader.readStringAsValidObject(io, Integer::parseInt, Person.Validator::isValidHeight,
                "Enter positive value", "Enter valid Integer", false);
    }

    public static String parsePassportID(IOManager<String, String> io) {
        io.write("Enter person passportID");
        return DataReader.readValidString(io, Person.Validator::isValidPassportID,
                "passportID has min lenght (10)");
    }

    public static Color parseEyeColor(IOManager<String, String> io) {
        io.write("Enter person eyeColor");
        return DataReader.readEnumValueOrIndex(io, Color.class);
    }

    public static Country parseNationality(IOManager<String, String> io) {
        io.write("Enter person nationality");
        return DataReader.readEnumValueOrIndex(io, Country.class);
    }

    public static Location parseLocation(IOManager<String, String> io) {
        io.write("Enter person location");
        return LocationParser.parseLocation(io);
    }

}
