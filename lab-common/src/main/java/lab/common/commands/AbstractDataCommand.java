package lab.common.commands;

import lab.common.data.DataManager;
import lab.common.data.Person;

public abstract class AbstractDataCommand extends AbstractCommand {

    private DataManager<Person> manager;

    public AbstractDataCommand() {
        super();
    }

    public AbstractDataCommand(DataManager<Person> manager) {
        super(true);
        this.manager = manager;
    }

    @Override
    public String toString() {
        return "CollectionCommand";
    }

    public DataManager<Person> getManager() {
        return manager;
    }

    public void setManager(DataManager<Person> manager) {
        this.manager = manager;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((manager == null) ? 0 : manager.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractDataCommand other = (AbstractDataCommand) obj;
        if (manager == null) {
            return other.manager == null;
        }
        return manager.equals(other.manager);
    }

}
