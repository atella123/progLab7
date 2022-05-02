package lab.common.commands.datacommands;

import java.util.stream.Collectors;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.commands.ValidityChecker;
import lab.common.data.Country;
import lab.common.data.Person;

public final class FilterLessThanNationality extends AbstractDataCommand {

    public FilterLessThanNationality() {
        super();
    }

    public FilterLessThanNationality(OwnedDataManager<Person> manager) {
        super(manager);
    }

    @Override
    public CommandResponse execute(User user, Object... args) {
        CommandResponse validy = ValidityChecker.checkValidity(this, args);
        if (validy.getResult() != CommandResult.SUCCESS) {
            return validy;
        }
        Country country = (Country) args[0];
        return new CommandResponse(CommandResult.SUCCESS,
                getManager().getAsCollection().stream().filter(person -> person.getNationality().compareTo(country) < 0)
                        .map(Object::toString).collect(Collectors.joining("\n")));
    }

    @Override
    public String toString() {
        return "FilterLessThanNationality";
    }

    @Override
    public boolean isVaildArgument(Object... args) {
        return args.length > 0 && args[0] instanceof Country;
    }

    @Override
    public String getMan() {
        return "filter_less_than_nationality nationality : вывести элементы, значение поля nationality которых меньше заданного";
    }

    @Override
    public Class<?>[] getArgumentClasses() {
        return new Class<?>[] {
                Country.class };
    }
}
