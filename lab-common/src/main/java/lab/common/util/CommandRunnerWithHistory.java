package lab.common.util;

import java.util.Collection;

public interface CommandRunnerWithHistory extends CommandRunner {

    Collection<String> getHistory();

}
