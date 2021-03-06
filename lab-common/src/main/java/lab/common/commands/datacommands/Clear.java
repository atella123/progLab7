package lab.common.commands.datacommands;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.data.Person;

public final class Clear extends AbstractDataCommand {

    public Clear() {
        super();
    }

    public Clear(OwnedDataManager<Person> manager) {
        super(manager);
    }

    @Override
    public CommandResponse execute(User user, Object... args) {
        if (!executableInstance) {
            return new CommandResponse(CommandResult.ERROR, "Execute called on non executable instance");
        }
        DataManagerResponse dataManagerResponse = getManager().clear(user);
        if (!dataManagerResponse.isSuccess()) {
            return new CommandResponse(CommandResult.ERROR, dataManagerResponse.getMessage());
        }
        return new CommandResponse(CommandResult.SUCCESS);
    }

    @Override
    public String toString() {
        return "Clear";
    }

    public String getMan() {
        return "clear : очистить коллекцию";
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
