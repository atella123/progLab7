package lab.common.data;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface DataManager<T> {

    public void add(T t);

    public boolean addIfMax(T t);

    public void remove(T t);

    public boolean removeByID(int id);

    public void removeGreater(T t);

    public Optional<Person> getByID(int id);

    public Collection<T> getAsCollection();

    public boolean updateID(int id, T t);

    public void clear();

    public LocalDate getInitDate();

    public String getDataSourceType();
}
