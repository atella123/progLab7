package lab.common.io;

@FunctionalInterface
public interface Reader<T> {
    T read();
}
