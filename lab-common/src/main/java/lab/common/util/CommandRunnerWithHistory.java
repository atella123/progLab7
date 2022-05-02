package lab.common.util;

import java.util.Collection;

public interface CommandRunnerWithHistory<R> extends CommandRunner<R> {

    Collection<String> getHistory();

}
