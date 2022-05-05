package lab.common.commands.datacommands;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.commands.ValidityChecker;
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
        CommandResponse validity = ValidityChecker.checkValidity(this, args);
        if (validity.getResult() != CommandResult.SUCCESS) {
            return validity;
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
    public boolean isValidArgument(Object... args) {
        return args.length > 0 && args[0] instanceof Integer;
    }

    @Override
    public Class<?>[] getArgumentClasses() {
        return new Class<?>[] {
                Integer.class };
    }
}
