package lab.util;

import java.util.Map;
import java.util.Objects;

import lab.common.commands.CommandResult;
import lab.common.commands.datacommands.DataCommand;
import lab.common.io.IOManager;
import lab.common.util.CommandRunner;
import lab.io.ServerExecuteRequest;
import lab.io.ServerResponse;

public class ServerToClientCommandRunner implements CommandRunner<ServerExecuteRequest, ServerResponse> {

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
    public ServerResponse run(ServerExecuteRequest request) {
        DataCommand command = commandsMap.get(request.getCommandClass());
        if (Objects.isNull(command)) {
            return new ServerResponse(CommandResult.ERROR, "Unknown command", request.getClientAddress());
        }
        return new ServerResponse(command.execute(request.getUser(), request.getArguments()),
                request.getClientAddress());
    }

    @Override
    public ServerResponse runNextCommand() {
        ServerExecuteRequest nextRequest = io.read();
        if (Objects.isNull(nextRequest)) {
            return new ServerResponse(CommandResult.NO_INPUT, null);
        }
        return run(nextRequest);
    }

    @Override
    public IOManager<ServerExecuteRequest, ServerResponse> getIO() {
        return io;
    }
}
