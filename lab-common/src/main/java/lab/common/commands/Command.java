package lab.common.commands;

public interface Command extends BasicCommand {

    CommandResponse execute(Object... args);

}
