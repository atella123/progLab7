package lab.common.commands;

import lab.common.data.PersonCollectionManager;

public abstract class CollectionCommand extends Command {

    private PersonCollectionManager manager;

    public CollectionCommand() {
        super();
    }

    public CollectionCommand(PersonCollectionManager manager) {
        super(true);
        this.manager = manager;
    }

    @Override
    public String toString() {
        return "CollectionCommand";
    }

    public PersonCollectionManager getManager() {
        return manager;
    }

    public void setManager(PersonCollectionManager manager) {
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
        CollectionCommand other = (CollectionCommand) obj;
        if (manager == null) {
            return other.manager == null;
        }
        return manager.equals(other.manager);
    }

}
