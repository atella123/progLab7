package lab.common.commands.datacommands;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.data.Person;

public final class Info extends AbstractDataCommand {

    public Info() {
        super();
    }

    public Info(OwnedDataManager<Person> manager) {
        super(manager);
    }

    @Override
    public CommandResponse execute(User user, Object... args) {
        if (!executableInstance) {
            return new CommandResponse(CommandResult.ERROR, "Execute called on non executable instance");
        }
        return new CommandResponse(CommandResult.SUCCESS, "Collection type: " +
                getManager().getDataSourceType() +
                "\nInit date: " +
                getManager().getInitDate().toString() +
                "\nElement count: " +
                getManager().getAsCollection().size());
    }

    @Override
    public String toString() {
        return "Info";
    }

    @Override
    public String getMan() {
        return "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)";
    }

    @Override
    public boolean isValidArgument(Object... args) {
        return true;
    }

    @Override
    public Class<?>[] getArgumentClasses() {
        return new Class<?>[0];
    }
}
