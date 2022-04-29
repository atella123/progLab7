package lab.data;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lab.common.data.DataManager;
import lab.common.data.Person;

public class PersonCollectionManager implements DataManager<Person> {

    private final LocalDate initDate;
    private Collection<Person> collection;
    private final TreeSet<Integer> idSet = new TreeSet<>();

    public PersonCollectionManager() {
        idSet.add(0);
        initDate = LocalDate.now();
    }

    public PersonCollectionManager(Collection<Person> collection) {
        initDate = LocalDate.now();
        this.collection = collection;
        idSet.add(0);
        idSet.addAll(collection.stream().map(Person::getID).collect(Collectors.toSet()));
    }

    @Override
    public boolean allMatches(Predicate<Person> predicate) {
        return collection.stream().allMatch(predicate);
    }

    @Override
    public boolean add(Person person) {
        if (idSet.contains(person.getID())) {
            person.setID(idSet.last() + 1);
        }
        idSet.add(person.getID());
        this.collection.add(person);
        return true;
    }

    @Override
    public boolean addIfAllMatches(Person person, Predicate<Person> predicate) {
        if (!allMatches(predicate)) {
            return false;
        }
        collection.add(person);
        return true;
    }

    @Override
    public void remove(Person person) {
        idSet.remove(person.getID());
        collection.remove(person);
    }

    @Override
    public void removeAll(Collection<Person> collectionToRemove) {
        collection.removeAll(collectionToRemove);
    }

    @Override
    public boolean removeByID(int id) {
        return collection.removeIf(p -> p.getID() == id);
    }

    @Override
    public void removeMatches(Predicate<Person> predicate) {
        collection.removeIf(predicate);
    }

    @Override
    public boolean removeIfAllMatches(Person person, Predicate<Person> predicate) {
        if (!allMatches(predicate)) {
            return false;
        }
        collection.remove(person);
        return true;
    }

    @Override
    public Optional<Person> getByID(int id) {
        return collection.stream().filter(p -> p.getID().equals(id)).findFirst();
    }

    @Override
    public Collection<Person> getMatches(Predicate<Person> predicate) {
        return collection.stream().filter(predicate).collect(Collectors.toSet());
    }

    @Override
    public Collection<Person> getAsCollection() {
        return Collections.unmodifiableCollection(collection);
    }

    @Override
    public boolean updateID(int id, Person person) {
        Optional<Person> personToRemove = getByID(id);
        if (!personToRemove.isPresent()) {
            return false;
        }
        collection.remove(personToRemove.get());
        collection.add(person);
        return true;
    }

    @Override
    public void clear() {
        collection.clear();
        idSet.clear();
        idSet.add(0);
    }

    @Override
    public LocalDate getInitDate() {
        return initDate;
    }

    @Override
    public String getDataSourceType() {
        return "java.util.HashSet";
    }
}
