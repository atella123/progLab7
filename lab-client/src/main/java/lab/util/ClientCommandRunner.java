package lab.util;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import lab.commands.RequestServer;
import lab.common.commands.Command;
import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.commands.datacommands.DataCommand;
import lab.common.io.IOManager;
import lab.common.util.AbstractStringCommandRunner;
import lab.common.util.ArgumentParser;
import lab.common.util.CommandRunnerWithHistory;
import lab.common.util.DataCommandExecuteRequest;
import lab.io.DatagramSocketIOManager;
import lab.parsers.UserParser;

public class ClientCommandRunner extends AbstractStringCommandRunner implements CommandRunnerWithHistory<String> {

    private static final int HISTORY_SIZE = 11;

    private final RequestServer requestCommand;
    private final ArrayList<String> history = new ArrayList<>(HISTORY_SIZE);

    public ClientCommandRunner(Map<String, Command> clientCommands,
            Map<String, DataCommand> serverCommands,
            ArgumentParser<String> argumentParser,
            InetSocketAddress serverAddress,
            IOManager<String, CommandResponse> io) throws SocketException {
        super(argumentParser, clientCommands, io);
        IOManager<CommandResponse, DataCommandExecuteRequest> serverIO = new DatagramSocketIOManager(serverAddress);
        this.requestCommand = new RequestServer(UserParser.readUser(System.out::println, io, serverIO), serverIO,
                serverCommands, argumentParser);
    }

    @Override
    public CommandResponse runNextCommand() {
        String nextLine = getIO().readLine();
        if (Objects.isNull(nextLine)) {
            return new CommandResponse(CommandResult.NO_INPUT);
        }
        Command command = parseCommand(nextLine);
        Object[] args = getArgumentParser().parseArguments(command, parseArgumentsFromString(nextLine));
        if (command != requestCommand) {
            updateHistory(command.toString());
        }
        if (args.length > 0) {
            updateHistory(args[0].toString());
        }
        return command.execute(args);
    }

    @Override
    public Command parseCommand(String arg) {
        String cmd = arg.trim().split("\\s+")[0];
        return getCommandsMap().getOrDefault(cmd, requestCommand);
    }

    @Override
    public String[] parseArgumentsFromString(String arg) {
        String[] splittedString = arg.trim().split("\\s+");
        if (parseCommand(arg) != requestCommand) {
            splittedString = Arrays.copyOfRange(splittedString, 1, splittedString.length);
        }
        return splittedString;
    }

    private void updateHistory(String update) {
        if (history.size() >= HISTORY_SIZE) {
            history.remove(0);
        }
        history.add(update);
    }

    @Override
    public Collection<String> getHistory() {
        return history;
    }

}
