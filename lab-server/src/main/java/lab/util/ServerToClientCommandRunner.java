package lab.util;

import java.util.Map;
import java.util.Objects;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.data.commands.DataCommand;
import lab.common.io.IOManager;
import lab.common.util.CommandRunner;
import lab.common.util.DataCommandExecuteRequest;

public class ServerToClientCommandRunner
        implements CommandRunner<DataCommandExecuteRequest> {

    private IOManager<DataCommandExecuteRequest, CommandResponse> io;
    private final Map<Class<? extends DataCommand>, DataCommand> commandsMap;

    public ServerToClientCommandRunner(Map<Class<? extends DataCommand>, DataCommand> commandsMap,
            IOManager<DataCommandExecuteRequest, CommandResponse> io) {
        this.commandsMap = commandsMap;
        this.io = io;
    }

    // TODO ?
    @Override
    public void run() {
        CommandResponse resp;
        do {
            resp = runNextCommand();
            getIO().write(resp);
        } while (!resp.getResult().equals(CommandResult.END));
    }

    @Override
    public CommandResponse runNextCommand() {
        DataCommandExecuteRequest nextRequest = io.readLine();
        if (Objects.isNull(nextRequest)) {
            return new CommandResponse(CommandResult.NO_INPUT);
        }
        DataCommand command = commandsMap.get(nextRequest.getCommandClass());
        if (Objects.isNull(command)) {
            return new CommandResponse(CommandResult.ERROR, "Unknown command");
        }
        return command.execute(nextRequest.getUser(), nextRequest.getArgumnets());
    }

    @Override
    public IOManager<DataCommandExecuteRequest, CommandResponse> getIO() {
        return io;
    }

    @Override
    public void setIO(IOManager<DataCommandExecuteRequest, CommandResponse> newIO) {
        this.io = newIO;

    }

}
