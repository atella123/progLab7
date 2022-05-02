package lab.common.commands.datacommands;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public interface OwnedDataManager<T> {

    boolean allMatches(Predicate<T> predicate);

    DataManagerResponse add(User user, T t);

    DataManagerResponse addIfAllMatches(User user, T t, Predicate<T> predicate);

    DataManagerResponse remove(User user, T t);

    DataManagerResponse removeAll(User user, Collection<T> t);

    DataManagerResponse removeByID(User user, int id);

    DataManagerResponse removeMatches(User user, Predicate<T> predicate);

    DataManagerResponse removeIfAllMatches(User user, T t, Predicate<T> predicate);

    Optional<T> getByID(int id);

    Collection<T> getMatches(Predicate<T> predicate);

    DataManagerResponse updateID(User user, int id, T t);

    DataManagerResponse clear(User user);

    Collection<T> getAsCollection();

    LocalDate getInitDate();

    String getDataSourceType();
}
