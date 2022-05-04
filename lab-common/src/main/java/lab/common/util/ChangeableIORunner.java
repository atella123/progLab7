package lab.common.util;

import lab.common.commands.CommandResponse;
import lab.common.io.IOManager;

public interface ChangeableIORunner<R, W extends CommandResponse> extends CommandRunner {

    IOManager<R, W> getIO();

    void setIO(IOManager<R, W> io);

}
