package lab.common.commands.datacommands;

import lab.common.commands.CommandResponse;
import lab.common.commands.BasicCommand;

public interface DataCommand extends BasicCommand {

    CommandResponse execute(User user, Object... args);

}
