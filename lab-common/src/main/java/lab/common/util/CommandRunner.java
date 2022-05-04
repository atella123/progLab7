package lab.common.util;

import lab.common.commands.CommandResponse;

public interface CommandRunner {

    void run();

    CommandResponse runNextCommand();

}
