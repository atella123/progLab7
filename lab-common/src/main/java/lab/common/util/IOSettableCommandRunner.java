package lab.common.util;

import lab.common.commands.CommandResponse;
import lab.common.io.IOManager;

public interface IOSettableCommandRunner<R, W extends CommandResponse> extends CommandRunner<R, W> {

    void setIO(IOManager<R, W> io);

}
