package lab.common.commands;

public interface BasicCommand {

    String getMan();

    boolean isValidArgument(Object... args);

    Class<?>[] getArgumentClasses();

    boolean isExecutableInstance();

}
