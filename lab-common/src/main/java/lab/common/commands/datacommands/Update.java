package lab.common.commands.datacommands;

import java.util.Optional;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.commands.ValidityChecker;
import lab.common.data.Person;

public final class Update extends AbstractDataCommand {

    public Update() {
        super();
    }

    public Update(OwnedDataManager<Person> manager) {
        super(manager);
    }

    @Override
    public CommandResponse execute(User user, Object... args) {
        CommandResponse validy = ValidityChecker.checkValidity(this, args);
        if (validy.getResult() != CommandResult.SUCCESS) {
            return validy;
        }
        Integer id = (Integer) args[0];
        Optional<Person> personToUpdate = getManager().getByID(id);
        if (personToUpdate.isPresent()) {
            DataManagerResponse dataManagerResponse = getManager().updateID(user, personToUpdate.get().getID(),
                    (Person) args[1]);
            if (!dataManagerResponse.isSuccess()) {
                return new CommandResponse(CommandResult.ERROR, dataManagerResponse.getMessage());
            }
            return new CommandResponse(CommandResult.SUCCESS);
        }
        return new CommandResponse(CommandResult.ERROR, String.format("No element with id (%d) is present", id));
    }

    @Override
    public String toString() {
        return "Update";
    }

    @Override
    public String getMan() {
        return "update id {element} : обновить значение элемента коллекции, id которого равен заданному";
    }

    @Override
    public boolean isVaildArgument(Object... args) {
        if (args.length < 2) {
            return false;
        }
        return args[0] instanceof Integer && args[1] instanceof Person;
    }

    @Override
    public Class<?>[] getArgumentClasses() {
        return new Class<?>[] {
                Integer.class, Person.class };
    }
}
