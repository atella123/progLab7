package lab.common.data;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

import lab.common.data.commands.User;

public interface OwnedDataManager<T> {

    boolean allMatches(Predicate<T> predicate);

    DataManagerResponse<T> add(User user, T t);

    DataManagerResponse<T> addIfAllMatches(User user, T t, Predicate<T> predicate);

    DataManagerResponse<T> remove(User user, T t);

    DataManagerResponse<T> removeAll(User user, Collection<T> t);

    DataManagerResponse<T> removeByID(User user, int id);

    DataManagerResponse<T> removeMatches(User user, Predicate<T> predicate);

    DataManagerResponse<T> removeIfAllMatches(User user, T t, Predicate<T> predicate);

    Optional<T> getByID(int id);

    Collection<T> getMatches(Predicate<T> predicate);

    DataManagerResponse<T> updateID(User user, int id, T t);

    DataManagerResponse<T> clear(User user);

    Collection<T> getAsCollection();

    LocalDate getInitDate();

    String getDataSourceType();
}
