package lab.common.commands;

import lab.common.data.DataManager;
import lab.common.data.Person;
import lab.common.users.User;

public final class AddIfMax extends AbstractDataCommand {

    public AddIfMax() {
        super();
    }

    public AddIfMax(DataManager<Person> manager) {
        super(manager);
    }

    @Override
    public CommandResponse execute(User user, Object... args) {
        if (!isExecutableInstance) {
            return new CommandResponse(CommandResult.ERROR, "Execute called on unexecutable instance");
        }
        if (!isVaildArgument(args)) {
            return new CommandResponse(CommandResult.ERROR, "Illegal argument");
        }
        Person p = (Person) args[0];
        getManager().addIfAllMatches(p, p2 -> p.compareTo(p) > 0);
        return new CommandResponse(CommandResult.SUCCESS);
    }

    @Override
    public String toString() {
        return "AddIfMax";
    }

    @Override
    public String getMan() {
        return "add_if_max {element} : добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции";
    }

    @Override
    public boolean isVaildArgument(Object... args) {
        return args.length > 0 && args[0] instanceof Person;
    }

    @Override
    public Class<?>[] getArgumentClasses() {
        return new Class<?>[] {
                Person.class };
    }
}
