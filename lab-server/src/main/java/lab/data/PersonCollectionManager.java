package lab.data;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lab.common.data.Person;

public final class PersonCollectionManager {

    private Collection<Person> collection;

    public PersonCollectionManager() {

    }

    public PersonCollectionManager(Collection<Person> collection) {
        this.collection = collection;
    }

    public boolean allMatches(Predicate<Person> predicate) {
        return collection.stream().allMatch(predicate);
    }

    public void add(Person person) {
        this.collection.add(person);
    }

    public boolean addIfAllMatches(Person person, Predicate<Person> predicate) {
        if (!allMatches(predicate)) {
            return false;
        }
        collection.add(person);
        return true;
    }

    public void remove(Person person) {
        collection.remove(person);
    }

    public void removeAll(Collection<Person> collectionToRemove) {
        collection.removeAll(collectionToRemove);
    }

    public void removeByID(int id) {
        collection.removeIf(p -> p.getID().equals(id));
    }

    public void removeMatches(Predicate<Person> predicate) {
        collection.removeIf(predicate);
    }

    public boolean removeIfAllMatches(Person person, Predicate<Person> predicate) {
        if (!allMatches(predicate)) {
            return false;
        }
        collection.remove(person);
        return true;
    }

    public Optional<Person> getByID(int id) {
        return collection.stream().filter(p -> p.getID().equals(id)).findFirst();
    }

    public Collection<Person> getMatches(Predicate<Person> predicate) {
        return collection.stream().filter(predicate).collect(Collectors.toSet());
    }

    public boolean updateID(int id, Person person) {
        Optional<Person> personToRemove = getByID(id);
        if (!personToRemove.isPresent()) {
            return false;
        }
        collection.remove(personToRemove.get());
        collection.add(person);
        return true;
    }

    public void clear() {
        collection.clear();
    }

    public String getDataSourceType() {
        return collection.getClass().getName();
    }

    public Collection<Person> getCollection() {
        return collection;
    }

    public void setCollection(Collection<Person> collection) {
        this.collection = collection;
    }
}
