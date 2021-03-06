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
        if (!isExecutableInstance()) {
            return new CommandResponse(CommandResult.ERROR, "Execute called on non executable instance");
        }
        if (!isValidArgument(args)) {
            return new CommandResponse(CommandResult.ERROR, "Unknown command");
        }
        DataCommand command = (DataCommand) args[0];
        Object[] parsedArgs = argumentParser.parseArguments(command,
                Arrays.copyOfRange(args, 1, args.length, String[].class));
        if (!command.isValidArgument(parsedArgs)) {
            return new CommandResponse(CommandResult.ERROR, "Illegal argument");
        }
        DataCommandExecuteRequest commandWithArguments = new DataCommandExecuteRequest(user, command.getClass(),
                parsedArgs);
        io.write(commandWithArguments);
        return io.read();
    }

    @Override
    public boolean isValidArgument(Object... args) {
        return args.length > 0 && commandsMap.containsValue(args[0]);
    }

    @Override
    public Class<?>[] getArgumentClasses() {
        return new Class[] {
                DataCommand.class };
    }

    @Override
    public String getMan() {
        return "request_server command_name arguments... : ?????????????????? ???????????? ?????????????????? ?????????????????? ???????????????? ?? ???????????????????? ??????????????????????";
    }

    @Override
    public String toString() {
        return "RequestServer";
    }

}
