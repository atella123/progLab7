package lab.common.commands.datacommands;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.commands.ValidityChecker;
import lab.common.data.Person;

public final class AddIfMax extends AbstractDataCommand {

    public AddIfMax() {
        super();
    }

    public AddIfMax(OwnedDataManager<Person> manager) {
        super(manager);
    }

    @Override
    public CommandResponse execute(User user, Object... args) {
        CommandResponse validy = ValidityChecker.checkValidity(this, args);
        if (validy.getResult() != CommandResult.SUCCESS) {
            return validy;
        }
        Person p = (Person) args[0];
        DataManagerResponse dataManagerResponse = getManager()
                .addIfAllMatches(user, p, p2 -> p2.compareTo(p) < 0);
        if (!dataManagerResponse.isSuccess()) {
            return new CommandResponse(CommandResult.ERROR, dataManagerResponse.getMessage());
        }
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
