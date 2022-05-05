package lab.common.util;

import java.util.Collection;

import lab.common.commands.CommandResponse;

public interface CommandRunnerWithHistory<R, W extends CommandResponse> extends CommandRunner<R, W> {

    Collection<String> getHistory();

}
