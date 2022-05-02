package lab.common.util;

import lab.common.commands.CommandResponse;
import lab.common.io.IOManager;

public interface CommandRunner<R> {

    void run();

    CommandResponse runNextCommand();

    IOManager<R, CommandResponse> getIO();

    void setIO(IOManager<R, CommandResponse> io);

}
