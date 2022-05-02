package lab.common.commands.datacommands;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.data.Person;

public final class RemoveByID extends AbstractDataCommand {

    public RemoveByID() {
        super();
    }

    public RemoveByID(OwnedDataManager<Person> manager) {
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
        Integer id = (Integer) args[0];
        DataManagerResponse dataResp = getManager().removeByID(user, id);
        if (!dataResp.isSuccess()) {
            return new CommandResponse(CommandResult.SUCCESS);
        }
        return new CommandResponse(CommandResult.ERROR, dataResp.getMessage());

    }

    @Override
    public String toString() {
        return "RemoveByID";
    }

    @Override
    public String getMan() {
        return "remove_by_id id : удалить элемент из коллекции по его id";
    }

    @Override
    public boolean isVaildArgument(Object... args) {
        return args.length > 0 && args[0] instanceof Integer;
    }

    @Override
    public Class<?>[] getArgumentClasses() {
        return new Class<?>[] {
                Integer.class };
    }
}