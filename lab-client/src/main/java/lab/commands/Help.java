package lab.commands;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lab.common.commands.AbstractCommand;
import lab.common.commands.BasicCommand;
import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;

public final class Help extends AbstractCommand {

    private final Set<Map<?, ? extends BasicCommand>> commandMaps = new HashSet<>();

    public Help() {
        super(true);
    }

    public void addCommandMap(Map<?, ? extends BasicCommand> commandMap) {
        this.commandMaps.add(commandMap);
    }

    @Override
    public CommandResponse execute(Object... args) {
        if (!isExecutableInstance) {
            return new CommandResponse(CommandResult.ERROR, "Execute called on unexecutable instance");
        }
        return new CommandResponse(CommandResult.SUCCESS,
                commandMaps.stream().flatMap(x -> x.values().stream()).map(BasicCommand::getMan)
                        .collect(Collectors.joining("\n")));
    }

    @Override
    public String toString() {
        return "Help";
    }

    @Override
    public String getMan() {
        return "help : вывести справку по доступным командам";
    }

    @Override
    public boolean isVaildArgument(Object... args) {
        return true;
    }

    @Override
    public Class<?>[] getArgumentClasses() {
        return new Class<?>[0];
    }

}
