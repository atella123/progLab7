package lab.util;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.util.CommandRunner;
import lab.common.util.DataCommandExecuteRequest;

public final class PersonCollectionServer implements Runnable {

    private final CommandRunner<DataCommandExecuteRequest> serverCommandRunner;

    public PersonCollectionServer(CommandRunner<DataCommandExecuteRequest> serverToClientCommandRunner) {
        this.serverCommandRunner = serverToClientCommandRunner;
    }

    @Override
    public void run() {
        CommandResponse resp;
        do {
            resp = serverCommandRunner.runNextCommand();
        } while (resp.getResult() == CommandResult.NO_INPUT);
    }
}
