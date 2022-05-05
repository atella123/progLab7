package lab.util;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import lab.common.commands.Command;
import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.io.IOManager;
import lab.common.util.AbstractStringCommandRunner;
import lab.common.util.ArgumentParser;

public class ServerCommandRunner extends AbstractStringCommandRunner {

    public ServerCommandRunner(ArgumentParser<String> argumentParser, Map<String, Command> commandsMap,
            IOManager<String, CommandResponse> io) {
        super(argumentParser, commandsMap, io);
    }

    @Override
    public CommandResponse run(String request) {
        Command command = parseCommand(request);
        if (Objects.isNull(command)) {
            return new CommandResponse(CommandResult.ERROR, "Unknown command");
        }
        Object[] args = getArgumentParser().parseArguments(command, parseArgumentsFromString(request));
        return command.execute(args);
    }

    @Override
    public Command parseCommand(String arg) {
        String cmd = arg.trim().split("\\s+")[0];
        return getCommandsMap().get(cmd);
    }

    @Override
    public String[] parseArgumentsFromString(String arg) {
        String[] splitString = arg.trim().split("\\s+");
        return Arrays.copyOfRange(splitString, 1, splitString.length);
    }

}
