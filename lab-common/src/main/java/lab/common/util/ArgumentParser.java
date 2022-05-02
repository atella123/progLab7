package lab.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

import lab.common.commands.BasicCommand;
import lab.common.exceptions.NoParserAvailableException;

public class ArgumentParser<T> {

    private final HashMap<Class<?>, Function<T, ?>> classParsers = new HashMap<>();

    public ArgumentParser() {
        add(Object.class, x -> x);
    }

    public <A> void add(Class<A> clazz, Function<T, A> converter) {
        classParsers.put(clazz, converter);
    }

    public void remove(Class<?> clazz) {
        classParsers.remove(clazz);
    }

    public Object convert(Class<?> clazz, T argument) {
        if (clazz.isInstance(argument)) {
            return argument;
        }
        if (!classParsers.containsKey(clazz)) {
            throw new NoParserAvailableException("For " + clazz);
        }
        return classParsers.get(clazz).apply(argument);
    }

    public Object[] parseArguments(BasicCommand command, T[] argumentsToParse) {
        Class<?>[] argumentClasses = command.getArgumentClasses();
        ArrayList<Object> parsedArguments = new ArrayList<>(argumentClasses.length);
        parsedArguments.addAll(Arrays.asList(argumentsToParse));
        if (argumentClasses.length > argumentsToParse.length) {
            parsedArguments.addAll(Arrays.asList(new Object[argumentClasses.length - argumentsToParse.length]));
        }
        T nextArgumentToParse = null;
        for (int i = 0; i < argumentClasses.length; i++) {
            if (argumentsToParse.length > i) {
                nextArgumentToParse = argumentsToParse[i];
            }
            Object nextArg = convert(argumentClasses[i], nextArgumentToParse);
            if (Objects.isNull(nextArg)) {
                return new Object[0];
            }
            parsedArguments.set(i, nextArg);
        }
        return parsedArguments.toArray();
    }

    public boolean canParse(Class<?> clazz) {
        return classParsers.containsKey(clazz);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + classParsers.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ArgumentParser<?> other = (ArgumentParser<?>) obj;
        return classParsers.equals(other.classParsers);
    }

}
