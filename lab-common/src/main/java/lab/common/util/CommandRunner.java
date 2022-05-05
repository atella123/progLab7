package lab.common.util;

import lab.common.commands.CommandResponse;
import lab.common.io.IOManager;

public interface CommandRunner<R, W extends CommandResponse> {

    void run();

    W run(R request);

    W runNextCommand();

    IOManager<R, W> getIO();

}
