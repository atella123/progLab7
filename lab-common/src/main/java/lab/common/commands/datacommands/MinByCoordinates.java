package lab.common.commands.datacommands;

import java.util.Optional;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.data.Person;

public final class MinByCoordinates extends AbstractDataCommand {

    public MinByCoordinates() {
        super();
    }

    public MinByCoordinates(OwnedDataManager<Person> manager) {
        super(manager);
    }

    @Override
    public CommandResponse execute(User user, Object... args) {
        if (!executableInstance) {
            return new CommandResponse(CommandResult.ERROR, "Execute called on unexecutable instance");
        }
        Optional<Person> minPerson = getManager()
                .getAsCollection().stream()
                .min((person1, person2) -> person1.getCoordinates().compareTo(person2.getCoordinates()));
        if (minPerson.isPresent()) {
            return new CommandResponse(CommandResult.SUCCESS, minPerson.get().toString());
        }
        return new CommandResponse(CommandResult.ERROR, "Collection is empty");
    }

    @Override
    public String getMan() {
        return "min_by_coordinates : вывести любой объект из коллекции, значение поля coordinates которого является минимальным";
    }

    @Override
    public String toString() {
        return "MinByCoordinates";
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
