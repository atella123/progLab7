package lab.common.commands;

import lab.common.data.DataManager;
import lab.common.data.Person;
import lab.common.users.User;

public final class Info extends AbstractDataCommand {

    public Info() {
        super();
    }

    public Info(DataManager<Person> manager) {
        super(manager);
    }

    @Override
    public CommandResponse execute(User user, Object... args) {
        if (!isExecutableInstance) {
            return new CommandResponse(CommandResult.ERROR, "Execute called on unexecutable instance");
        }
        return new CommandResponse(CommandResult.SUCCESS, new StringBuilder()
                .append("Collection type: ")
                .append(getManager().getDataSourceType())
                .append("\nInit date: ")
                .append(getManager().getInitDate().toString())
                .append("\nElement count: ")
                .append(getManager().getAsCollection().size())
                .toString());
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
    public boolean isVaildArgument(Object... args) {
        return true;
    }

    @Override
    public Class<?>[] getArgumentClasses() {
        return new Class<?>[0];
    }
}
