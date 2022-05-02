package lab.common.commands;

public interface BasicCommand {

    String getMan();

    boolean isVaildArgument(Object... args);

    Class<?>[] getArgumentClasses();

    boolean isExecutableInstance();

}
