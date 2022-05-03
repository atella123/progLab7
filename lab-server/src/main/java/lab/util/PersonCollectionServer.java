package lab.util;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.util.CommandRunner;
import lab.io.ServerExecuteRequest;

public final class PersonCollectionServer {

    private final CommandRunner<String> serverCommandRunner;
    private final CommandRunner<ServerExecuteRequest> serverToClientCommandRunner;

    public PersonCollectionServer(CommandRunner<String> serverCommandRunner,
            CommandRunner<ServerExecuteRequest> serverToClientCommandRunner) {
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
