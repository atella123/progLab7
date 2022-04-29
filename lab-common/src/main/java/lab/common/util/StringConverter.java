package lab.common.util;

import java.util.function.Function;

@FunctionalInterface
public interface StringConverter<T> extends Function<String, T> {
    T apply(String f);
}
