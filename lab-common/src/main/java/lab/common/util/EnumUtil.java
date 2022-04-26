package lab.common.util;

import java.util.Arrays;

import lab.common.io.IOManager;

public final class EnumUtil {
    private EnumUtil() {
    }

    public static <E extends Enum<E>> boolean isEnumValue(String s, Class<E> e) {
        E[] constants = e.getEnumConstants();
        return Arrays.stream(constants).map(Object::toString).anyMatch(x -> x.equals(s));
    }

    public static <E extends Enum<E>> void printEnumValues(IOManager<String, String> io, Class<E> e) {
        for (E i : e.getEnumConstants()) {
            io.write(i.toString());
        }
    }
}
