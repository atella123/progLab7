package lab.common.commands.datacommands;

import java.util.Map;
import java.util.stream.Collectors;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.data.Person;

public final class GroupCountingByPassportID extends AbstractDataCommand {

    public GroupCountingByPassportID() {
        super();
    }

    public GroupCountingByPassportID(OwnedDataManager<Person> manager) {
        super(manager);
    }

    @Override
    public CommandResponse execute(User user, Object... args) {
        if (!executableInstance) {
            return new CommandResponse(CommandResult.ERROR, "Execute called on unexecutable instance");
        }
        Map<String, Long> groupCounting = getManager().getAsCollection().stream()
                .collect(Collectors.groupingBy(Person::getPassportID, Collectors.counting()));
        return new CommandResponse(CommandResult.SUCCESS,
                groupCounting.entrySet().stream()
                        .map(Object::toString)
                        .collect(Collectors.joining("\n")));
    }

    @Override
    public String toString() {
        return "GroupCountingByPassportID";
    }

    @Override
    public String getMan() {
        return "group_counting_by_passport_id : сгруппировать элементы коллекции по значению поля passportID, вывести количество элементов в каждой группе";
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
