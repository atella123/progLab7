package lab.common.commands.datacommands;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.commands.ValidityChecker;
import lab.common.data.Person;

public final class RemoveGreater extends AbstractDataCommand {

    public RemoveGreater() {
        super();
    }

    public RemoveGreater(OwnedDataManager<Person> manager) {
        super(manager);
    }

    @Override
    public CommandResponse execute(User user, Object... args) {
        CommandResponse validy = ValidityChecker.checkValidity(this, args);
        if (validy.getResult() != CommandResult.SUCCESS) {
            return validy;
        }
        Person p = (Person) args[0];
        DataManagerResponse dataManagerResponse = getManager().removeMatches(user, p2 -> p.compareTo(p2) < 0);
        if (!dataManagerResponse.isSuccess()) {
            return new CommandResponse(CommandResult.ERROR, dataManagerResponse.getMessage());
        }
        return new CommandResponse(CommandResult.SUCCESS);
    }

    @Override
    public String toString() {
        return "RemoveGreater";
    }

    @Override
    public String getMan() {
        return "remove_greater {element} : удалить из коллекции все элементы, превышающие заданный";
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
