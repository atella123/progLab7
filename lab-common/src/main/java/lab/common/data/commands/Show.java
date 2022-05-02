package lab.common.data.commands;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.data.OwnedDataManager;
import lab.common.data.Person;

public final class Show extends AbstractDataCommand {

    public Show() {
        super();
    }

    public Show(OwnedDataManager<Person> manager) {
        super(manager);
    }

    @Override
    public CommandResponse execute(User user, Object... args) {
        if (!isExecutableInstance) {
            return new CommandResponse(CommandResult.ERROR, "Execute called on unexecutable instance");
        }

        return new CommandResponse(CommandResult.SUCCESS, "Person manager elements:",
                getManager().getAsCollection().toArray(new Person[0]));
    }

    @Override
    public String toString() {
        return "Show";
    }

    @Override
    public String getMan() {
        return "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении";
    }

    @Override
    public boolean isVaildArgument(Object... args) {
        return true;
    }

    @Override
    public Class<?>[] getArgumentClasses() {
        return new Class<?>[0];
    }
}
