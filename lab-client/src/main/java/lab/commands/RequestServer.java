package lab.commands;

import java.util.Arrays;
import java.util.Map;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.commands.datacommands.DataCommand;
import lab.common.commands.datacommands.User;
import lab.common.commands.AbstractCommand;
import lab.common.io.IOManager;
import lab.common.util.ArgumentParser;
import lab.common.util.DataCommandExecuteRequest;

public final class RequestServer extends AbstractCommand {

    private final User user;
    private final IOManager<CommandResponse, DataCommandExecuteRequest> io;
    private final Map<String, DataCommand> commandsMap;
    private final ArgumentParser<String> argumentParser;

    public RequestServer(User user, IOManager<CommandResponse, DataCommandExecuteRequest> io,
            Map<String, DataCommand> commandsMap,
            ArgumentParser<String> argumentParser) {
        super(true);
        this.user = user;
        this.io = io;
        this.commandsMap = commandsMap;
        this.argumentParser = argumentParser;
    }

    @Override
    public CommandResponse execute(Object... args) {
        if (!isVaildArgument(args)) {
            return new CommandResponse(CommandResult.ERROR, "Illegal argument");
        }
        if (!commandsMap.containsValue(args[0])) {
            return new CommandResponse(CommandResult.ERROR, "Unknown command");
        }
        DataCommand command = (DataCommand) args[0];
        Object[] parsedArgs = argumentParser.parseArguments(command,
                Arrays.copyOfRange(args, 1, args.length, String[].class));
        if (!command.isVaildArgument(parsedArgs)) {
            return new CommandResponse(CommandResult.ERROR, "Illegal argument");
        }
        DataCommandExecuteRequest commandWithArguments = new DataCommandExecuteRequest(user, command.getClass(),
                parsedArgs);
        io.write(commandWithArguments);
        return io.readLine();
    }

    @Override
    public boolean isVaildArgument(Object... args) {
        return args.length > 0;
    }

    @Override
    public Class<?>[] getArgumentClasses() {
        return new Class[] {
                DataCommand.class };
    }

    @Override
    public String getMan() {
        return "request_server command_name arguments... : запросить сервер выполнить указанную комманду с указанными аргументами";
    }

    @Override
    public String toString() {
        return "RequestServer";
    }

}
