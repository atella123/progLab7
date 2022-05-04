package lab.util;

import java.util.Map;
import java.util.Objects;

import lab.common.commands.CommandResult;
import lab.common.commands.datacommands.DataCommand;
import lab.common.io.IOManager;
import lab.common.util.CommandRunner;
import lab.io.ServerExecuteRequest;
import lab.io.ServerResponse;

public class ServerToClientCommandRunner implements CommandRunner {

    private final IOManager<ServerExecuteRequest, ServerResponse> io;
    private final Map<Class<? extends DataCommand>, DataCommand> commandsMap;

    public ServerToClientCommandRunner(Map<Class<? extends DataCommand>, DataCommand> commandsMap,
            IOManager<ServerExecuteRequest, ServerResponse> io) {
        this.commandsMap = commandsMap;
        this.io = io;
    }

    @Override
    public void run() {
        ServerResponse resp;
        do {
            resp = runNextCommand();
            io.write(resp);
        } while (!resp.getResult().equals(CommandResult.END));
    }

    @Override
    public ServerResponse runNextCommand() {
        ServerExecuteRequest nextRequest = io.readLine();
        if (Objects.isNull(nextRequest)) {
            return new ServerResponse(CommandResult.NO_INPUT, null);
        }
        DataCommand command = commandsMap.get(nextRequest.getCommandClass());
        if (Objects.isNull(command)) {
            return new ServerResponse(CommandResult.ERROR, "Unknown command", nextRequest.getClientAddress());
        }
        return new ServerResponse(command.execute(nextRequest.getUser(), nextRequest.getArgumnets()),
                nextRequest.getClientAddress());
    }
}
