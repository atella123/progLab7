package lab.commands;

import java.util.stream.Collectors;

import lab.common.commands.AbstractCommand;
import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.commands.ValidityChecker;
import lab.common.util.CommandRunnerWithHistory;

public final class History extends AbstractCommand {

    private CommandRunnerWithHistory<?, ?> commandRunner;

    public History() {
        super();
    }

    public History(CommandRunnerWithHistory<?, ?> commandRunner) {
        super(true);
        this.commandRunner = commandRunner;
    }

    @Override
    public CommandResponse execute(Object... args) {
        CommandResponse validity = ValidityChecker.checkValidity(this, args);
        if (validity.getResult() != CommandResult.SUCCESS) {
            return validity;
        }
        return new CommandResponse(CommandResult.SUCCESS,
                commandRunner.getHistory().stream().map(Object::toString).collect(Collectors.joining("\n")));
    }

    @Override
    public String toString() {
        return "History";
    }

    @Override
    public String getMan() {
        return "history : вывести последние 11 команд (без их аргументов)";
    }

    @Override
    public boolean isValidArgument(Object... args) {
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((commandRunner == null) ? 0 : commandRunner.hashCode());
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
        History other = (History) obj;
        if (commandRunner == null) {
            return other.commandRunner == null;
        }
        return commandRunner.equals(other.commandRunner);
    }

    @Override
    public Class<?>[] getArgumentClasses() {
        return new Class<?>[0];
    }

}
