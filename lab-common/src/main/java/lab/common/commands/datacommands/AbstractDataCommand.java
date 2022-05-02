package lab.common.commands.datacommands;

import lab.common.data.Person;

public abstract class AbstractDataCommand implements DataCommand {

    protected final boolean executableInstance;
    private OwnedDataManager<Person> manager;

    public AbstractDataCommand() {
        executableInstance = false;
    }

    protected AbstractDataCommand(boolean isExecutableInstance) {
        this.executableInstance = isExecutableInstance;
    }

    public AbstractDataCommand(OwnedDataManager<Person> manager) {
        executableInstance = true;
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
        return executableInstance;
    }

}
