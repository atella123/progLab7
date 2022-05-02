package lab.common.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import lab.common.exceptions.IllegalFieldValueException;

public final class Person implements Comparable<Person>, Serializable {

    private Integer id; // Поле не может быть null, Значение поля должно быть больше 0, Значение этого
    // поля должно быть уникальным, Значение этого поля должно генерироваться
    // автоматически
    private String name; // Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; // Поле не может быть null
    private LocalDate creationDate; // Поле не может быть null, Значение этого поля должно
                                    // генерироваться
                                    // автоматически
    private int height; // Значение поля должно быть больше 0
    private String passportID; // Длина строки должна быть не меньше 10, Поле может быть null
    private Color eyeColor; // Поле может быть null
    private Country nationality; // Поле не может быть null
    private Location location; // Поле не может быть null

    private Person() {
    }

    public Integer getID() {
        return id;
    }

    public void setID(Integer newID) {
        this.id = newID;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public java.time.LocalDate getCreationDate() {
        return creationDate;
    }

    public int getHeight() {
        return height;
    }

    public String getPassportID() {
        return passportID;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public Country getNationality() {
        return nationality;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((coordinates == null) ? 0 : coordinates.hashCode());
        result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
        result = prime * result + ((eyeColor == null) ? 0 : eyeColor.hashCode());
        result = prime * result + height;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((nationality == null) ? 0 : nationality.hashCode());
        result = prime * result + ((passportID == null) ? 0 : passportID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Person other = (Person) obj;
        if (!coordinates.equals(other.coordinates)) {
            return false;
        }
        if (!creationDate.equals(other.creationDate)) {
            return false;
        }
        if (eyeColor != other.eyeColor) {
            return false;
        }
        if (height != other.height) {
            return false;
        }
        if (!id.equals(other.id)) {
            return false;
        }
        if (!location.equals(other.location)) {
            return false;
        }
        if (!name.equals(other.name)) {
            return false;
        }
        if (nationality != other.nationality) {
            return false;
        }
        return !passportID.equals(other.passportID);
    }

    @Override
    public String toString() {
        return "Person [name=" + name + ", creationDate=" + creationDate + ", eyeColor=" + eyeColor
                + ", height=" + height + ", id=" + id + ", location=" + location + ", [coordinates=" + coordinates
                + " +, nationality=" + nationality + ", passportID=" + passportID + "]";
    }

    @Override
    public int compareTo(Person person) {
        return this.name.length() - person.name.length();
    }

    public static final class Validator {
        private static final int MIN_H = 10;

        private Validator() {
        }

        public static boolean isValidName(String name) {
            if (Objects.nonNull(name)) {
                return !name.isEmpty();
            }
            return false;
        }

        public static boolean isValidCoordinates(Coordinates coordinates) {
            return Objects.nonNull(coordinates);
        }

        public static boolean isValidHeight(int height) {
            return height > 0;
        }

        public static boolean isValidPassportID(String passportID) {
            if (Objects.nonNull(passportID)) {
                return passportID.length() >= MIN_H;
            }
            return false;
        }

        public static boolean isValidLocation(Location location) {
            return Objects.nonNull(location);
        }
    }

    public static class Builder {

        private Person person;

        public Builder() {
            this.person = new Person();
        }

        public Builder(Person person) {
            this.person = new Person();
            this.person.id = person.id;
            this.person.name = person.name;
            this.person.coordinates = person.coordinates;
            this.person.creationDate = person.creationDate;
            this.person.height = person.height;
            this.person.passportID = person.passportID;
            this.person.height = person.height;
            this.person.passportID = person.passportID;
            this.person.eyeColor = person.eyeColor;
            this.person.nationality = person.nationality;
            this.person.location = person.location;
        }

        public Builder setId(Integer id) {
            person.id = id;
            return this;
        }

        public Builder setName(String name) {
            if (!Validator.isValidName(name)) {
                throw new IllegalFieldValueException();
            }
            person.name = name;
            return this;
        }

        public Builder setCoordinates(Coordinates coordinates) {
            if (!Validator.isValidCoordinates(coordinates)) {
                throw new IllegalFieldValueException();
            }
            person.coordinates = coordinates;
            return this;
        }

        public Builder setHeight(int height) {
            if (!Validator.isValidHeight(height)) {
                throw new IllegalFieldValueException();
            }
            person.height = height;
            return this;
        }

        public Builder setPassportID(String passportID) {
            if (!Validator.isValidPassportID(passportID)) {
                throw new IllegalFieldValueException();
            }
            person.passportID = passportID;
            return this;
        }

        public Builder setEyeColor(Color eyeColor) {
            person.eyeColor = eyeColor;
            return this;
        }

        public Builder setNationality(Country nationality) {
            person.nationality = nationality;
            return this;
        }

        public Builder setLocation(Location location) {
            if (!Validator.isValidLocation(location)) {
                throw new IllegalFieldValueException();
            }
            person.location = location;
            return this;
        }

        public Builder setCreationDate(LocalDate date) {
            person.creationDate = date;
            return this;
        }

        public Person build() {
            return person;
        }
    }
}
