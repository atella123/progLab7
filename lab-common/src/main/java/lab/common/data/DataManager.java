package lab.common.data;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public interface DataManager<T> {

    boolean allMatches(Predicate<T> predicate);

    boolean add(T t);

    boolean addIfAllMatches(T t, Predicate<T> predicate);

    void remove(T t);

    void removeAll(Collection<T> t);

    boolean removeByID(int id);

    void removeMatches(Predicate<T> predicate);

    boolean removeIfAllMatches(T t, Predicate<T> predicate);

    Optional<Person> getByID(int id);

    Collection<T> getMatches(Predicate<T> predicate);

    boolean updateID(int id, T t);

    Collection<T> getAsCollection();

    void clear();

    LocalDate getInitDate();

    String getDataSourceType();
}
