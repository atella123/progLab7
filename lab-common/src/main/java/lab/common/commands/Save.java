package lab.common.commands;

import lab.common.data.DataManager;
import lab.common.data.Person;

public abstract class Save extends CollectionCommand {

    public Save(DataManager<Person> manager) {
        super(manager);
    }

    public Save() {
    }

}
