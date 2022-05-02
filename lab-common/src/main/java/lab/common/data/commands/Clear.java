package lab.common.data.commands;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.data.DataManagerResponse;
import lab.common.data.OwnedDataManager;
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
        if (!isExecutableInstance) {
            return new CommandResponse(CommandResult.ERROR, "Execute called on unexecutable instance");
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
    public boolean isVaildArgument(Object... args) {
        return true;
    }

    @Override
    public Class<?>[] getArgumentClasses() {
        return new Class<?>[0];
    }
}
