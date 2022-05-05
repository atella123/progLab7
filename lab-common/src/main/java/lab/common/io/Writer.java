package lab.common.io;

@FunctionalInterface
public interface Writer<T> {
    void write(T message);
}
