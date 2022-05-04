package lab.util;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.util.CommandRunner;

public final class PersonCollectionServer {

    private final CommandRunner serverCommandRunner;
    private final CommandRunner serverToClientCommandRunner;

    public PersonCollectionServer(CommandRunner serverCommandRunner,
            CommandRunner serverToClientCommandRunner) {
        this.serverCommandRunner = serverCommandRunner;
        this.serverToClientCommandRunner = serverToClientCommandRunner;
    }

    public void run() {
        CommandResponse resp;
        do {
            // TODO
            serverToClientCommandRunner.run();
            resp = serverCommandRunner.runNextCommand();
        } while (resp.getResult() == CommandResult.NO_INPUT);
    }
}
