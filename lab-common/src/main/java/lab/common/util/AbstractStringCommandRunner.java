package lab.common.util;

import java.util.Map;

import lab.common.commands.Command;
import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.io.IOManager;

public abstract class AbstractStringCommandRunner implements ChangeableIORunner<String, CommandResponse> {

    private final ArgumentParser<String> argumentParser;
    private final Map<String, Command> commandsMap;
    private IOManager<String, CommandResponse> io;

    public AbstractStringCommandRunner(ArgumentParser<String> argumentParser, Map<String, Command> commandsMap,
            IOManager<String, CommandResponse> io) {
        this.argumentParser = argumentParser;
        this.commandsMap = commandsMap;
        this.io = io;
    }

    @Override
    public void run() {
        CommandResponse resp;
        do {
            resp = runNextCommand();
            getIO().write(resp);
        } while (!resp.getResult().equals(CommandResult.END) && !resp.getResult().equals(CommandResult.NO_INPUT));
    }

    protected final Map<String, Command> getCommandsMap() {
        return commandsMap;
    }

    protected final ArgumentParser<String> getArgumentParser() {
        return argumentParser;
    }

    public abstract Command parseCommand(String arg);

    public abstract String[] parseArgumentsFromString(String arg);

    public IOManager<String, CommandResponse> getIO() {
        return io;
    }

    @Override
    public void setIO(IOManager<String, CommandResponse> newIO) {
        this.io = newIO;
    }

}
