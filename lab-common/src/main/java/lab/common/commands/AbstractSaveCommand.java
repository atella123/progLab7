package lab.common.commands;

import lab.common.data.DataManager;
import lab.common.data.Person;

public abstract class AbstractSaveCommand extends AbstractDataCommand {

    public AbstractSaveCommand(DataManager<Person> manager) {
        super(manager);
    }

    public AbstractSaveCommand() {
    }

}
