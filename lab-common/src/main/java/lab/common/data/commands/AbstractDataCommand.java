package lab.common.data.commands;

import lab.common.data.OwnedDataManager;
import lab.common.data.Person;

public abstract class AbstractDataCommand implements DataCommand {

    protected final boolean isExecutableInstance;
    private OwnedDataManager<Person> manager;

    public AbstractDataCommand() {
        isExecutableInstance = false;
    }

    protected AbstractDataCommand(boolean isExecutableInstance) {
        this.isExecutableInstance = isExecutableInstance;
    }

    public AbstractDataCommand(OwnedDataManager<Person> manager) {
        isExecutableInstance = true;
        this.manager = manager;
    }

    @Override
    public String toString() {
        return "CollectionCommand";
    }

    public OwnedDataManager<Person> getManager() {
        return manager;
    }

    public void setManager(OwnedDataManager<Person> manager) {
        this.manager = manager;
    }

    public boolean isExecutableInstance() {
        return isExecutableInstance;
    }

}
