package lab.data;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.TreeSet;
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
    public void add(Person person) {
        if (idSet.contains(person.getID())) {
            person.setID(idSet.last() + 1);
        }
        idSet.add(person.getID());
        this.collection.add(person);
    }

    @Override
    public boolean addIfMax(Person person) {
        if (collection.stream().min((p1, p2) -> p1.compareTo(p2)).isPresent()) {
            collection.add(person);
            return true;
        }
        return false;
    }

    @Override
    public void remove(Person person) {
        idSet.remove(person.getID());
        collection.remove(person);
    }

    @Override
    public boolean removeByID(int id) {
        return collection.removeIf(p -> p.getID() == id);
    }

    @Override
    public void removeGreater(Person person) {
        collection.removeIf(p -> p.compareTo(person) > 0);
    }

    @Override
    public Optional<Person> getByID(int id) {
        return collection.stream().filter(p -> p.getID().equals(id)).findFirst();
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