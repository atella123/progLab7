package lab.util;

import java.util.Arrays;
import java.util.Map;

import lab.common.commands.Command;
import lab.common.commands.CommandResponse;
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
        String[] splittedString = arg.trim().split("\\s+");
        return Arrays.copyOfRange(splittedString, 1, splittedString.length);
    }

}
